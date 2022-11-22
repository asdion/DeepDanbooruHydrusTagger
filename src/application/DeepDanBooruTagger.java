package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeepDanBooruTagger
{
	String workingDir;
	String taggerCommandPart1ExePath;
	String taggerCommandPart2 = "evaluate";
	String taggerCommandPart3TempImageFolder;
	String taggerCommandPart4 = "--project-path";
	String taggerCommandPart5ProjectDir;
	String taggerCommandPart6 = "--allow-folder";
	String tempImageFolderPath;
	
	public DeepDanBooruTagger(String deepDanBooruWorkingDir, 
			String deepDanBooruEXEPathWithinWorkingDir,
			String deepDanBooruProjectDirWithinWorkingDir, 
			String tempImageFolder)
	{
		workingDir = deepDanBooruWorkingDir;
		taggerCommandPart1ExePath = "\\" + deepDanBooruEXEPathWithinWorkingDir;
		taggerCommandPart3TempImageFolder = "\"" + tempImageFolder + "\"";
		tempImageFolderPath = tempImageFolder;
		taggerCommandPart5ProjectDir = deepDanBooruProjectDirWithinWorkingDir;
	}

	public ArrayList<String> evaluate()
	{
		System.out.println("Evaluation Start");
		ArrayList<String> evaluationOutput = new ArrayList<String>();
		Pattern patternTag = Pattern.compile("^\\(\\d\\.\\d*\\).*", Pattern.CASE_INSENSITIVE);
		try
		{
			ProcessBuilder pb = new ProcessBuilder(
					workingDir + taggerCommandPart1ExePath, 
					taggerCommandPart2,
					taggerCommandPart3TempImageFolder, 
					taggerCommandPart4, 
					taggerCommandPart5ProjectDir,
					taggerCommandPart6);
			pb.redirectErrorStream(true);
			pb.directory(new File(workingDir));
			Process p = pb.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			Instant timerStart = Instant.now();
			Instant timerEnd = Instant.now();
			Instant timerEvaluation = Instant.now();
			Integer counter = 0;
			
			while ((line = in.readLine()) != null)
			{
				Matcher matcherTag = patternTag.matcher(line);
				boolean matchFoundTag = matcherTag.find();
				
				if (line.contains(tempImageFolderPath))
				{
					timerEnd = Instant.now();	
					Long timerState = timerEnd.toEpochMilli() - timerStart.toEpochMilli();
					System.out.println("==== Time per Image: " + timerState + "ms");
					counter++;
					System.out.println("==== Image #" + counter);
					Long seconds = ((timerEnd.toEpochMilli() - timerEvaluation.toEpochMilli()) / 1000L);
					Long minutes = seconds / 60;
					Long hours = minutes / 60;
					seconds = seconds % 60;
					minutes = minutes % 60;
					System.out.println("==== Total Evaluation Time: " + hours + "h " + minutes + "min " + seconds + "sec \n");
					
					timerStart = Instant.now();	
					evaluationOutput.add("{NEWIMAGE}");
					evaluationOutput.add(line);
				}
				if (matchFoundTag)
				{
					evaluationOutput.add(line);
				}
				else
				{
					System.out.println(line);
				}
			}
			p.waitFor(1,TimeUnit.MINUTES);
			in.close();
			in = null;
			evaluationOutput.add("{IMAGEEND}");
			System.out.println("Evaluation End");
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		return evaluationOutput;
	}
}

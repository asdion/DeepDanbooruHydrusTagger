package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
			pb.directory(new File(workingDir));
			Process p = pb.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				Matcher matcherTag = patternTag.matcher(line);
				boolean matchFoundTag = matcherTag.find();
				
				if (line.contains(tempImageFolderPath))
				{
					evaluationOutput.add("{NEWIMAGE}");
					evaluationOutput.add(line);
				}
				if (matchFoundTag)
				{
					evaluationOutput.add(line);
				}
			}
			p.waitFor();
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

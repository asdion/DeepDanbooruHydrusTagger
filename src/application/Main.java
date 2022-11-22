package application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		HashMap<String, String> arguments = new HashMap<String, String>();

		arguments.put("--hydrusIP", "127.0.0.1");
		arguments.put("--hydrusPort", "45869");
		arguments.put("--hydrusAPIKey", "");
		arguments.put("--hydrusFilterTags",
				"\"-special:deepdanboorutagged\",\"system:filetype_=_image/jpg,_image/jpeg,_image/png,_apng\",\"system:limit=512\"");
		arguments.put("--deepDanBooruWorkingDir", "DeepDanbooruTagger");
		arguments.put("--deepDanBooruEXEPathWithinWorkingDir", "venv\\Scripts\\deepdanbooru.exe");
		arguments.put("--deepDanBooruProjectDirWithinWorkingDir", "deepdanbooru\\project");
		arguments.put("--tempImageFolder", "tmpImageFolder");
		arguments.put("--hydrusTagServiceName", "my tags");

		if (args.length == 10)
		{
			for (String arg : args)
			{
				String[] argument = arg.split("<=");
				switch (argument[0])
				{
					case "--hydrusIP":
						arguments.put(argument[0], argument[1]);
						break;
					case "--hydrusPort":
						arguments.put(argument[0], argument[1]);
						break;
					case "--hydrusAPIKey":
						arguments.put(argument[0], argument[1]);
						break;
					case "--hydrusFilterTags":
						arguments.put(argument[0], argument[1]);
						break;
					case "--deepDanBooruWorkingDir":
						arguments.put(argument[0], argument[1]);
						break;
					case "--deepDanBooruEXEPathWithinWorkingDir":
						arguments.put(argument[0], argument[1]);
						break;
					case "--deepDanBooruProjectDirWithinWorkingDir":
						arguments.put(argument[0], argument[1]);
						break;
					case "--tempImageFolder":
						arguments.put(argument[0], argument[1]);
						break;
					case "--hydrusTagServiceName":
						arguments.put(argument[0], argument[1]);
						break;
					case "--clear":
						arguments.put(argument[0], argument[1]);
						break;
				}
			}

			HydrusAccess ha = new HydrusAccess(arguments.get("--hydrusIP"), arguments.get("--hydrusPort"),
					arguments.get("--hydrusAPIKey"), arguments.get("--hydrusFilterTags"),
					arguments.get("--tempImageFolder"), arguments.get("--hydrusTagServiceName"));

			DeepDanBooruTagger ddbt = new DeepDanBooruTagger(arguments.get("--deepDanBooruWorkingDir"),
					arguments.get("--deepDanBooruEXEPathWithinWorkingDir"),
					arguments.get("--deepDanBooruProjectDirWithinWorkingDir"), arguments.get("--tempImageFolder"));

			Boolean loop = true;
			Scanner s = new Scanner(System.in);
			while (loop)
			{
				if (arguments.get("--clear").equals("true"))
				{
					ha.clearTemporaryImageFolder();
					ha.getImageIDS();
					ha.loadImages();
				}
				ha.commitTags(ddbt.evaluate());
				long count = 0;
				try
				{
					count = Files.walk(Paths.get(arguments.get("--tempImageFolder"))).filter(Files::isRegularFile).count();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
				System.out.println("Enter X to evaluate after failure");
				if (count > 2 || s.nextLine().toLowerCase().equals("x"))
				{
					loop = true;
					arguments.put("--clear", "false");
				}
				else
				{
					loop = false;
				}
			}
			s.close();
		}
		else
		{
			System.out.println("arguments are incomplete");
		}
	}
}

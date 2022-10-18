package application;

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

		if (args.length == 9)
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
				ha.clearTemporaryImageFolder();
				ha.getImageIDS();
				ha.loadImages();
				ha.commitTags(ddbt.evaluate());

				System.out.println("Enter X to end the process:");
				if (s.nextLine().toLowerCase().equals("x"))
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

package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HydrusAccess
{
	private String urlElement0_IP;

	private static String urlElement1_SearchFiles = "get_files/search_files?";
	private static String urlElement1_GetFile = "get_files/file?file_id=";
	private static String urlElement1_GetMetadata = "get_files/file_metadata?file_id=";

	private static String urlElement2_GetMetadataLimit = "&only_return_basic_information=true";
	private String urlElement2_APIKey;

	private String urlElement3_Tags;
	private String imageFolderPath;
	private String tagServiceName;
	private String hydrusAPIKey;

	private ArrayList<String> imageIDs;

	public HydrusAccess(String hydrusIP, String hydrusPort, String hydrusAPIKey, String hydrusFilterTags,
			String tempImageFolderPath, String hydrusTagServiceName)
	{
		imageIDs = new ArrayList<String>();
		urlElement0_IP = "http://" + hydrusIP + ":" + hydrusPort + "/";
		urlElement2_APIKey = "Hydrus-Client-API-Access-Key=" + hydrusAPIKey + "&";
		urlElement3_Tags = "tags=[" + hydrusFilterTags + "]";
		imageFolderPath = tempImageFolderPath;
		tagServiceName = hydrusTagServiceName;
		this.hydrusAPIKey = hydrusAPIKey;
	}

	public void getImageIDS()
	{
		URL url;
		try
		{
			url = new URL(urlElement0_IP + urlElement1_SearchFiles + urlElement2_APIKey + urlElement3_Tags);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10000);// 10000 milliseconds = 10 seconds
			conn.setReadTimeout(10000);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null)
			{
				line = line.replace("{\"file_ids\": [", "").replace("]}", "");

				for (String id : line.split(", "))
				{
					imageIDs.add(id);
				}
			}
			reader.close();
			reader = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		url = null;
	}

	public void loadImages()
	{
		Integer counter = 0;
		for (String id : imageIDs)
		{
			URL url;
			String extension = "";
			String hash = "";
			try
			{
				url = new URL(urlElement0_IP + urlElement1_GetMetadata + id + "&" + urlElement2_APIKey
						+ urlElement2_GetMetadataLimit);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(10000);// 10000 milliseconds = 10 seconds
				conn.setReadTimeout(10000);
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null)
				{
					for (String result : line.split(", "))
					{
						if (result.contains("hash"))
						{
							hash = result.replace("\"hash\": \"", "").replace("\"", "");
						}
						if (result.contains("ext"))
						{
							extension = result.replace("\"ext\": \"", "").replace("\"", "");
						}
					}
				}
				reader.close();
				reader = null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			if (!extension.equals(""))
			{
				try
				{
					String imagePath = imageFolderPath + "\\" + hash + extension;
					InputStream in = new URL(urlElement0_IP + urlElement1_GetFile + id + "&" + urlElement2_APIKey)
							.openStream();
					Files.copy(in, Paths.get(imagePath), StandardCopyOption.REPLACE_EXISTING);
					in.close();
					in = null;
					System.out.println("Download Successfull image #" + counter + ": " + imagePath);
					counter++;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void clearTemporaryImageFolder()
	{
		File tempImageDir = new File(imageFolderPath);
		for (File file : tempImageDir.listFiles())
		{
			if (!file.isDirectory())
			{
				file.delete();
			}
		}
		System.out.println("Temporary Image Directory is now empty.");
	}

	public void commitTags(ArrayList<String> evaluationResults)
	{
		ArrayList<ImageTags> imagesTags = new ArrayList<ImageTags>();
		Pattern patternTag = Pattern.compile("^\\(\\d\\.\\d*\\).*", Pattern.CASE_INSENSITIVE);
		ImageTags tempImageTags = null;

		for (String evaluationResult : evaluationResults)
		{
			Matcher matcherTag = patternTag.matcher(evaluationResult);
			boolean matchFoundTag = matcherTag.find();

			if ((evaluationResult.equals("{NEWIMAGE}") && tempImageTags != null) || evaluationResult.equals("{IMAGEEND}"))
			{
				imagesTags.add(tempImageTags);
			}
			else if (!matchFoundTag)
			{
				Integer tmpIndex1 = evaluationResult.indexOf(imageFolderPath);
				Integer tmpIndex2;
				if (tmpIndex1 != -1)
				{
					String tmpEvaluationResult = evaluationResult.substring(tmpIndex1 + imageFolderPath.length() + 1);
					tmpIndex2 = tmpEvaluationResult.indexOf(".");
					String hash = tmpEvaluationResult.substring(0, tmpIndex2);
					tempImageTags = new ImageTags(hash);
				}
			}
			else
			{
				String[] tag = evaluationResult.split(" ", 2);
				tempImageTags.addTag(tag[1].replace("_", " "));
			}
		}
		pushTagsToHydrus(imagesTags);
	}

	private void pushTagsToHydrus(ArrayList<ImageTags> imagesTags)
	{
		for (ImageTags imageTags : imagesTags)
		{
			String urlStr = urlElement0_IP + "add_tags/add_tags";
			String jsonStrPart1 = "{\"hash\":\"";
			String jsonStrPart2Hash = imageTags.getHash();
			String jsonStrPart3 = "\",\"service_names_to_tags\":{\"";
			String jsonStrPart4ServiceName = tagServiceName;
			String jsonStrPart5 = "\": [";
			String jsonStrPart6Tags = "";
			
			ArrayList<String> tags = imageTags.getTags();
			
			for (int i = 0; i < tags.size(); i++)
			{
				if (i == 0)
				{
					jsonStrPart6Tags += "\"";
				}
				else
				{
					jsonStrPart6Tags += ", \"";
				}
				jsonStrPart6Tags += tags.get(i);
				jsonStrPart6Tags += "\"";
			}
			
			String jsonStrPart7 = "]}}";
			String jsonStr = jsonStrPart1 + jsonStrPart2Hash + jsonStrPart3 + jsonStrPart4ServiceName + jsonStrPart5 + jsonStrPart6Tags + jsonStrPart7;

			try
			{
				URL url = new URL(urlStr);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Hydrus-Client-API-Access-Key", hydrusAPIKey);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");
				conn.setDoOutput(true);
				try (OutputStream os = conn.getOutputStream())
				{
					byte[] input = jsonStr.getBytes("utf-8");
					os.write(input, 0, input.length);
					os.close();
				}
				try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8")))
				{
					StringBuilder response = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null)
					{
						response.append(responseLine.trim());
					}
					br.close();
					System.out.println(jsonStrPart2Hash + " Tags were Sent. | " + response.toString());
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}

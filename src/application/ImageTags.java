package application;

import java.util.ArrayList;

public class ImageTags
{
	String imageHash;
	ArrayList<String> tags;
	
	public ImageTags(String hash)
	{
		imageHash = hash;
		tags = new ArrayList<String>();
		tags.add("special:taggedbydeepdanbooru");
	}

	public void addTag(String string)
	{
		tags.add(string);
	}
	
	public String getHash()
	{
		return imageHash;
	}
	
	public ArrayList<String> getTags()
	{
		return tags;
	}
}

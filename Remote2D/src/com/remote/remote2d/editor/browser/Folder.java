package com.remote.remote2d.editor.browser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Folder {
	
	private String folderPath;
	
	ArrayList<File> files;	
	
	public Folder(String path)
	{
		files = new ArrayList<File>();
		setPath(path);
	}
	
	public String getPath()
	{
		return folderPath;
	}
	
	public void setPath(String path)
	{
		folderPath = path;
		
		files.clear();
		
		File[] subFiles = new File(path).listFiles();
		
		Arrays.sort(subFiles,new FileComparator());
		
		for(File f : subFiles)
			files.add(f);
	}
	
	public Folder getParent()
	{
		return new Folder(new File(folderPath).getParent());
	}
	
}

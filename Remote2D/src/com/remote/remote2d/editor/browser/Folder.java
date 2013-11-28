package com.remote.remote2d.editor.browser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.io.R2DFileUtility;

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
		
		File file = R2DFileUtility.getResource(path);
		File[] subFiles = file.listFiles();
		
		if(subFiles != null)
		{
			Arrays.sort(subFiles,new FileComparator());
		
			for(File f : subFiles)
				files.add(f);
		} else
			Log.warn("Can't find folder path: "+path);
	}
	
	public Folder getParent()
	{
		return new Folder(new File(folderPath).getParent());
	}
	
}

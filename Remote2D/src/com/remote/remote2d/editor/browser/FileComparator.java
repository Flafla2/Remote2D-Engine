package com.remote.remote2d.editor.browser;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {
	
	@Override
	public int compare(File arg0, File arg1) {
		if(arg0.isFile() && arg1.isDirectory())
			return 1;
		if(arg0.isDirectory() && arg1.isFile())
			return -1;
		
		return arg0.getName().compareTo(arg1.getName());
	}

}

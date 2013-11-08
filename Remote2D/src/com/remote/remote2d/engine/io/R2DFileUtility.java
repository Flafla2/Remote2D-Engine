package com.remote.remote2d.engine.io;

import java.io.File;

import com.remote.remote2d.engine.Remote2D;

/**
 * A utility class for saving loading, and managing R2D files.
 * 
 * @author Flafla2
 */
public class R2DFileUtility {

	/**
	 * Converts a folder's R2D files from binary to XML.
	 * @param dir Directory to start at in relation to the jar path
	 * @param delete If true, deletes binary files when it is done converting.
	 * @param useLatest If an XML file already exists, use the latest file (if true) and don't convert if necessary.  Otherwise, always convert from binary to XML.
	 * @param recursive If true, recursively searches any subdirectories for files to convert.
	 */
	public static void convertFolderToXML(String dir, boolean delete, boolean useLatest, boolean recursive)
	{
		File file = new File(dir);
		if(!file.exists() || !file.isDirectory())
			return;
		R2DFileFilter filter = new R2DFileFilter();
		for(File f : file.listFiles())
		{
			if(f.isFile() && filter.accept(file, f.getName()) && !f.getName().endsWith(".xml"))
			{
				if(useLatest)
				{
					File possibleCurrent = new File(f.getPath()+".xml");
					if(possibleCurrent.exists() && possibleCurrent.lastModified() > f.lastModified())
							continue;
				}
				
				String localPath = f.toURI().relativize(Remote2D.getJarPath().toURI()).getPath();
				R2DFileManager manager = new R2DFileManager(localPath,null);
				manager.read(false);
				manager.write(true);
				if(delete)
					f.delete();
			} else if(f.isDirectory() && recursive)
				convertFolderToXML(f.toURI().relativize(Remote2D.getJarPath().toURI()).getPath(),delete,useLatest,recursive);
		}
	}
	
	/**
	 * Converts a folder's R2D files from XML to Binary.
	 * @param dir Directory to start at in relation to the jar path
	 * @param delete If true, deletes XML files when it is done converting.
	 * @param useLatest If a binary file already exists, use the latest file (if true) and don't convert if necessary.  Otherwise, always convert from XML to binary.
	 * @param recursive If true, recursively searches any subdirectories for files to convert.
	 */
	public static void convertFolderToBinary(String dir, boolean delete, boolean useLatest, boolean recursive)
	{
		File file = new File(dir);
		if(!file.exists() || !file.isDirectory())
			return;
		R2DFileFilter filter = new R2DFileFilter();
		for(File f : file.listFiles())
		{
			if(f.isFile() && filter.accept(file, f.getName()) && f.getName().endsWith(".xml"))
			{
				if(useLatest)
				{
					File possibleCurrent = new File(f.getPath().substring(0, f.getPath().length()-4));
					if(possibleCurrent.exists() && possibleCurrent.lastModified() > f.lastModified())
							continue;
				}
				
				String localPath = f.toURI().relativize(Remote2D.getJarPath().toURI()).getPath();
				localPath = localPath.substring(0,localPath.length()-4);
				R2DFileManager manager = new R2DFileManager(localPath,null);
				manager.read(true);
				manager.write(false);
				if(delete)
					f.delete();
			} else if(f.isDirectory() && recursive)
				convertFolderToXML(f.toURI().relativize(Remote2D.getJarPath().toURI()).getPath(),delete,useLatest,recursive);
		}
	}

}

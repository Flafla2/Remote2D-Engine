package com.remote.remote2d.engine.io;

import java.io.File;

import com.esotericsoftware.minlog.Log;
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
	 * @param recursive If true, recursively searches any subdirectories for files to convert.
	 */
	public static void convertFolderToXML(String dir, boolean recursive)
	{
		File file = new File(dir);
		if(!file.exists() || !file.isDirectory())
			return;
		R2DFileFilter filter = new R2DFileFilter();
		for(File f : file.listFiles())
		{
			Log.debug(f.isFile()+" "+filter.accept(file, f.getName()));
			if(f.isFile() && filter.accept(file, f.getName()))
			{
				String localPath = Remote2D.getRelativeFile(f).getPath();
				R2DFileManager manager = new R2DFileManager(localPath,null);
				manager.read();
				f.renameTo(new File(f.getAbsolutePath()+".orig"));
				manager.write(true);
			} else if(f.isDirectory() && recursive)
				convertFolderToXML(Remote2D.getRelativeFile(f).getPath(),recursive);
		}
	}
	
	/**
	 * Converts a folder's R2D files from XML to Binary.
	 * @param dir Directory to start at in relation to the jar path
	 * @param recursive If true, recursively searches any subdirectories for files to convert.
	 */
	public static void convertFolderToBinary(String dir, boolean recursive)
	{
		File file = new File(dir);
		if(!file.exists() || !file.isDirectory())
			return;
		R2DFileFilter filter = new R2DFileFilter();
		for(File f : file.listFiles())
		{
			if(f.isFile() && filter.accept(file, f.getName()))
			{
				
				String localPath = Remote2D.getRelativeFile(f).getPath();
				localPath = localPath.substring(0,localPath.length()-4);
				R2DFileManager manager = new R2DFileManager(localPath,null);
				manager.read();
				f.renameTo(new File(f.getAbsolutePath()+".orig"));
				manager.write(false);
			} else if(f.isDirectory() && recursive)
				convertFolderToXML(Remote2D.getRelativeFile(f).getPath(),recursive);
		}
	}
	
	public static boolean textureExists(String s)
	{
		File f = new File(s);

		if(f.exists() && f.isFile() && f.getName().endsWith(".png"))
			return true;
		else
			return false;
	}
	
	public static boolean R2DExists(String s)
	{
		File f = new File(s);
				
		if(new R2DFileFilter().accept(f.getParentFile(), f.getName()) || new R2DFileFilter().accept(f.getParentFile(), f.getName()+".xml"))
			return true;
		else
			return false;
	}

}

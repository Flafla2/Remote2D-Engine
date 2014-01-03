package com.remote.remote2d.engine.io;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.Remote2DException;

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
		File file = R2DFileUtility.getResource(dir);
		if(!file.exists() || !file.isDirectory())
			return;
		R2DFileFilter filter = new R2DFileFilter();
		for(File f : file.listFiles())
		{
			//Log.debug(f.isFile()+" "+filter.accept(file, f.getName()));
			if(f.isFile() && filter.accept(file, f.getName()))
			{
				try
				{
					String localPath = R2DFileUtility.getRelativeFile(f).getPath();
					R2DFileManager manager = new R2DFileManager(localPath,null);
					manager.read();
					f.renameTo(new File(f.getAbsolutePath()+".orig"));
					manager.write(true);
				} catch(IOException e)
				{
					throw new Remote2DException(e);
				}
			} else if(f.isDirectory() && recursive)
				convertFolderToXML(R2DFileUtility.getRelativeFile(f).getPath(),recursive);
		}
	}
	
	/**
	 * Converts a folder's R2D files from XML to Binary.
	 * @param dir Directory to start at in relation to the jar path
	 * @param recursive If true, recursively searches any subdirectories for files to convert.
	 */
	public static void convertFolderToBinary(String dir, boolean recursive)
	{
		File file = R2DFileUtility.getResource(dir);
		if(!file.exists() || !file.isDirectory())
			return;
		R2DFileFilter filter = new R2DFileFilter();
		for(File f : file.listFiles())
		{
			if(f.isFile() && filter.accept(file, f.getName()))
			{
				try
				{
					String localPath = R2DFileUtility.getRelativeFile(f).getPath();
					localPath = localPath.substring(0,localPath.length()-4);
					R2DFileManager manager = new R2DFileManager(localPath,null);
					manager.read();
					f.renameTo(new File(f.getAbsolutePath()+".orig"));
					manager.write(false);
				} catch(IOException e)
				{
					throw new Remote2DException(e);
				}
			} else if(f.isDirectory() && recursive)
				convertFolderToXML(R2DFileUtility.getRelativeFile(f).getPath(),recursive);
		}
	}
	
	/**
	 * Converts a local path to a File.
	 * @param s Path to convert to a file
	 */
	public static File getResource(String s)
	{
		return new File(formatPath(s));
	}
	
	/**
	 * Reformats the given path into a uniform path structure so that equivalent
	 * paths in the file system will still equal each other as a string.
	 * @param s Path to format
	 */
	public static String formatPath(String s)
	{
		s = s.replace('\\', File.separatorChar);
		s = s.replace('/', File.separatorChar);
		
		if(s.startsWith(File.separator))
			s = s.substring(1);
		
		if(s.startsWith("."+File.separator))
			s = s.substring(2);
		return s;
	}
	
	public static boolean textureExists(String s)
	{
		File f = R2DFileUtility.getResource(s);

		if(f.exists() && f.isFile() && f.getName().endsWith(".png"))
			return true;
		else
			return false;
	}
	
	public static boolean R2DExists(String s)
	{
		File f = R2DFileUtility.getResource(s);
				
		if(new R2DFileFilter().accept(f.getParentFile(), f.getName()))
			return true;
		else
			return false;
	}

	public static File getJarPath()
	{
		try {
			File f = new File(Remote2D.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsoluteFile().getParentFile();
			return f;
		} catch (URISyntaxException e) {
			throw new Remote2DException(e);
		}
	}

	public static String getRelativePath(File file, File folder) {
	    String filePath = file.getAbsolutePath();
	    String folderPath = folder.getAbsolutePath();
	    if (filePath.startsWith(folderPath)) {
	        return filePath.substring(folderPath.length() + 1);
	    } else {
	        return null;
	    }
	}
	
	/**
	 * Returns a standard path that will be the same across all platforms.
	 * Useful for IO.
	 * @param path Path to convert to standard
	 */
	public static String getStandardPath(String path)
	{
		path = path.replace('\\', '/');
		if(path.startsWith("/"))
			path = path.substring(1);
		return path;
	}

	/**
	 * Returns a relative file to the jar path, based on an absolute file.
	 * @param absolute An absolute file; in other words a file that is not relative to the jar path.
	 * @return A relative file to the game's jar folder.
	 */
	public static File getRelativeFile(File absolute)
	{
		return getResource(getRelativePath(absolute,getJarPath()));
	}

}

package com.remote.remote2d.engine.art;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.remote.remote2d.engine.io.R2DFileFilter;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.io.R2DFileUtility;
import com.remote.remote2d.engine.io.R2DTypeCollection;

/**
 * Caches all Remote2D Resources so that they don't need to be reloaded all the time
 * 
 * @author Flafla2
 */
public class ResourceLoader {
	
	private static HashMap<String,R2DTypeCollection> r2dCache;
	private static HashMap<String,Texture> textureCache;
	private static ArrayList<String> folders;
	private static R2DFileFilter filter;
	
	static
	{
		r2dCache = new HashMap<String,R2DTypeCollection>();
		textureCache = new HashMap<String,Texture>();
		folders = new ArrayList<String>();
		filter = new R2DFileFilter();
	}
	
	public static Texture getTexture(String path)
	{
		return textureCache.get(R2DFileUtility.formatPath(path));
	}
	
	public static R2DTypeCollection getCollection(String path)
	{
		return r2dCache.get(R2DFileUtility.formatPath(path));
	}
	
	public static boolean isTextureLoaded(String path)
	{
		return textureCache.containsKey(R2DFileUtility.formatPath(path));
	}
	
	public static boolean isR2DLoaded(String path)
	{
		return r2dCache.containsKey(R2DFileUtility.formatPath(path));
	}
	
	/**
	 * Refreshes all resources, starting at the given root folder.  Checks all folders recursively.
	 * @param root The root folder of all game resources, relative to the jar path.
	 */
	public static void refresh(String root)
	{
		r2dCache.clear();
		textureCache.clear();
		folders.clear();
		
		refreshFile(new File(root));
	}
	
	public static void refreshFile(File f)
	{
		if(!f.exists())
			return;
		
		if(f.isFile())
		{
			if(filter.accept(f.getParentFile(), f.getName()))
			{
				R2DFileManager manager = new R2DFileManager(f.getPath(),null);
				manager.read();
				r2dCache.put(R2DFileUtility.formatPath(f.getPath()), manager.getCollection());
			} else if(f.getName().endsWith(".png") || f.getName().endsWith(".jpg") || f.getName().endsWith(".bmp") || f.getName().endsWith(".jpeg") || f.getName().endsWith(".wbmp") || f.getName().endsWith(".jpeg"))
				textureCache.put(R2DFileUtility.formatPath(f.getPath()), new Texture(f.getPath()));
		} else if(f.isDirectory())
		{
			File[] files = f.listFiles();
			for(File sub : files)
				refreshFile(sub);
			folders.add(R2DFileUtility.formatPath(f.getPath()));
		}
	}
	
}

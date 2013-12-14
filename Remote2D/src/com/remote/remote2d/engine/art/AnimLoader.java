package com.remote.remote2d.engine.art;

import java.util.ArrayList;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.io.R2DFileUtility;

/**
 * Extremely simple class that caches animations so they don't
 * need to be reloaded all the time.
 * 
 * @author Flafla2
 */
public class AnimLoader {
	
	private static ArrayList<AnimData> animList;
	
	static
	{
		animList = new ArrayList<AnimData>();
	}
	
	public static Animation getAnimation(String s)
	{
		if(!R2DFileUtility.R2DExists(s))
			return null;
		
		AnimData data = new AnimData();
		data.path = R2DFileUtility.formatPath(s);
		
		if(!animList.contains(data))
		{
			Animation animation = new Animation(R2DFileUtility.formatPath(s));
			data.animation = animation;
			animList.add(data);
			Log.debug("New animation added to list: "+R2DFileUtility.formatPath(s)+"; size="+animList.size());
		}
		return animList.get(animList.indexOf(data)).animation;
	}
	
	private static class AnimData
	{
		public Animation animation;
		public String path;
		
		public boolean equals(Object o)
		{
			if(!(o instanceof AnimData))
				return false;
			
			return path.equals(((AnimData)o).path);
		}
	}
	
}

package com.remote.remote2d.engine.art;

import java.util.HashMap;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.io.R2DFileUtility;

/**
 * Extremely simple class that caches animations so they don't
 * need to be reloaded all the time.
 * 
 * @author Flafla2
 */
public class AnimLoader {
	
	private static HashMap<String,Animation> animList;
	
	static
	{
		animList = new HashMap<String,Animation>();
	}
	
	public static Animation getAnimation(String s)
	{
		if(!R2DFileUtility.R2DExists(s))
			return null;
		if(!animList.containsKey(s))
		{
			Log.debug("New animation added to list: "+s);
			Animation animation = new Animation(s);
			animList.put(s, animation);
		}
		return animList.get(s);
	}
	
}

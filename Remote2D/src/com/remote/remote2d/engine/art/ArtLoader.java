package com.remote.remote2d.engine.art;

import java.util.HashMap;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.io.R2DFileUtility;

/**
 * Loads textures and animations for you, and also caches animations in case
 * it is used a lot.
 * 
 * @author Flafla2
 */
public class ArtLoader {
	
	private HashMap<String,Animation> animList;
	
	public ArtLoader()
	{
		animList = new HashMap<String,Animation>();
	}
	
	public Animation getAnimation(String s)
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

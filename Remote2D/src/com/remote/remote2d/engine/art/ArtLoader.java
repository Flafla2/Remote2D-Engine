package com.remote.remote2d.engine.art;

import java.io.File;
import java.util.HashMap;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.particles.ParticleSystem;
import com.remote.remote2d.engine.world.Map;

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
		if(!R2DExists(s))
			return null;
		if(!animList.containsKey(s))
		{
			Log.debug("New animation added to list: "+s);
			Animation animation = new Animation(s);
			animList.put(s, animation);
		}
		return animList.get(s);
	}
	
	public boolean textureExists(String s)
	{
		String x = Remote2D.getJarPath().getPath().toString()+s;
		File f = new File(x);

		if(f.exists() && f.isFile() && f.getName().endsWith(".png"))
			return true;
		else
			return false;
	}
	
	public boolean R2DExists(String s)
	{
		String x = Remote2D.getJarPath().getPath().toString()+s;
		File f = new File(x);

		if(f.exists() && f.isFile() && (f.getName().endsWith(Entity.getExtension()) || f.getName().endsWith(Animation.getExtension()) || f.getName().endsWith(ParticleSystem.getExtension()) || f.getName().endsWith(Map.getExtension())))
			return true;
		else
			return false;
	}
	
}

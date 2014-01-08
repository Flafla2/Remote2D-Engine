package com.remote.remote2d.engine.logic;

public class CollisionResponderBroad {
	
	public static boolean doesCollide(Collider c1, Collider c2)
	{
		c1.updateVerts();
		c2.updateVerts();
		
		Vector2 vec = c1.getCenter().subtract(c2.getCenter()).abs();
		float distanceSquared = vec.x*vec.x+vec.y*vec.y;
		float addedDistance = c1.getBroadRadius()+c2.getBroadRadius();
		if(distanceSquared <= addedDistance*addedDistance)
			return true;
		return false;
	}
	
}

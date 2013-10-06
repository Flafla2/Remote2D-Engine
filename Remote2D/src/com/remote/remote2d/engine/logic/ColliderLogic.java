package com.remote.remote2d.engine.logic;

/**
 * A utility class that detects and handles collisions with the Collider class.
 * @author Flafla2
 *
 */
public class ColliderLogic {
	
	public static float getDistBetweenPoints(float x1, float y1, float x2, float y2)
	{
		return (float)Math.sqrt(Math.pow((x1-x2),2)+Math.pow((y1-y2),2));
	}
	
	public static Collider setColliderPos(Collider c, Vector2 pos)
	{
		if(c instanceof ColliderBox)
		{
			ColliderBox coll = (ColliderBox)c;
			return new ColliderBox(pos,coll.getDim());
		} else if(c instanceof ColliderSphere)
		{
			ColliderSphere coll = (ColliderSphere)c;
			return new ColliderSphere(pos,coll.radius);
		}
		return c;
	}
	
}

package com.remote.remote2d.engine.logic;

public abstract class Collider {
	
	public boolean isIdle = true;
	public Vector2[] verts;
	
	public Collider(Vector2[] verts)
	{
		this.verts = verts;
	}
	
	public Collider()
	{
		
	}
	
	public static Collision getCollision(Collider stationary, Collider responding)
	{
		boolean broad = CollisionResponderBroad.doesCollide(stationary, responding);
		if(!broad)
		{
			Collision collision = new Collision();
			collision.finishedCalculating = true;
			return collision;
		}
		
		return CollisionResponderNarrow.getCollision(stationary, responding);
	}
	
	public boolean isEqual(Collider c)
	{
		if(c == null)
			return false;
		c.updateVerts();
		updateVerts();
		if(c.verts.length != verts.length)
			return false;
		
		for(int x=0;x<verts.length;x++)
		{
			if(verts[x].x != c.verts[x].x || verts[x].y != c.verts[x].y)
				return false;
		}
		
		return true;
	}
	
	public float getBroadRadius()
	{
		float distSquared = 0;
		Vector2 center = getCenter();
		for(int x=0;x<verts.length;x++)
		{
			Vector2 localPoint = verts[x].subtract(center);
			localPoint = localPoint.multiply(localPoint); // Square it
			if(localPoint.x + localPoint.y > distSquared)
				distSquared = localPoint.x + localPoint.y;
		}
		return (float) Math.sqrt(distSquared);
	}
	
	public abstract void updateVerts();
	public abstract boolean isPointInside(Vector2 vec);
	public abstract Collider getTransformedCollider(Vector2 trans);
	public abstract Vector2 getCenter();
	public abstract void drawCollider(int color);
	
}

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
	
	public static boolean collides(Collider col1, Collider col2)
	{
		if(col1 instanceof ColliderBox && col2 instanceof ColliderBox)
		{
			ColliderBox c1 = (ColliderBox)col1;
			ColliderBox c2 = (ColliderBox)col2;
			boolean collides1 = c1.isPointInside(c2.pos) || 
					c1.isPointInside(new Vector2(c2.pos.x+c2.dim.x,c2.pos.y)) || 
					c1.isPointInside(c2.pos.add(c2.dim)) || 
					c1.isPointInside(new Vector2(c2.pos.x,c2.pos.y+c2.dim.y));
			if(collides1) return true; // If true we don't need to calculate collides2
			boolean collides2 = c2.isPointInside(c1.pos) || 
					c2.isPointInside(new Vector2(c1.pos.x+c1.dim.x,c1.pos.y)) || 
					c2.isPointInside(c1.pos.add(c1.dim)) || 
					c2.isPointInside(new Vector2(c1.pos.x,c1.pos.y+c1.dim.y));
			return collides2;
		} else if(col1 instanceof ColliderSphere && col2 instanceof ColliderSphere)
		{
			ColliderSphere c1 = (ColliderSphere)col1;
			ColliderSphere c2 = (ColliderSphere)col2;
			Vector2 dist = c2.pos.subtract(c1.pos).abs();
			float distSquared = dist.x*dist.x+dist.y*dist.y;
			float radSquared = (float) Math.pow(c1.radius+c2.radius,2);
			if(distSquared <= radSquared)
				return true;
			return false;
		}
		return getCollision(col1,col2).collides;
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

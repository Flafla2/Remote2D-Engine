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
	
	public Collision getCollision(Collider other, Vector2 movement)
	{
		Collider moved = this.getTransformedCollider(movement);
		Collision collision = new Collision();
		collision.idle = other;
		collision.moving = this;
		collision.movementVector = movement.normalize();
		updateVerts();
		for(int j = moved.verts.length-1, i = 0; i < moved.verts.length; j = i, i++)
		{
			Vector2 v0 = verts[j];
			Vector2 v1 = verts[i];
			Vector2 edge = new Vector2(0,0);
			edge.x = v1.x - v0.x; // edge
			edge.y = v1.y - v0.y; // edge
		  
			Vector2 axis = edge.perp(); // Separate axis is perpendicular to the edge
			if(calculateCollisionInfo(axis, moved, other, collision))
			{
				if(!collision.collides)
				{
					collision.correction = new Vector2(0,0);
					collision.lengthSquared = 0;
				}
				return collision;
			}
		}
		
		for(int j = other.verts.length-1, i = 0; i < other.verts.length; j = i, i++)
		{
			Vector2 v0 = other.verts[j];
			Vector2 v1 = other.verts[i];
			Vector2 edge2 = new Vector2(0,0);
			edge2.x = v1.x - v0.x; // edge
			edge2.y = v1.y - v0.y; // edge
		  
			Vector2 axis = edge2.perp(); // Separate axis is perpendicular to the edge
			if(calculateCollisionInfo(axis, moved, other, collision))
			{
				if(!collision.collides)
				{
					collision.correction = new Vector2(0,0);
					collision.lengthSquared = 0;
				}
				return collision;
			}
		}
		
		collision.collides = true;
		return collision;
	}
	
	protected boolean doesCollideOnAxis(Collider moved, Collider poly2, Vector2 axis)
	{
		int[] thisMinMax = moved.calculateInterval(axis);
		int mina = thisMinMax[0];
		int maxa = thisMinMax[1];
		int[] otherMinMax = poly2.calculateInterval(axis);
		int minb = otherMinMax[0];
		int maxb = otherMinMax[1];
		
		double d0 = maxb-mina;
		double d1 = minb-maxa;
		
		if(d0 < 0.0 || d1 > 0.0)
		{
			return true;
		}
		return false;
	}
	
	protected boolean calculateCollisionInfo(Vector2 axis, Collider moved, Collider poly2, Collision info) {
		int[] thisMinMax = moved.calculateInterval(axis);
		int mina = thisMinMax[0];
		int maxa = thisMinMax[1];
		int[] otherMinMax = poly2.calculateInterval(axis);
		int minb = otherMinMax[0];
		int maxb = otherMinMax[1];
		
		double d0 = maxb-mina;
		double d1 = minb-maxa;
		
		if(d0 < 0.0 || d1 > 0.0)
		{
			info.collides = false;
			return true;
		}	
		
		// find out if overlap on the right or left of the polygon.
		double overlap = (d0 < -d1)? d0 : d1;
		
		if(mina < maxb && (maxb-mina < info.max-info.min || info.max < 0 || info.min < 0))
		{
			info.min = mina;
			info.max = maxb;
		} else if(mina >= maxb && (maxa-minb < info.max-info.min || info.max < 0 || info.min < 0))
		{
			info.min = minb;
			info.max = maxa;
		}
			

		// the axis length squared
		double axis_length_squared = axis.dot(axis);
		assert(axis_length_squared > 0.00001);

		// the mtd vector for that axis
		Vector2 sep = new Vector2(0,0); 
		sep.x = (float)(axis.x * (overlap / axis_length_squared));
		sep.y = (float)(axis.y * (overlap / axis_length_squared));
		

		// the mtd vector length squared.
		double sep_length_squared = sep.dot(sep);
		
		// if that vector is smaller than our computed MTD (or the mtd hasn't been computed yet)
		// use that vector as our current mtd.
		boolean favorable = sep_length_squared < info.lengthSquared;
		boolean lastPreferred = getOpposites(info.correction.x,info.movementVector.x) && getOpposites(info.correction.y,info.movementVector.y) && info.lengthSquared >= 0.0;
		boolean preferred = getOpposites(sep.x,info.movementVector.x) && getOpposites(sep.y,info.movementVector.y);
		if((favorable && preferred) || info.lengthSquared < 0.0 || (!lastPreferred && preferred))
		{
			info.lengthSquared = sep_length_squared;
			info.correction = sep;
		}
		return false;
		
	}
	
	protected boolean getOpposites(float x, float y)
	{
		boolean opposite = (x>0 && y<0) || (x<0 && y>0) || x==0 || y==0;
		return opposite;
	}
	
	protected int[] calculateInterval(Vector2 axis)
	{
		return calculateInterval(verts,axis);
	}
		
	protected int[] calculateInterval(Vector2[] verts, Vector2 axis) {
		int min;
		int max;
		min = max = (int)verts[0].dot(axis);
		for (int i = 1; i < verts.length; i++) {
			int d = (int)verts[i].dot(axis);
			if (d < min)
				min = d;
			else if (d > max)
				max = d;
		}
		int[] interval = {min,max};
		return interval;
	}
	
	protected boolean intervalsSeparated(float mina, float maxa, float minb, float maxb) {
		return (mina > maxb) || (minb > maxa);
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
	
	public abstract void updateVerts();
	public abstract boolean isPointInside(Vector2 vec);
	public abstract Collider getTransformedCollider(Vector2 trans);
	public abstract void drawCollider(int color);
	
}

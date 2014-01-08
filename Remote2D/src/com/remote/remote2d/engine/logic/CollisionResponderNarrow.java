package com.remote.remote2d.engine.logic;

public class CollisionResponderNarrow {

	public static Collision getCollision(Collider stationary, Collider responding)
	{
		Collision collision = new Collision();
		responding.updateVerts();
		for(int j = responding.verts.length-1, i = 0; i < responding.verts.length; j = i, i++)
		{
			Vector2 v0 = responding.verts[j];
			Vector2 v1 = responding.verts[i];
			Vector2 edge = new Vector2(0,0);
			edge.x = v1.x - v0.x; // edge
			edge.y = v1.y - v0.y; // edge
		  
			Vector2 axis = edge.perp(); // Separate axis is perpendicular to the edge
			calculateCollisionInfo(axis, responding, stationary, collision);
			if(collision.finishedCalculating)
			{
				if(!collision.collides)
				{
					collision.correction = new Vector2(0,0);
					collision.lengthSquared = 0;
				}
				return collision;
			}
		}
		
		for(int j = stationary.verts.length-1, i = 0; i < stationary.verts.length; j = i, i++)
		{
			Vector2 v0 = stationary.verts[j];
			Vector2 v1 = stationary.verts[i];
			Vector2 edge2 = new Vector2(0,0);
			edge2.x = v1.x - v0.x; // edge
			edge2.y = v1.y - v0.y; // edge
		  
			Vector2 axis = edge2.perp(); // Separate axis is perpendicular to the edge
			calculateCollisionInfo(axis, responding, stationary, collision);
			if(collision.finishedCalculating)
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
	
	protected static void calculateCollisionInfo(Vector2 axis, Collider moved, Collider poly2, Collision info) {
		int[] thisMinMax = calculateInterval(moved.verts,axis);
		int mina = thisMinMax[0];
		int maxa = thisMinMax[1];
		int[] otherMinMax = calculateInterval(poly2.verts,axis);
		int minb = otherMinMax[0];
		int maxb = otherMinMax[1];
		
		double d0 = maxb-mina;
		double d1 = minb-maxa;
		
		if(d0 < 0.0 || d1 > 0.0)
		{
			info.collides = false;
			info.finishedCalculating = true;
			return;
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
		if(favorable || info.lengthSquared < 0.0)
		{
			info.lengthSquared = sep_length_squared;
			info.correction = sep;
		}
		return;
		
	}
	
	protected static boolean getOpposites(float x, float y)
	{
		boolean opposite = (x>0 && y<0) || (x<0 && y>0) || x==0 || y==0;
		return opposite;
	}
		
	protected static int[] calculateInterval(Vector2[] verts, Vector2 axis) {
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
	
	protected static boolean intervalsSeparated(float mina, float maxa, float minb, float maxb) {
		return (mina > maxb) || (minb > maxa);
	}

}

package com.remote.remote2d.engine.logic;

import org.lwjgl.opengl.GL11;

import com.remote.remote2d.engine.art.Renderer;

public class ColliderBox extends Collider{
	
	/**
	 * A simple Axis-Aligned Box Collider
	 */
	
	public Vector2 pos;
	public Vector2 dim;
	
	public ColliderBox(Vector2 pos, Vector2 size)
	{
		this.pos = pos;
		this.dim = size;
		updateVerts();
	}
	
	public Vector2 getPos()
	{
		Vector2 pos = new Vector2(this.pos.x,this.pos.y);
		
		if(this.dim.x < 0)
		{
			pos.x += dim.x;
		}
		
		if(this.dim.y < 0)
		{
			pos.y += dim.y;
		}
		
		return pos;
	}
	
	public Vector2 getDim()
	{
		Vector2 dim = new Vector2(this.dim.x,this.dim.y);
		
		if(this.dim.x < 0)
		{
			dim.x = Math.abs(this.dim.x);
		}
		
		if(this.dim.y < 0)
		{
			dim.y = Math.abs(this.dim.y);
		}
		
		return dim;
	}

	@Override
	public boolean isPointInside(Vector2 vec) {
		
		Vector2 pos = getPos();
		Vector2 dim = getDim();
		
		return vec.x >= pos.x && vec.y >= pos.y && vec.x <= pos.x+dim.x && vec.y <= pos.y+dim.y;
		
	}

	@Override
	public void drawCollider(int color) {
		Vector2 pos = getPos();
		Vector2 dim = getDim();
		
		Renderer.drawLineRect(pos, dim, color, 1.0f);
	}

	@Override
	public Collider getTransformedCollider(Vector2 trans) {
		ColliderBox box = new ColliderBox(pos.add(trans),dim);
		return box;
	}

	@Override
	public void updateVerts() {
		verts = new Vector2[4];
		verts[0] = new Vector2(pos.x,pos.y);
		verts[1] = new Vector2(pos.x+dim.x,pos.y);
		verts[2] = new Vector2(pos.x+dim.x,pos.y+dim.y);
		verts[3] = new Vector2(pos.x,pos.y+dim.y);
	}
}

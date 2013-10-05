package com.remote.remote2d.engine.logic;

import com.remote.remote2d.engine.art.Renderer;

public class ColliderGeneric extends Collider {

	public ColliderGeneric(Vector2[] newVerts) {
		this.verts = newVerts;
	}

	@Override
	public void updateVerts() {
	}

	@Override
	public boolean isPointInside(Vector2 vec) {
		return false;
	}

	@Override
	public Collider getTransformedCollider(Vector2 trans) {
		Vector2[] newVerts = new Vector2[verts.length];
		for(int x=0;x<verts.length;x++)
		{
			newVerts[x] = new Vector2(verts[x].x+trans.x,verts[x].y+trans.y);
		}
		return new ColliderGeneric(newVerts);
	}

	@Override
	public void drawCollider(int color) {
		Renderer.drawLinePoly(verts, color, 1.0f);
	}

}

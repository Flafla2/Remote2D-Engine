package com.remote.remote2d.engine.logic;

import com.remote.remote2d.engine.art.Renderer;

public class ColliderGeneric extends Collider {
	
	private Vector2 computedCenter = new Vector2(0,0);

	public ColliderGeneric(Vector2[] newVerts) {
		this.verts = newVerts;
	}

	@Override
	public void updateVerts() {
		computedCenter.y = 0;
		computedCenter.x = 0;
		
		for(Vector2 vec : verts)
			computedCenter = computedCenter.add(vec);
		computedCenter = computedCenter.divide(new Vector2(2));
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

	@Override
	public Vector2 getCenter() {
		return computedCenter;
	}

}

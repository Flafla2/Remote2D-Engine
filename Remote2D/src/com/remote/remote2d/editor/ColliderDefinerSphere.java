package com.remote.remote2d.editor;

import com.remote.remote2d.engine.logic.Collider;
import com.remote.remote2d.engine.logic.ColliderSphere;
import com.remote.remote2d.engine.logic.Vector2;

public class ColliderDefinerSphere extends ColliderDefiner {
	
	Vector2 origin;
	float radius = -1;

	@Override
	public void click() {
		if(origin == null)
			origin = hover.copy();
		else if(radius == -1)
		{
			float a = Math.abs(hover.x-origin.x);
			float b = Math.abs(hover.y-origin.y);
			radius = (float) Math.sqrt(a*a+b*b);
		}
	}

	@Override
	public boolean isDefined() {
		return origin != null && radius != -1;
	}

	@Override
	public Collider getCollider() {
		if(hover == null || origin == null)
			return null;
		float a = Math.abs(hover.x-origin.x);
		float b = Math.abs(hover.y-origin.y);
		return isDefined() ? new ColliderSphere(new Vector2(origin.getElements()),(float)radius) : new ColliderSphere(new Vector2(origin.getElements()),(int)Math.sqrt(a*a+b*b));
	}

}

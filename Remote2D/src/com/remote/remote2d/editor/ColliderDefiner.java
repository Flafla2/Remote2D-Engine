package com.remote.remote2d.editor;

import com.remote.remote2d.engine.logic.Collider;
import com.remote.remote2d.engine.logic.Vector2;

public abstract class ColliderDefiner {
	
	protected Vector2 hover;
	
	public abstract void click();
	public abstract boolean isDefined();
	public abstract Collider getCollider();
	
	public void hover(float f, float g)
	{
		hover = new Vector2(f,g);
	}
}

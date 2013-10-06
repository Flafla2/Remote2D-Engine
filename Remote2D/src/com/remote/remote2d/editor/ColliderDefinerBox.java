package com.remote.remote2d.editor;

import com.remote.remote2d.engine.logic.Collider;
import com.remote.remote2d.engine.logic.Vector2;

public class ColliderDefinerBox extends ColliderDefiner {
	
	private Vector2 point1 = new Vector2(-1,-1);
	private Vector2 point2 = new Vector2(-1,-1);

	@Override
	public void click() {
		if(point1.x == -1)
			point1 = new Vector2(hover.x,hover.y);
		else if(point2.x == -1)
			point2 = new Vector2(hover.x,hover.y);
	}

	@Override
	public boolean isDefined() {
		return point1.x != -1 && point2.x != -1;
	}

	@Override
	public Collider getCollider() {
		if(hover == null)
			hover = new Vector2(0,0);
		Vector2 point1 = this.point1.x==-1 ? hover : this.point1;
		Vector2 point2 = this.point2.x==-1 ? hover : this.point2;
		return point1.getColliderWithDim(point2.subtract(point1));
	}

}

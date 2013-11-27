package com.remote.remote2d.extras.test.entity;

import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.entity.component.Component;
import com.remote.remote2d.engine.logic.Vector2;

public class ComponentEntityPointerTest extends Component {
	
	public Entity testEntity;

	@Override
	public void tick(int i, int j, int k) {
		
	}

	@Override
	public void renderBefore(boolean editor, float interpolation) {
		
	}

	@Override
	public void onEntitySpawn() {
		
	}

	@Override
	public void renderAfter(boolean editor, float interpolation) {
		Vector2 pos = entity.getPosGlobal(interpolation);
		if(entity != null)
			Fonts.get("Arial").drawString(testEntity.name, pos.x, pos.y, 20, 0x000000);
	}

	@Override
	public void init() {
		
	}

	@Override
	public void apply() {
		
	}

}

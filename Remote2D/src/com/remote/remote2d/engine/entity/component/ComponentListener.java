package com.remote.remote2d.engine.entity.component;

import com.remote.remote2d.engine.AudioHandler;

public class ComponentListener extends Component {

	@Override
	public void tick(int i, int j, int k) {
		AudioHandler.setListenerPos(entity.pos);
	}

	@Override
	public void renderBefore(boolean editor, float interpolation) {
		
	}

	@Override
	public void onEntitySpawn() {
		
	}

	@Override
	public void renderAfter(boolean editor, float interpolation) {
		
	}

	@Override
	public void init() {
		
	}

	@Override
	public void apply() {
		
	}

}

package com.remote.remote2d.engine.entity.component;

import com.remote.remote2d.engine.DisplayHandler;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.logic.Vector2;

/**
 * Simple built-in component that sets the camera based on position.
 * 
 * @author Flafla2
 */
public class ComponentCamera extends Component {

	@Override
	public void tick(int i, int j, int k) {
		entity.getMap().camera.pos = entity.getPosGlobal();
	}

	@Override
	public void renderBefore(boolean editor, float interpolation) {
		
		if(editor)
		{
			Renderer.drawRect(entity.getPosGlobal(), new Vector2(Fonts.get("Arial").getStringDim("CAMERA", 20)[0]+10,20), 1, 1, 0, 1);
			Fonts.get("Arial").drawString("CAMERA", entity.pos.x+5, entity.pos.y, 20, 0x000000);
		}
		
	}

	@Override
	public void onEntitySpawn() {
		entity.getMap().camera.pos = entity.getPosGlobal();
		entity.getMap().camera.updatePos();
	}

	@Override
	public void renderAfter(boolean editor, float interpolation) {
		if(editor)
		{
			Vector2 entityPos = entity.getPosGlobal(interpolation);
			Vector2 dim = DisplayHandler.getDefaultDimensions();
			Renderer.drawLineRect(new Vector2(entityPos.x-dim.x/2,entityPos.y-dim.y/2), dim, 0, 0, 1, 1);
		}
	}

	@Override
	public void apply() {
		
		
		
	}

	@Override
	public void init() {
		
	}

}

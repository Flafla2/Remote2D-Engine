package com.remote.remote2d.engine.entity.component;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Camera;

/**
 * Simple built-in component that sets the camera based on position.
 * 
 * @author Flafla2
 */
public class ComponentCamera extends Component {

	@Override
	public void tick(int i, int j, int k) {
		
	}

	@Override
	public void renderBefore(boolean editor, float interpolation) {
		
		if(editor)
		{
			Renderer.drawRect(entity.pos, new Vector2(Fonts.get("Arial").getStringDim("CAMERA", 20)[0]+10,20), 1, 1, 0, 1);
			Fonts.get("Arial").drawString("CAMERA", entity.pos.x+5, entity.pos.y, 20, 0x000000);
		}
		
	}

	@Override
	public void onEntitySpawn() {
		Camera camera = new Camera(entity.pos);
		entity.getMap().camera = camera;
	}

	@Override
	public void renderAfter(boolean editor, float interpolation) {
		if(editor)
		{
			Vector2 dim = Remote2D.displayHandler.getDefaultDimensions();
			Renderer.drawLineRect(new Vector2(entity.pos.x-dim.x/2,entity.pos.y-dim.y/2), dim, 0, 0, 1, 1);
		}
	}

	@Override
	public void apply() {
		
		
		
	}

	@Override
	public void init() {
		
	}

}

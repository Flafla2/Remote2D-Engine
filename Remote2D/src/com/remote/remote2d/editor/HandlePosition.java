package com.remote.remote2d.editor;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Map;

public class HandlePosition extends Handle {
	
	private Vector2 pos;
	private int clickMode = 0;//0=nothing,1=x,2=y,3=both
	private Vector2 clickOffset;//Position of mouse on initial click RELATIVE to the original position
	private static final int longDim = 100;
	private static final int shortDim = 10;
	private static final int tipDim = 15;

	public HandlePosition(GuiEditor editor, String uuid) {
		super(editor,uuid);
	}

	@Override
	public void tick(int i, int j, int k) {
		if(getEntityUUID() == null)
		{
			pos = null;
			return;
		}
		Map map = editor.getMap();
		if(map == null)
		{
			pos = null;
			return;
		}
		Entity entity = map.getEntityList().getEntityWithUUID(getEntityUUID());
		if(entity == null)
		{
			pos = null;
			return;
		}
		
		pos = map.worldToScreenCoords(entity.getPosGlobal());
		
		if(Remote2D.hasMouseBeenReleased())
		{
			clickOffset = null;
			clickMode = 0;
		}
		
		ColliderBox xAxis = pos.add(new Vector2(10,-tipDim/2)).getColliderWithDim(new Vector2(longDim,tipDim));
		ColliderBox yAxis = pos.add(new Vector2(-tipDim/2,10)).getColliderWithDim(new Vector2(tipDim,longDim));
		ColliderBox bothAxis = pos.add(new Vector2(-10)).getColliderWithDim(new Vector2(20));
		Vector2 mouse = new Vector2(i,j);
		if(Remote2D.hasMouseBeenPressed())
		{
			if(xAxis.isPointInside(mouse))
			{
				clickOffset = new Vector2(i,j).subtract(pos);
				clickMode = 1;
			} else if(yAxis.isPointInside(mouse))
			{
				clickOffset = new Vector2(i,j).subtract(pos);
				clickMode = 2;
			} else if(bothAxis.isPointInside(mouse))
			{
				clickOffset = new Vector2(i,j).subtract(pos);
				clickMode = 3;
			}
		}
		
		if(clickMode == 0)
			return;
		
		Vector2 scrToWorld = map.screenToWorldCoords(mouse.subtract(clickOffset));
		if(editor.grid)
		{
			scrToWorld = scrToWorld.add(new Vector2(editor.getMap().gridSize/2));
			scrToWorld = scrToWorld.subtract(scrToWorld.mod(editor.getMap().gridSize));
		}
			
		if(clickMode == 1 || clickMode == 3)
			entity.pos.x = scrToWorld.x;
		if(clickMode == 2 || clickMode == 3)
			entity.pos.y = scrToWorld.y;
		
		entity.updatePos();
		
		pos = map.worldToScreenCoords(entity.getPosGlobal());
		editor.getInspector().setCurrentEntity(entity);
	}

	@Override
	public void render(float interpolation) {
		if(pos == null)
			return;
		Renderer.drawArrow(pos.add(new Vector2(10,0)), longDim, shortDim, 0, new Vector2(tipDim), 0xff0000, clickMode == 1 ? 1 : 0.6f);
		Renderer.drawArrow(pos.add(new Vector2(0,10)), longDim, shortDim, 90, new Vector2(tipDim), 0x00ff00, clickMode == 2 ? 1 : 0.6f);
		Renderer.drawRect(pos.subtract(new Vector2(10)), new Vector2(20), 0x000000, clickMode == 3 ? 1 : 0.6f);
	}

	@Override
	public boolean isSelected() {
		return clickOffset != null;
	}
	

}

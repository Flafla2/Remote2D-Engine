package com.remote.remote2d.editor;

import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.gui.Gui;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorPreview extends Gui {
	
	private GuiEditor editor;
	
	public Vector2 pos;
	public Vector2 dim;
	
	public GuiEditorPreview(GuiEditor editor, Vector2 pos, Vector2 dim)
	{
		this.pos = pos;
		this.dim = dim;
		this.editor = editor;
	}

	@Override
	public void tick(int i, int j, int k) {
		
	}

	@Override
	public void render(float interpolation) {
		
		Renderer.drawRect(pos, dim, 0x000000, 0.5f);
		
		Fonts.get("Arial").drawString("Preview", pos.x, pos.y, 20, 0xffffff);
		Renderer.drawLine(new Vector2(pos.x, pos.y+20),new Vector2(pos.x+dim.x, pos.y+20),0xffffff,1.0f);
		
		Renderer.startScissor(new Vector2(pos.x,pos.y+21), dim);
		
		String currentEntity = editor.getInspector().currentEntity;
		if(currentEntity != null)
		{
			Entity e = editor.getMap().getEntityList().getEntityWithUUID(currentEntity);
			Renderer.pushMatrix();
				Renderer.translate(new Vector2(pos.x+dim.x/2-e.getDim().x/2, pos.y+dim.y/2-e.getDim().y/2));
				e.renderPreview(interpolation);
			Renderer.popMatrix();
		}
		
		Renderer.endScissor();
		
	}

}

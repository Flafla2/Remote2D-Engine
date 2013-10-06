package com.remote.remote2d.editor;

import org.lwjgl.opengl.GL11;

import com.remote.remote2d.editor.inspector.GuiEditorInspector;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.gui.Gui;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorPreview extends Gui {
	
	private GuiEditorInspector inspector;
	
	public Vector2 pos;
	public Vector2 dim;
	
	public GuiEditorPreview(GuiEditorInspector inspector, Vector2 pos, Vector2 dim)
	{
		this.pos = pos;
		this.dim = dim;
		this.inspector = inspector;
	}

	@Override
	public void tick(int i, int j, int k) {
		
	}

	@Override
	public void render(float interpolation) {
		
		Renderer.drawRect(pos, dim, 0x000000, 0.5f);
		
		Fonts.get("Arial").drawString("Preview", pos.x, pos.y, 20, 0xffffff);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2f(pos.x, pos.y+20);
		GL11.glVertex2f(pos.x+dim.x, pos.y+20);
		GL11.glEnd();
		
		Renderer.startScissor(new Vector2(pos.x,pos.y+21), dim);
		
		if(inspector.currentEntity != null)
		{
			GL11.glPushMatrix();
			if(inspector.currentEntity instanceof Entity)
			{
				GL11.glTranslatef(pos.x+dim.x/2-inspector.currentEntity.getDim().x/2, pos.y+dim.y/2-inspector.currentEntity.getDim().y/2, 0);
				inspector.currentEntity.renderPreview(interpolation);
			}
			GL11.glPopMatrix();
		}
		
		Renderer.endScissor();
		
	}

}

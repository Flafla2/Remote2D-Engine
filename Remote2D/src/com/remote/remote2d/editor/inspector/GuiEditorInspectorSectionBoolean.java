package com.remote.remote2d.editor.inspector;

import org.lwjgl.opengl.GL11;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.gui.GuiTextField;
import com.remote.remote2d.engine.gui.TextLimiter;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorInspectorSectionBoolean extends GuiEditorInspectorSection {
	
	boolean isTrue = false;
	private boolean hasBeenChanged = false;

	public GuiEditorInspectorSectionBoolean(String name, GuiEditor inspector, Vector2 pos, int width) {
		super(name, inspector, pos, width);
	}

	@Override
	public int sectionHeight() {
		return 20;
	}

	@Override
	public Object getData() {
		return isTrue;
	}

	@Override
	public void initSection() {
		
	}

	@Override
	public void tick(int i, int j, int k) {
		hasBeenChanged = false;
		if(i > pos.x+width-20 && i < pos.x+width && j > pos.y && j < pos.y+20 && Remote2D.getInstance().hasMouseBeenPressed())
		{
			isTrue = !isTrue;
			hasBeenChanged = true;
		}
	}

	@Override
	public void render(float interpolation) {
		Fonts.get("Arial").drawString(renderName, pos.x, pos.y, 20, isComplete() ? 0xffffff : 0xff7777);
		Renderer.drawLineRect(new Vector2(pos.x+width-20,pos.y), new Vector2(20), 0xffffff, 1.0f);
		
		if(isTrue)
		{
			Renderer.drawLine(new Vector2(pos.x+width-20,pos.y), new Vector2(pos.x+width,pos.y+20), 0xffffff, 1.0f);
			Renderer.drawLine(new Vector2(pos.x+width-20,pos.y+20), new Vector2(pos.x+width,pos.y), 0xffffff, 1.0f);
		}
	}
	
	@Override
	public void setData(Object o) {
		if(o instanceof Boolean)
		{
			isTrue = ((Boolean)o).booleanValue();
		}
	}
	
	@Override
	public boolean hasFieldBeenChanged()
	{
		return hasBeenChanged;
	}

	@Override
	public void deselect() {
		
	}
	
	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

}

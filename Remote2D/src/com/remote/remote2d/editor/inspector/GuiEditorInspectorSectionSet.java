package com.remote.remote2d.editor.inspector;

import java.awt.Color;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.art.Animation;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.logic.Vector2;

public abstract class GuiEditorInspectorSectionSet extends GuiEditorInspectorSection {
	
	protected GuiEditorInspectorSection[] set;
	private int height;
	
	public GuiEditorInspectorSectionSet(String name, GuiEditor inspector, Vector2 pos, int width, String[] names, Class<?>[] objects) {
		super(name, inspector, pos, width);
		
		set = new GuiEditorInspectorSection[objects.length];
		int currentY = 20;
		for(int x=0;x<set.length;x++)
		{
			if(objects[x] == Animation.class)
				set[x] = new GuiEditorInspectorSectionAnimation(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Boolean.class)
				set[x] = new GuiEditorInspectorSectionBoolean(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Color.class)
				set[x] = new GuiEditorInspectorSectionColor(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Entity.class)
				set[x] = new GuiEditorInspectorSectionEntity(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Float.class)
				set[x] = new GuiEditorInspectorSectionFloat(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Integer.class)
				set[x] = new GuiEditorInspectorSectionInt(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Texture.class)
				set[x] = new GuiEditorInspectorSectionTexture(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == String.class)
				set[x] = new GuiEditorInspectorSectionString(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Vector2.class)
				set[x] = new GuiEditorInspectorSectionVec2D(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			currentY += set[x].sectionHeight();
		}
		height = currentY;
	}

	@Override
	public int sectionHeight() {
		return height;
	}

	@Override
	public void initSection() {
		for(GuiEditorInspectorSection sec : set)
			sec.initSection();
	}

	@Override
	public void deselect() {
		for(GuiEditorInspectorSection sec : set)
			sec.deselect();
	}

	@Override
	public boolean isSelected() {
		for(GuiEditorInspectorSection sec : set)
			if(sec.isSelected())
				return true;
		return false;
	}

	@Override
	public boolean isComplete() {
		for(GuiEditorInspectorSection sec : set)
			if(!sec.isComplete())
				return false;
		return true;
	}

	@Override
	public boolean hasFieldBeenChanged() {
		for(GuiEditorInspectorSection sec : set)
			if(sec.hasFieldBeenChanged())
				return true;
		return false;
	}

	@Override
	public void tick(int i, int j, int k) {
		for(GuiEditorInspectorSection sec : set)
			sec.tick(i, j, k);
	}

	@Override
	public void render(float interpolation) {
		Fonts.get("Arial").drawString(renderName, pos.x, pos.y, 20, isComplete() ? 0xffffff : 0xff7777);
		for(GuiEditorInspectorSection sec : set)
			sec.render(interpolation);
	}
	
	public void getDataWithName(String name)
	{
//		for(GuiEditorInspectorSection sec : set)
//			if(sec.name.equals(name))
	}

}

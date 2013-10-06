package com.remote.remote2d.editor.inspector;

import com.remote.remote2d.editor.DraggableObject;
import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.gui.Gui;
import com.remote.remote2d.engine.io.R2DType;
import com.remote.remote2d.engine.logic.Vector2;

public abstract class GuiEditorInspectorSection extends Gui {
	
	public Vector2 pos;
	protected String name;
	public String renderName;
	protected int width;
	protected GuiEditor inspector;
	
	public GuiEditorInspectorSection(String name, GuiEditor inspector, Vector2 pos, int width)
	{
		this.pos = pos.copy();
		this.name = name;
		this.width = width;
		this.inspector = inspector;
		
		renderName = splitCamelCase(name);
		if(Character.isLowerCase(renderName.charAt(0)))
			renderName = Character.toUpperCase(renderName.charAt(0))+renderName.substring(1);
	}
	
	public abstract int sectionHeight();
	public abstract Object getData();
	public abstract void initSection();
	public abstract void setData(Object o);
	public abstract void deselect();
	public abstract boolean isSelected();
	public abstract boolean isComplete();
	public abstract boolean hasFieldBeenChanged();
	
	public boolean acceptsDraggableObject(DraggableObject object)
	{
		return false;
	}
	
	public void acceptDraggableObject(DraggableObject object)
	{
		
	}
	
	public static String splitCamelCase(String s) {
	   return s.replaceAll(
	      String.format("%s|%s|%s",
	         "(?<=[A-Z])(?=[A-Z][a-z])",
	         "(?<=[^A-Z])(?=[A-Z])",
	         "(?<=[A-Za-z])(?=[^A-Za-z])"
	      ),
	      " "
	   );
	}
}

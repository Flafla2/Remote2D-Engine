package com.remote.remote2d.editor;

import com.remote.remote2d.engine.logic.Vector2;

public class DraggableObjectEntity extends DraggableObject {
	
	public String uuid;
	
	public DraggableObjectEntity(GuiEditor editor, String name, String uuid, Vector2 pos, Vector2 dim, Vector2 mouseOffset) {
		super(editor, name, pos, dim, mouseOffset);
		this.uuid = uuid;
	}

}

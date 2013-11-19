package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.entity.Entity;

public class OperationAddChild extends Operation {
	
	private String parent;
	private String child;

	public OperationAddChild(GuiEditor editor, Entity parent, Entity child) {
		super(editor);
		this.parent = parent.getUUID();
		this.child = child.getUUID();
	}

	@Override
	public void undo() {
		editor.getMap().getEntityList().getEntityWithUUID(parent).removeChild(child);
	}

	@Override
	public void execute() {
		Entity e = editor.getMap().getEntityList().getEntityWithUUID(parent);
		e.addChild(editor.getMap().getEntityList().getEntityWithUUID(child));
		editor.getMap().getEntityList().removeEntityFromList(e);
	}

	@Override
	public String name() {
		return "Remove Entity";
	}

	@Override
	public boolean canBeUndone() {
		return true;
	}

}

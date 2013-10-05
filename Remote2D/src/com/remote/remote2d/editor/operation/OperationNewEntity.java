package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.entity.Entity;

public class OperationNewEntity extends Operation{
	
	Entity entity;
		
	public OperationNewEntity(GuiEditor editor)
	{
		super(editor);
	}

	@Override
	public void execute() {
		editor.insertEntity();
	}

	@Override
	public void undo() {
		editor.getMap().getEntityList().removeEntityFromList(entity);
	}

	@Override
	public String name() {
		return "Insert Entity";
	}

	@Override
	public boolean canBeUndone() {
		return true;
	}
	
}

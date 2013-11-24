package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.entity.Entity;

public class OperationDeleteEntity extends Operation {
	
	Entity entity;
	int position;

	public OperationDeleteEntity(GuiEditor editor) {
		super(editor);
		this.entity = editor.getMap().getEntityList().getEntityWithUUID(editor.getSelectedEntity());
	}

	@Override
	public void execute() {
		position = editor.getMap().getEntityList().indexOf(entity);
		editor.getMap().getEntityList().removeEntityFromList(entity);
	}

	@Override
	public void undo() {
		editor.getMap().getEntityList().addEntityToList(entity,position);
	}

	@Override
	public String name() {
		return "Delete Entity";
	}

	@Override
	public boolean canBeUndone() {
		return true;
	}

}

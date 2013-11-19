package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.entity.Entity;

public class OperationDeleteEntity extends Operation {
	
	Entity entity;
	int position;
	Entity parent;

	public OperationDeleteEntity(GuiEditor editor) {
		super(editor);
		this.entity = editor.getMap().getEntityList().getEntityWithUUID(editor.getSelectedEntity());
	}

	@Override
	public void execute() {
		parent = entity.getParent();
		if(entity.getParent() != null)
		{
			position = editor.getMap().getEntityList().indexOf(entity);
			editor.getMap().getEntityList().removeEntityFromList(entity);
		} else
		{
			position = entity.getParent().indexOfChild(entity);
			entity.getParent().removeChild(entity);
		}
	}

	@Override
	public void undo() {
		if(parent == null)
			editor.getMap().getEntityList().addEntityToList(entity,position);
		else
			parent.addChild(position,entity);
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

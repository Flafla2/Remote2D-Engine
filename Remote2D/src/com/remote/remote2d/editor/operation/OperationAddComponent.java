package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.entity.component.Component;

public class OperationAddComponent extends Operation {
	
	private String u;
	private Component c;
	private int position;

	public OperationAddComponent(GuiEditor editor, String e, Component c) {
		super(editor);
		this.u = e;
		this.c = c;
	}

	@Override
	public void execute() {
		Entity e = editor.getMap().getEntityList().getEntityWithUUID(u);
		e.getComponents().add(c);
		position = e.getComponents().indexOf(c);
		
		if(u != null && u.equals(editor.getSelectedEntity()))
			editor.getInspector().setCurrentEntity(u);
	}

	@Override
	public void undo() {
		Entity e = editor.getMap().getEntityList().getEntityWithUUID(u);
		e.getComponents().remove(position);
		
		if(u != null && u.equals(editor.getSelectedEntity()))
			editor.getInspector().setCurrentEntity(u);
	}

	@Override
	public String name() {
		return "Add Component";
	}

	@Override
	public boolean canBeUndone() {
		return true;
	}

}

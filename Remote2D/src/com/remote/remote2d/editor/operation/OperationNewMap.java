package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.world.Map;

public class OperationNewMap extends Operation {

	public OperationNewMap(GuiEditor editor) {
		super(editor);
	}

	@Override
	public void execute() {
		editor.setMap(new Map());
	}

	@Override
	public void undo() {
		
	}

	@Override
	public String name() {
		return "Create New Map";
	}

	@Override
	public boolean canBeUndone() {
		return false;
	}

}

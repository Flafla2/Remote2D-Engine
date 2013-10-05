package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.world.Map;

public class OperationOpenMap extends Operation {
	
	Map map;

	public OperationOpenMap(GuiEditor editor, Map map) {
		super(editor);
		this.map = map;
	}

	@Override
	public void execute() {
		editor.setMap(map);
	}

	@Override
	public void undo() {
		
	}

	@Override
	public String name() {
		return "Open Map";
	}

	@Override
	public boolean canBeUndone() {
		return false;
	}
}

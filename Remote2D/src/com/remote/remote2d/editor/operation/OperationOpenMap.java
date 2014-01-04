package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.world.Map;

public class OperationOpenMap extends Operation {
	
	Map map;
	String path;

	public OperationOpenMap(GuiEditor editor, Map map, String path) {
		super(editor);
		this.map = map;
		this.path = path;
	}

	@Override
	public void execute() {
		map.path = path;
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

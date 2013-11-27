package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.world.Map;

public class OperationSaveMap extends Operation {
	
	String path;
	
	public OperationSaveMap(GuiEditor editor, String path) {
		super(editor);
		this.path = path;
	}

	@Override
	public void execute() {
		Map map = editor.getMap();
		R2DFileManager mapManager = new R2DFileManager(path, map);
		mapManager.write();
		map.path = path;
		editor.setMap(map);
	}

	@Override
	public void undo() {
		
	}

	@Override
	public String name() {
		return "Save Map";
	}

	@Override
	public boolean canBeUndone() {
		return false;
	}

}

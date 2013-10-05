package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.world.Map;

public class OperationSaveMap extends Operation {

	public OperationSaveMap(GuiEditor editor) {
		super(editor);
	}

	@Override
	public void execute() {
		Map map = editor.getMap();
		R2DFileManager mapManager = new R2DFileManager("/res/maps/map.r2d", map);
		mapManager.write();
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

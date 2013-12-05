package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.io.R2DTypeCollection;

public class OperationEditPrefab extends Operation {
	
	String path;
	R2DTypeCollection newColl;
	R2DTypeCollection oldColl;

	public OperationEditPrefab(GuiEditor editor, String path, R2DTypeCollection newColl) {
		super(editor);
		this.path = path;
		this.newColl = newColl;
	}

	@Override
	public void execute() {		
		R2DFileManager manager = new R2DFileManager(path,null);
		manager.read();
		oldColl = manager.getCollection();
		manager.setCollection(newColl);
		manager.write();
	}

	@Override
	public void undo() {
		R2DFileManager manager = new R2DFileManager(path,null);
		manager.setCollection(oldColl);
		manager.write();
	}

	@Override
	public String name() {
		return "Edit Prefab";
	}

	@Override
	public boolean canBeUndone() {
		return true;
	}

}

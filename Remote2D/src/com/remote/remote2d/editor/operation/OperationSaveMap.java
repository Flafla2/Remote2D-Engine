package com.remote.remote2d.editor.operation;

import java.io.File;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.Remote2DException;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.io.R2DFileUtility;
import com.remote.remote2d.engine.world.Map;

public class OperationSaveMap extends Operation {
	
	String path;
	String name;
	boolean exists;
	
	public OperationSaveMap(GuiEditor editor, String path) {
		super(editor);
		this.path = path;
		File f = R2DFileUtility.getResource(path);
		exists = f.exists();
		name = f.getName();
	}

	@Override
	public void execute() {
		try
		{
			Map map = editor.getMap();
			R2DFileManager mapManager = new R2DFileManager(path, map);
			mapManager.write();
			map.path = path;
			editor.setMap(map);
		} catch(Exception e){
			throw new Remote2DException(e);
		}
	}

	@Override
	public void undo() {
		
	}

	@Override
	public String name() {
		return "Save Map";
	}
	
	@Override
	public String confirmationMessage()
	{
		String ret = "Are you sure you would like to "+name()+"?";
		if(!exists)
			ret += "  "+name+" already exists, and will be overwritten!";
		if(!canBeUndone())
			ret += "  This operation cannot be undone.";
		return ret;
	}

	@Override
	public boolean canBeUndone() {
		return false;
	}

}

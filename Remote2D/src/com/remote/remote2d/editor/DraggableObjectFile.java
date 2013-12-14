package com.remote.remote2d.editor;

import java.io.File;

import com.remote.remote2d.engine.io.R2DFileUtility;
import com.remote.remote2d.engine.logic.Vector2;

public class DraggableObjectFile extends DraggableObject {
	
	public File file;
	
	public DraggableObjectFile(GuiEditor editor, String name, File file, Vector2 pos, Vector2 dim, Vector2 mouseOffset) {
		super(editor, name, pos, dim, mouseOffset);
		this.file = file;
		if(this.file.isAbsolute())
			this.file = R2DFileUtility.getRelativeFile(file);
	}
}

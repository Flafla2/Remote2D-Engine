package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;

public abstract class Operation {
	
	protected GuiEditor editor;
	
	public Operation(GuiEditor editor)
	{
		this.editor = editor;
	}
	
	public abstract void execute();
	public abstract void undo();
	public abstract String name();
	public abstract boolean canBeUndone();
	
	public String confirmationMessage()
	{
		String ret = "Are you sure you would like to "+name()+"?";
		if(!canBeUndone())
			ret += "  This operation cannot be undone.";
		return ret;
	}
	
}

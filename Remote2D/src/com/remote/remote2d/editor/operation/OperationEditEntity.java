package com.remote.remote2d.editor.operation;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.entity.Entity;

public class OperationEditEntity extends Operation {
	
	Entity before;
	Entity after;
	
	
	public OperationEditEntity(GuiEditor editor, Entity before, Entity after) {
		super(editor);
		this.before = before;
		this.after = after;
	}

	@Override
	public void execute() {
//		int position = editor.getMap().getEntityList().indexOf(before);
//		after.updatePos();
//		editor.getMap().getEntityList().set(position,after);
//		editor.setSelectedEntity(position);
		
		for(int x=0;x<editor.getMap().getEntityList().size();x++)
		{
			if(editor.getMap().getEntityList().get(x).getUUID().equals(before.getUUID()))
			{
				boolean setSelected = before.getUUID().equals(editor.getSelectedEntity());
				after.updatePos();
				editor.getMap().getEntityList().get(x).transpose(after);
				if(setSelected)
					editor.setSelectedEntity(after.getUUID());
				return;
			}
		}
	}

	@Override
	public void undo() {
//		int position = editor.getMap().getEntityList().indexOf(after);
//		before.updatePos();
//		editor.getMap().getEntityList().set(position,before);
//		editor.setSelectedEntity(position);
		for(int x=0;x<editor.getMap().getEntityList().size();x++)
		{
			if(editor.getMap().getEntityList().get(x).getUUID().equals(after.getUUID()))
			{
				boolean setSelected = after.getUUID().equals(editor.getSelectedEntity());
				before.updatePos();
				editor.getMap().getEntityList().get(x).transpose(before);
				if(setSelected)
					editor.setSelectedEntity(before.getUUID());
				return;
			}
		}
	}

	@Override
	public String name() {
		return "Edit Entity";
	}
	
	@Override
	public String confirmationMessage()
	{
		String name = before.name;
		if(name.equals(""))
			name = "Untitled";
		String ret = "Are you sure you would like to edit "+name+"?";
		if(before.getPrefabPath() != null && after.getPrefabPath() == null)
			ret += "  This entity is currently attached to a prefab, and will be disconnected from its prefab status!";
		if(!canBeUndone())
			ret += "  This operation cannot be undone.";
		return ret;
	}

	@Override
	public boolean canBeUndone() {
		return true;
	}

}

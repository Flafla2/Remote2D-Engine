package com.remote.remote2d.editor.operation;

import com.esotericsoftware.minlog.Log;
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
				boolean setSelected = editor.getSelectedEntity().getUUID().equals(before.getUUID());
				after.updatePos();
				editor.getMap().getEntityList().set(x, after);
				if(setSelected)
					editor.setSelectedEntity(x);
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
				boolean setSelected = editor.getSelectedEntity().getUUID().equals(after.getUUID());
				before.updatePos();
				editor.getMap().getEntityList().set(x, before);
				if(setSelected)
					editor.setSelectedEntity(x);
				return;
			}
		}
	}

	@Override
	public String name() {
		return "Edit Entity";
	}

	@Override
	public boolean canBeUndone() {
		return true;
	}

}

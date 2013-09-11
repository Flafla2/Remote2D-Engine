package com.remote.remote2d.gui.editor;

import com.remote.remote2d.Remote2D;
import com.remote.remote2d.entity.Entity;
import com.remote.remote2d.entity.component.Component;
import com.remote.remote2d.gui.GuiButton;
import com.remote.remote2d.gui.GuiTextField;
import com.remote.remote2d.gui.GuiWindow;
import com.remote.remote2d.gui.WindowHolder;
import com.remote.remote2d.logic.ColliderBox;
import com.remote.remote2d.logic.Vector2;

public class GuiWindowInsertComponent extends GuiWindow {
	
	GuiTextField textField;
	Entity entity;
	GuiButton doneButton;
	GuiEditor editor;

	public GuiWindowInsertComponent(WindowHolder holder, Vector2 pos, ColliderBox allowedBounds, Entity e, GuiEditor editor) {
		super(holder, pos, new Vector2(200,95), allowedBounds, "Insert Component");
		textField = new GuiTextField(new Vector2(10,5),new Vector2(180,40),20);
		this.entity = e;
		this.editor = editor;
	}
	
	public void initGui()
	{
		buttonList.clear();
		doneButton = new GuiButton(0,new Vector2(10,50),new Vector2(80,40),"Done");
		doneButton.setDisabled(true);
		buttonList.add(doneButton);
		buttonList.add(new GuiButton(1,new Vector2(110,50),new Vector2(80,40),"Cancel"));
	}

	@Override
	public void renderContents(float interpolation) {
		textField.render(interpolation);
	}
	
	@Override
	public void tick(int i, int j, int k)
	{
		super.tick(i, j, k);
		Vector2 mouse = getMouseInWindow(i,j);
		textField.tick((int)mouse.x, (int)mouse.y, k);
		
		if(!Remote2D.getInstance().componentList.containsComponent(textField.text) && !doneButton.getDisabled())
			doneButton.setDisabled(true);
		if(Remote2D.getInstance().componentList.containsComponent(textField.text) && doneButton.getDisabled())
			doneButton.setDisabled(false);
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			editor.getInspector().apply();
			Component c = Remote2D.getInstance().componentList.getComponentWithEntity(textField.text.trim(),entity);
			entity.addComponent(c.clone());
			editor.getInspector().setCurrentEntity(entity);
		}
		holder.closeWindow(this);
	}

}

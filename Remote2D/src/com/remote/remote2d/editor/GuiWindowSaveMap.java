package com.remote.remote2d.editor;

import com.remote.remote2d.editor.operation.OperationSaveMap;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiTextField;
import com.remote.remote2d.engine.gui.GuiWindow;
import com.remote.remote2d.engine.gui.WindowHolder;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiWindowSaveMap extends GuiWindow {
	
	GuiTextField textField;
	GuiButton doneButton;
	
	public GuiWindowSaveMap(WindowHolder holder, Vector2 pos, ColliderBox allowedBounds)
	{
		this(holder,pos,new Vector2(300,100),allowedBounds);
	}

	public GuiWindowSaveMap(WindowHolder holder, Vector2 pos, Vector2 dim, ColliderBox allowedBounds) {
		super(holder, pos, dim, allowedBounds, "Save Map");
		
		textField = new GuiTextField(new Vector2(10,10),new Vector2(dim.x-20,40), 20);
		textField.text = "/res/maps/map.r2d";
		if(holder instanceof GuiEditor && ((GuiEditor)holder).getMap().path != null)
			textField.text = ((GuiEditor)holder).getMap().path;
	}
	
	@Override
	public void initGui()
	{
		textField.dim = new Vector2(dim.x-20,40);
		
		buttonList.clear();
		buttonList.add(new GuiButton(0,new Vector2(10,dim.y-50),new Vector2(dim.x/2-10,40),"Cancel"));
		buttonList.add(doneButton = new GuiButton(1,new Vector2(dim.x/2+10,dim.y-50),new Vector2(dim.x/2-20,40),"Done"));	
	}

	@Override
	public void renderContents(float interpolation) {
		textField.render(interpolation);
	}
	
	@Override
	public void tick(int i, int j, int k)
	{
		super.tick(i,j,k);
		textField.tick(i, j, k);
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
			holder.closeWindow(this);
		else if(button.id == 1 && holder instanceof GuiEditor)
		{
			GuiEditor editor = (GuiEditor)holder;
			
			editor.confirmOperation(new OperationSaveMap(editor,textField.text));
		}
	}

}

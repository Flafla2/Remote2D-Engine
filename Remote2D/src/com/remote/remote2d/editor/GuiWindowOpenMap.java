package com.remote.remote2d.editor;

import com.remote.remote2d.editor.operation.OperationOpenMap;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiTextField;
import com.remote.remote2d.engine.gui.GuiWindow;
import com.remote.remote2d.engine.gui.WindowHolder;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.io.R2DFileUtility;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Map;

public class GuiWindowOpenMap extends GuiWindow {
	
	GuiTextField textField;
	GuiButton doneButton;
	
	public GuiWindowOpenMap(WindowHolder holder, Vector2 pos, ColliderBox allowedBounds)
	{
		this(holder,pos,new Vector2(300,100),allowedBounds);
	}

	public GuiWindowOpenMap(WindowHolder holder, Vector2 pos, Vector2 dim, ColliderBox allowedBounds) {
		super(holder, pos, dim, allowedBounds, "Open Map");
		
		textField = new GuiTextField(new Vector2(10,10),new Vector2(dim.x-20,40), 20);
		textField.text = "/res/maps/map.r2d";
	}
	
	@Override
	public void initGui()
	{
		if(textField != null)
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
		
		if(!R2DFileUtility.R2DExists(textField.text))
			doneButton.setDisabled(true);
		else if(doneButton.getDisabled())
			doneButton.setDisabled(false);
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
			holder.closeWindow(this);
		else if(button.id == 1 && holder instanceof GuiEditor)
		{
			GuiEditor editor = (GuiEditor)holder;
			
			Map newMap = new Map();
			R2DFileManager mapManager = new R2DFileManager(textField.text, newMap);
			mapManager.read();
			if(editor.getMap() != null)
				editor.confirmOperation(new OperationOpenMap(editor,newMap));
			else
				editor.executeOperation(new OperationOpenMap(editor,newMap));
		}
	}

}

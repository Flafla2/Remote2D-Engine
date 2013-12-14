package com.remote.remote2d.editor.operation;

import java.util.ArrayList;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiWindow;
import com.remote.remote2d.engine.gui.WindowHolder;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiWindowConfirmOperation extends GuiWindow {
	
	private String[] contents;
	private Operation operation;

	public GuiWindowConfirmOperation(WindowHolder holder, Vector2 pos, ColliderBox allowedBounds, Operation operation) {
		super(holder, pos, new Vector2(300,300), allowedBounds, "Confirm Operation");
		this.operation = operation;
		String contents = operation.confirmationMessage();
		
		ArrayList<String> trueContents = Fonts.get("Arial").getStringSet(contents, 20, dim.x-20);
		
		//Log.debug(trueContents.toString());
		this.contents = new String[trueContents.size()];
		this.contents = trueContents.toArray(this.contents);
		
		dim.y = this.contents.length*25+60;
		initGui();
	}
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		buttonList.add(new GuiButton(0,new Vector2(10,dim.y-40),new Vector2(135,40),"Yes"));
		buttonList.add(new GuiButton(1,new Vector2(155,dim.y-40),new Vector2(135,40),"No"));
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			if(holder instanceof GuiEditor)
			{
				((GuiEditor)holder).executeOperation(operation);
			}
		}
		holder.closeWindow(this);
	}

	@Override
	public void renderContents(float interpolation) {
		for(int x=0;x<contents.length;x++)
			Fonts.get("Arial").drawString(contents[x], 10, 10+x*25, 20, 0xffffff);
	}
}

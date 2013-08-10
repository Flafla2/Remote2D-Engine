package com.remote.remote2d.test.gui;

import com.remote.remote2d.Remote2D;
import com.remote.remote2d.art.Fonts;
import com.remote.remote2d.gui.GuiButton;
import com.remote.remote2d.gui.GuiMenu;
import com.remote.remote2d.gui.editor.GuiEditor;
import com.remote.remote2d.logic.Vector2;

public class GuiMainMenu extends GuiMenu {
		
	public GuiMainMenu()
	{
	}
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		buttonList.add(new GuiButton(0,new Vector2(screenWidth()/2-125,150),new Vector2(250,40),"Open Editor"));
		buttonList.add(new GuiButton(1,new Vector2(screenWidth()/2-125,200),new Vector2(250,40),"Quit"));
	}
	
	@Override
	public void render(float interpolation)
	{
		super.render(interpolation);
		int[] remoteDim = Fonts.get("Logo").getStringDim("REMOTE", 100);
		int[] otherDim = Fonts.get("Logo").getStringDim("2D", 50);
		int remotePos = screenWidth()/2-(remoteDim[0]+otherDim[0])/2;
		int otherPos = remotePos+remoteDim[0];
		
		Fonts.get("Logo").drawString("REMOTE", remotePos, 0, 100, 0xFF0000);
		Fonts.get("Logo").drawString("2D", otherPos, 50, 50, 0xFF0000);
		
		Fonts.get("Arial").drawCenteredString("Remote2D Test Game", 90, 40, 0x000000);
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			Remote2D.getInstance().guiList.add(new GuiEditor());
		} else if(button.id == 1)
		{
			Remote2D.getInstance().running = false;
		}
	}
	
}

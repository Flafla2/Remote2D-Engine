package com.remote.remote2d.extras.test.gui;

import java.util.ArrayList;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiMenu;
import com.remote.remote2d.engine.io.R2DFileUtility;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiOptions extends GuiMenu {
	
	private String message = "";
	private long lastMessageTime = -1;
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		buttonList.add(new GuiButton(0, new Vector2(screenWidth()/2-100,	screenHeight()-40),	new Vector2(200,40),"Done"));
		buttonList.add(new GuiButton(1, new Vector2(screenWidth()/2-100,	100),				new Vector2(200,40),"Toggle Fullscreen"));
		buttonList.add(new GuiButton(2, new Vector2(screenWidth()/2-200,	180),				new Vector2(200,40),"to XML"));
		buttonList.add(new GuiButton(3, new Vector2(screenWidth()/2,		180),				new Vector2(200,40),"to Binary"));
	}
	
	@Override
	public void render(float interpolation)
	{
		super.render(interpolation);
		Fonts.get("Logo").drawCenteredString("OPTIONS", 10, 70, 0xff0000);
		Fonts.get("Arial").drawCenteredString("Convert all R2D Assets...", 150, 20, 0x000000);
		Fonts.get("Arial").drawCenteredString(message, 240, 20, 0x000000);
		
		if(System.currentTimeMillis() - lastMessageTime > 7000)
			message = "";
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
			Remote2D.guiList.pop();
		else if(button.id == 1)
		{
			Vector2 scrdim = Remote2D.displayHandler.getScreenDimensions();
			Remote2D.displayHandler.setDisplayMode((int)scrdim.x, (int)scrdim.y, !Remote2D.displayHandler.getFullscreen(), Remote2D.displayHandler.getBorderless());
		} else if(button.id == 2)
		{
			new Thread(new Runnable() {
				@Override
				public void run()
				{
					R2DFileUtility.convertFolderToXML("/res", false, true, true);
				}
			}).run();
			message = "Finished converting all Remote2D files to XML";
			lastMessageTime = System.currentTimeMillis();
		}
		else if(button.id == 2)
		{
			new Thread(new Runnable() {
				@Override
				public void run()
				{
					R2DFileUtility.convertFolderToBinary("/res", false, true, true);
				}
			}).run();
			message = "Finished converting all Remote2D files to Binary";
			lastMessageTime = System.currentTimeMillis();
		}
	}
	
}

package com.remote.remote2d.extras.test.gui;

import org.lwjgl.opengl.Display;

import com.remote.remote2d.engine.AudioHandler;
import com.remote.remote2d.engine.DisplayHandler;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiMenu;
import com.remote.remote2d.engine.io.R2DFileUtility;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiOptions extends GuiMenu {
	
	private String message = "";
	private long lastMessageTime = -1;
	private Texture regginator;
	private long lastReggie = -1;
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		buttonList.add(new GuiButton(0, new Vector2(screenWidth()/2-100,	screenHeight()-40),	new Vector2(200,40),"Done"));
		buttonList.add(new GuiButton(1, new Vector2(screenWidth()/2-100,	100),				new Vector2(200,40),"Toggle Fullscreen"));
		buttonList.add(new GuiButton(2, new Vector2(screenWidth()/2-200,	180),				new Vector2(200,40),"to XML"));
		buttonList.add(new GuiButton(3, new Vector2(screenWidth()/2,		180),				new Vector2(200,40),"to Binary"));
		buttonList.add(new GuiButton(4, new Vector2(screenWidth()/2-100,	250),				new Vector2(200,40),"Play Test Sound"));
		buttonList.add(new GuiButton(5, new Vector2(screenWidth()/2-250,	320),				new Vector2(50,40),"<"));
		buttonList.add(new GuiButton(6, new Vector2(screenWidth()/2+200,	320),				new Vector2(50,40),">"));
		
		if(regginator == null)
			regginator = new Texture("res/art/ready.png",true,false);
	}
	
	@Override
	public void render(float interpolation)
	{
		super.render(interpolation);
		Fonts.get("Logo").drawCenteredString("OPTIONS", 10, 70, 0xff0000);
		Fonts.get("Arial").drawCenteredString("Convert all R2D Assets...", 150, 20, 0x000000);
		Fonts.get("Arial").drawCenteredString(message, 240, 20, 0x000000);
		Fonts.get("Arial").drawCenteredString("Volume", 300, 20, 0x000000);
		Fonts.get("Arial").drawCenteredString(Math.round(AudioHandler.getVolume()*100)+"%", 330, 20, 0x000000);
		
		if(System.currentTimeMillis() - lastMessageTime > 7000)
			message = "";
		
		if(System.currentTimeMillis()-lastReggie < 6647)
			Renderer.drawRect(new Vector2(screenWidth()/2-80,360), new Vector2(160), regginator, 0xffffff, 1);
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
			Remote2D.guiList.pop();
		else if(button.id == 1)
		{
			DisplayHandler.setDisplayMode(Display.getDesktopDisplayMode().getWidth(),
					Display.getDesktopDisplayMode().getHeight(), !Display.isFullscreen(), 
					false);
		} else if(button.id == 2)
		{
			new Thread(new Runnable() {
				@Override
				public void run()
				{
					message = "Loading...";
					R2DFileUtility.convertFolderToXML("res", true);
					message = "Finished converting all Remote2D files to XML";
					lastMessageTime = System.currentTimeMillis();
				}
			}).run();
		}
		else if(button.id == 3)
		{
			new Thread(new Runnable() {
				@Override
				public void run()
				{
					message = "Loading...";
					R2DFileUtility.convertFolderToBinary("res", true);
					message = "Finished converting all Remote2D files to Binary";
					lastMessageTime = System.currentTimeMillis();
				}
			}).run();
		} else if(button.id == 4)
		{
			AudioHandler.playSound("res/sounds/regginator.wav", true, false);
			lastReggie = System.currentTimeMillis();
		} else if(button.id == 5 && AudioHandler.getVolume() >= 0.1f)
		{
			AudioHandler.setVolume(AudioHandler.getVolume()-0.1f);
		} else if(button.id == 6 && AudioHandler.getVolume() <= 0.9f)
		{
			AudioHandler.setVolume(AudioHandler.getVolume()+0.1f);
		}
	}
	
}

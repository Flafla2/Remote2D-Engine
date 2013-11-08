package com.remote.remote2d.extras.test.gui;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiInGame;
import com.remote.remote2d.engine.gui.GuiMenu;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Map;

public class GuiMainMenu extends GuiMenu {
		
	public GuiMainMenu()
	{
	}
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		buttonList.add(new GuiButton(0, new Vector2(screenWidth() / 2 - 125, 150), new Vector2(250, 40), "Run Game"));
		buttonList.add(new GuiButton(1, new Vector2(screenWidth() / 2 - 125, 200), new Vector2(250, 40), "Open Editor"));
		buttonList.add(new GuiButton(2, new Vector2(screenWidth() / 2 - 125, 250), new Vector2(250, 40), "Options"));
		buttonList.add(new GuiButton(3, new Vector2(screenWidth() / 2 - 125, 300), new Vector2(250, 40), "Quit"));
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
		if (button.id == 0) {
			Map newMap = new Map();
			R2DFileManager mapManager = new R2DFileManager("res/maps/map.r2d", newMap);
			mapManager.read();
			Remote2D.guiList.push(new GuiInGame(newMap)); // start game
		}
		else if (button.id == 1) Remote2D.guiList.add(new GuiEditor());
		else if (button.id == 2) Remote2D.guiList.add(new GuiOptions());
		else if (button.id == 3) Remote2D.running = false;
	}
	
}

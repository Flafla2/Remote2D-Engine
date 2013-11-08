package com.remote.remote2d.extras.test;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.Remote2DGame;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.extras.test.entity.ComponentPlayer;
import com.remote.remote2d.extras.test.entity.ComponentTopDownPlayer;
import com.remote.remote2d.extras.test.gui.GuiMainMenu;

public class Remote2DTest extends Remote2DGame {
	
	public static void main(String[] args)
	{
		Remote2D.startRemote2D(new Remote2DTest());
	}

	@Override
	public void initGame() {
		Log.TRACE();
		R2DFileManager.USE_XML = true;
		Remote2D.guiList.push(new GuiMainMenu());
		Remote2D.componentList.addInsertableComponent("Player", ComponentPlayer.class);
		Remote2D.componentList.addInsertableComponent("Top Down Player", ComponentTopDownPlayer.class);
	}
	
	@Override
	public String[] getIconPath()
	{
		String[] paths = {"res/gui/icon_128.png","res/gui/icon_32.png","res/gui/icon_16.png"};
		return paths;
	}
	
}

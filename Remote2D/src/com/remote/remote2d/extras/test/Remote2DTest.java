package com.remote.remote2d.extras.test;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.Remote2DGame;
import com.remote.remote2d.extras.test.entity.ComponentPlayer;
import com.remote.remote2d.extras.test.gui.GuiMainMenu;

public class Remote2DTest extends Remote2DGame {
	
	public static void main(String[] args)
	{
		Remote2D.startRemote2D(new Remote2DTest());
	}

	@Override
	public void initGame() {
		Log.TRACE();
		Remote2D.getInstance().guiList.push(new GuiMainMenu());
		Remote2D.getInstance().componentList.addInsertableComponent("Player", ComponentPlayer.class);
	}
	
	@Override
	public String[] getIconPath()
	{
		String[] paths = {"/res/gui/icon_16.png","/res/gui/icon_32.png","/res/gui/icon_128.png"};
		return paths;
	}
	
}

package com.remote.remote2d.engine.gui;

import org.lwjgl.input.Keyboard;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.world.Map;

public class GuiInGame extends GuiMenu implements MapHolder {
	
	protected Map map;
	protected String mapPath;
	
	public GuiInGame(String mapPath)
	{
		Log.info("Loading Map...");
		map = new Map();
		R2DFileManager mapManager = new R2DFileManager(mapPath,map);
		mapManager.read();
		backgroundColor = map.backgroundColor;
		map.spawn();
	}
	
	public GuiInGame(Map map) {
		this.map = map;
		Log.info("Loading Map...");
		backgroundColor = map.backgroundColor;
		this.map.spawn();
	}

	@Override
	public void initGui()
	{
		
	}
	
	@Override
	public void render(float interpolation)
	{
		map.render(false,interpolation);
		Fonts.get("Arial").drawString("FPS: "+Remote2D.getFPS(), 10, 10, 20, 0x000000);
	}
	
	@Override
	public void tick(int i, int j, int k)
	{
		map.tick(i,j,k,false);
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			Remote2D.guiList.pop();
	}

	@Override
	public Map getMap() {
		return map;
	}
	
}

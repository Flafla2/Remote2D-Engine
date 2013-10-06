package com.remote.remote2d.editor;

import java.awt.image.BufferedImage;

import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiWindow;
import com.remote.remote2d.engine.gui.WindowHolder;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Noise2D;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiWindowPerlin2D extends GuiWindow {
	
	private Noise2D perlin;
	private BufferedImage values;
	private Texture tex;

	public GuiWindowPerlin2D(WindowHolder holder, Vector2 pos, ColliderBox allowedBounds) {
		super(holder, pos, new Vector2(300,300), allowedBounds, "2D Perlin Noise");
		generate();
	}
	
	public void generate()
	{
		if(tex != null)
			tex.removeTexture();
		values = Noise2D.perlinImage(300, 300, 6);
		tex = new Texture(values,true,false);
	}
	
	@Override
	public void renderContents(float interpolation) {
		Renderer.drawRect(new Vector2(0,0), new Vector2(300,300), tex, 0xffffff, 1.0f);
	}
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		buttonList.add(new GuiButton(0,new Vector2(30,10),new Vector2(240,40),"Regenerate"));
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
			generate();
	}
	
	@Override
	public boolean canResize()
	{
		return false;
	}

}

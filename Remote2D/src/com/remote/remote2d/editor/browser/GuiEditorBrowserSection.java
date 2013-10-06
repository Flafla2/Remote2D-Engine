package com.remote.remote2d.editor.browser;

import java.io.File;

import org.lwjgl.input.Mouse;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.editor.DraggableObjectFile;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.gui.Gui;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorBrowserSection extends Gui {
	
	File file;
	public Vector2 pos;
	public Vector2 oldPos;
	public Vector2 dim;
	public boolean isSelected = false;
	
	private GuiEditorBrowser browser;
	private long lastClickEvent = -1;

	public GuiEditorBrowserSection(GuiEditorBrowser browser, File file, Vector2 pos, Vector2 dim) {
		this.pos = pos;
		this.oldPos = pos.copy();
		this.dim = dim;
		this.file = file;
		this.browser = browser;
	}

	@Override
	public void tick(int i, int j, int k) {
		long time = System.currentTimeMillis();
		if(Remote2D.getInstance().hasMouseBeenPressed())
		{
			if(pos.getColliderWithDim(dim).isPointInside(new Vector2(i,j)))
			{
				isSelected = true;
				if(lastClickEvent != -1 && time-lastClickEvent <= 500)
				{
					browser.doubleClickEvent(file);
					lastClickEvent = -1;
				} else
				{
					lastClickEvent = time;
				}
			} else
			{
				lastClickEvent = -1;
				isSelected = false;
			}
		} else if(Mouse.isButtonDown(0) && pos.getColliderWithDim(dim).isPointInside(new Vector2(i,j)) && file != null && browser.getEditor().dragObject == null)
		{
			browser.getEditor().dragObject = new DraggableObjectFile(browser.getEditor(), file.getName(),file,pos,dim,new Vector2(i,j).subtract(pos));
		}
	}

	@Override
	public void render(float interpolation) {
		if(isSelected)
			Renderer.drawRect(pos, dim, 0xffffff, 0.5f);
		String name = "...";
		if(file != null)
			name = file.getName();
		Fonts.get("Arial").drawString(name, pos.x+10, pos.y, 20, 0xffffff);
	}

}

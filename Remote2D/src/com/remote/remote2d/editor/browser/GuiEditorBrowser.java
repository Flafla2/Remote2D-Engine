package com.remote.remote2d.editor.browser;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import com.remote.remote2d.editor.GuiCreateSpriteSheet;
import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Animation;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.gui.Gui;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Map;

public class GuiEditorBrowser extends Gui {
	
	private Stack<Folder> folderStack;
	private ArrayList<GuiEditorBrowserSection> sections;
	private GuiEditor editor;
	
	public Vector2 pos;
	public Vector2 dim;
	
	public GuiEditorBrowser(GuiEditor editor, Vector2 pos, Vector2 dim)
	{
		this.pos = pos;
		this.dim = dim;
		this.editor = editor;
		
		folderStack = new Stack<Folder>();
		sections = new ArrayList<GuiEditorBrowserSection>();
		pushFolder(new Folder(Remote2D.getJarPath().getPath()));
	}
	
	public void pushFolder(Folder f)
	{
		folderStack.push(f);
		resetSections();
	}
	
	public Folder popFolder()
	{
		if(folderStack.size() > 1)
		{
			Folder f = folderStack.pop();
			resetSections();
			return f;
		} else
			return null;
	}
	
	public void resetSections()
	{
		sections.clear();
		float currentY = pos.y+20;
		if(folderStack.size() > 1)
		{
			sections.add(new GuiEditorBrowserSection(this,null,new Vector2(pos.x,currentY),new Vector2(dim.x,20)));
			currentY+=20;
		}
		for(File file : folderStack.peek().files)
		{
			sections.add(new GuiEditorBrowserSection(this,file,new Vector2(pos.x,currentY),new Vector2(dim.x,20)));
			currentY += 20;
		}
	}

	@Override
	public void tick(int i, int j, int k) {
		for(int x=0;x<sections.size();x++)
			sections.get(x).tick(i, j, k);
	}
	
	public void doubleClickEvent(File file)
	{
		if(file == null)
			popFolder();
		else if(file.isDirectory())
			pushFolder(new Folder(file.getPath()));
		else if(file.isFile())
		{
			String localPath = file.getPath().substring((Remote2D.getJarPath().getPath().length()));
			if(localPath.endsWith(Animation.getExtension()))
				Remote2D.getInstance().guiList.push(new GuiCreateSpriteSheet(new Animation(localPath)));
			else if(localPath.endsWith(Entity.getExtension()))
			{
				Entity e = new Entity(editor.getMap());
				R2DFileManager manager = new R2DFileManager(localPath,e);
				manager.read();
				editor.insertEntity(e);
			} else if(localPath.endsWith(Map.getExtension()))
			{
				Map map = new Map();
				R2DFileManager manager = new R2DFileManager(localPath,map);
				manager.read();
				editor.setMap(map);
			}

		}
	}
	
	public GuiEditor getEditor()
	{
		return editor;
	}

	@Override
	public void render(float interpolation) {
		
		Renderer.drawRect(pos, dim, 0x000000, 0.5f);
		Renderer.drawLine(new Vector2(pos.x,pos.y+20), new Vector2(pos.x+dim.x, pos.y+20), 0xffffff, 1.0f);
		
		Fonts.get("Arial").drawString("Browser", pos.x+10, pos.y, 20, 0xffffff);
		
		for(GuiEditorBrowserSection section : sections)
			section.render(interpolation);
	}
	
	
	
}

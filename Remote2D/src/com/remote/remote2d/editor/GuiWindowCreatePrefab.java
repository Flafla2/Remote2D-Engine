package com.remote.remote2d.editor;

import java.util.ArrayList;

import com.remote.remote2d.engine.Remote2DException;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiTextField;
import com.remote.remote2d.engine.gui.GuiWindow;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Map;

public class GuiWindowCreatePrefab extends GuiWindow {
	
	GuiTextField textField;
	GuiButton doneButton;
	String entity;
	ArrayList<String> stringSet;

	public GuiWindowCreatePrefab(GuiEditor holder, Vector2 pos, ColliderBox allowedBounds,Entity e) {
		super(holder, pos, new Vector2(300,
				105+20*
				Fonts.get("Arial").getStringSet("Create Prefab for Entity: "+e.name, 20, 280).size()),
				allowedBounds,
				"Create Prefab");
		
		stringSet = Fonts.get("Arial").getStringSet("Create Prefab for Entity: "+e.name, 20, 280);
		textField = new GuiTextField(new Vector2(10,dim.y-95),new Vector2(dim.x-20,40), 20);
		textField.text = "/res/entity/prefab"+Entity.getExtension()+".xml";
		this.entity = e.getUUID();
	}
	
	@Override
	public void initGui()
	{
		if(textField != null)
			textField.dim = new Vector2(dim.x-20,40);
		
		buttonList.clear();
		buttonList.add(new GuiButton(0,new Vector2(10,dim.y-50),new Vector2(dim.x/2-10,40),"Cancel"));
		buttonList.add(doneButton = new GuiButton(1,new Vector2(dim.x/2+10,dim.y-50),new Vector2(dim.x/2-20,40),"Done"));	
	}

	@Override
	public void renderContents(float interpolation) {
		textField.render(interpolation);
		for(int x = 0;x<stringSet.size();x++)
			Fonts.get("Arial").drawString(stringSet.get(x), 10, 10+20*x, 20, 0xffffff);
	}
	
	@Override
	public void tick(int i, int j, int k)
	{
		super.tick(i,j,k);
		Vector2 mouse = getMouseInWindow(i,j);
		textField.tick((int)mouse.x, (int)mouse.y, k);
		
		if(!Entity.isValidFile(textField.text))
			doneButton.setDisabled(true);
		else if(doneButton.getDisabled())
			doneButton.setDisabled(false);
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
			holder.closeWindow(this);
		else if(button.id == 1)
		{
			try
			{
				Entity e = ((GuiEditor)holder).getMap().getEntityList().getEntityWithUUID(entity);
				R2DFileManager manager = new R2DFileManager(textField.text,null);
				Map.saveEntityFull(e, manager.getCollection(), true);
				manager.write();
				
				e.setPrefabPath(textField.text);
				holder.closeWindow(this);
			}catch(Exception e)
			{
				throw new Remote2DException(e);
			}
		}
	}
	
	@Override
	public boolean canResize()
	{
		return false;
	}

}

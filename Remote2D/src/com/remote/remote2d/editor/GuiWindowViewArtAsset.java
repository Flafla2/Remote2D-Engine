package com.remote.remote2d.editor;

import com.remote.remote2d.engine.art.ResourceLoader;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiTextField;
import com.remote.remote2d.engine.gui.GuiWindow;
import com.remote.remote2d.engine.gui.WindowHolder;
import com.remote.remote2d.engine.io.R2DFileUtility;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiWindowViewArtAsset extends GuiWindow {
	
	GuiTextField field;
	GuiButton doneButton;
	Texture tex;

	public GuiWindowViewArtAsset(WindowHolder holder, Vector2 pos, ColliderBox allowedBounds) {
		super(holder, pos, new Vector2(400,450), allowedBounds, "Art Viewer/Selecter");
		field = new GuiTextField(new Vector2(10,10), new Vector2(380,40), 20);
	}
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		doneButton = new GuiButton(0, new Vector2(20,400), new Vector2(360,40), "Done");
		buttonList.add(doneButton);
	}

	@Override
	public void renderContents(float interpolation) {
		field.render(interpolation);

		if(tex == null)
			reloadTex();
		else if(!field.text.equals(tex.getTextureLocation()))
			reloadTex();
	}
	
	public void reloadTex()
	{
		if(R2DFileUtility.textureExists(field.text))
		{
			tex = ResourceLoader.getTexture(field.text);
		}
	}
	
	@Override
	public void tick(int i, int j, int k)
	{
		super.tick(i, j, k);
		field.tick((int)getMouseInWindow(i, j).x, (int)getMouseInWindow(i, j).y, k);
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			holder.closeWindow(this);
		}
	}
	
	@Override
	public boolean canResize()
	{
		return false;
	}
	
}

package com.remote.remote2d.editor;

import org.lwjgl.opengl.GL11;

import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiWindow;
import com.remote.remote2d.engine.gui.WindowHolder;
import com.remote.remote2d.engine.logic.Collider;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.ColliderLogic;
import com.remote.remote2d.engine.logic.ColliderSphere;
import com.remote.remote2d.engine.logic.Collision;
import com.remote.remote2d.engine.logic.Vector2;


public class GuiWindowGeneralColliderTest extends GuiWindow {
	
	Collider mainCollider;
	Collider moveCollider;
	Collider altMoveCollider;
	Collider rawMoveCollider;
	Vector2 vec = new Vector2(20,20);
	Collision collision = new Collision();

	public GuiWindowGeneralColliderTest(WindowHolder holder, Vector2 pos, ColliderBox allowedBounds) {
		super(holder, pos, new Vector2(500,500), allowedBounds, "Collider Test");
	}
		
	public void initGui()
	{
		buttonList.clear();
		buttonList.add(new GuiButton(0,new Vector2(10,10),new Vector2(150,40),"AA Box"));
		buttonList.add(new GuiButton(1,new Vector2(10,55),new Vector2(150,40),"Sphere"));
		buttonList.add(new GuiButton(2,new Vector2(340,10),new Vector2(150,40),"AA Box"));
		buttonList.add(new GuiButton(3,new Vector2(340,55),new Vector2(150,40),"Sphere"));
		buttonList.add(new GuiButton(4,new Vector2(175,450),new Vector2(150,40),"Done"));
	}

	@Override
	public void renderContents(float interpolation) {
		if(mainCollider != null)
			mainCollider.drawCollider(0xffffff);
		if(moveCollider != null)
			moveCollider.drawCollider(0xffffff);
		
		if(altMoveCollider != null)
			altMoveCollider.drawCollider(0x00ff00);
		
		if(rawMoveCollider != null)
			rawMoveCollider.drawCollider(0x0000ff);
		
		if(collision.collides)
		{
			Fonts.get("Arial").drawString("Colliding!", 10, 200, 20, 0xffffff);
			Fonts.get("Arial").drawString(collision.correction.toString(), 10, 220, 20, 0xffffff);
		}
	}
	
	@Override
	public void tick(int i, int j, int k)
	{
		super.tick(i, j, k);
		altMoveCollider = null;
		rawMoveCollider = null;
		if(mainCollider != null && moveCollider != null)
		{
			Vector2 m = new Vector2(getMouseInWindow(i,j).getElements());
			
			moveCollider = ColliderLogic.setColliderPos(moveCollider, m);
			
			collision = moveCollider.getCollision(mainCollider,vec);
			altMoveCollider = moveCollider.getTransformedCollider(vec.add(collision.correction));
			rawMoveCollider = moveCollider.getTransformedCollider(vec);
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			mainCollider = new ColliderBox(new Vector2(150,175),new Vector2(200,150));
		} else if(button.id == 1)
		{
			mainCollider = new ColliderSphere(new Vector2(250,250),100);
		} else if(button.id == 2)
		{
			moveCollider = new ColliderBox(new Vector2(150,175),new Vector2(200,150));
			moveCollider.isIdle = false;
		} else if(button.id == 3)
		{
			moveCollider = new ColliderSphere(new Vector2(250,250),100);
			moveCollider.isIdle = false;
		} else if(button.id == 4)
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

package com.remote.remote2d.editor;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.gui.GuiMenu;
import com.remote.remote2d.engine.logic.Interpolator;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorHeirarchy extends GuiMenu {
	
	public Vector2 pos = new Vector2(0,20);
	public Vector2 dim;
	private ArrayList<GuiEditorHeirarchySection> sections;
	private GuiEditor editor;
	
	//TODO: Implement scrolling in the heirarchy view.
	private float oldOffset = 0;
	public float offset = 0;
	
	public GuiEditorHeirarchy(Vector2 pos, Vector2 dim, GuiEditor editor)
	{
		this.editor = editor;
		this.pos = pos;
		this.dim = dim;
		
		sections = new ArrayList<GuiEditorHeirarchySection>();
		reloadSections();
	}
	
	@Override
	public void initGui()
	{
		
	}

	@Override
	public void tick(int i, int j, int k) {
		if(getEditor().getMap() == null)
			return;
		
		if(!Mouse.isButtonDown(0) && !isSecAnimating())
		{
			reloadSections();
		}
		
		for(int x=0;x < sections.size();x++)
			sections.get(x).tick(i,j+(int)offset,k);
		
		oldOffset = offset;
		if(pos.getColliderWithDim(dim).isPointInside(new Vector2(i,j)))
		{
			if(Remote2D.getDeltaWheel() < 0)
				offset += 20;
			if(Remote2D.getDeltaWheel() > 0)
				offset -= 20;
			
			if(offset > sections.size()*20-dim.y+20)
				offset = sections.size()*20-dim.y+20;
			if(offset < 0)
				offset = 0;
		}
	}
	
	@Override
	public void render(float interpolation) {
		Renderer.drawRect(pos, dim, 0x000000, 0.5f);
		
		if(getEditor().getMap() == null)
			return;
		
		float offset = (float) Interpolator.linearInterpolate(oldOffset, this.offset, interpolation);
		
		Renderer.startScissor(pos, dim);
		Renderer.pushMatrix();
		Renderer.translate(new Vector2(0,-offset));
		for(int x=0;x < sections.size();x++)
			sections.get(x).render(interpolation);
		
		DraggableObject object = editor.dragObject;
		if(object != null && object instanceof DraggableObjectEntity)
		{
			Vector2 mouse = new Vector2(Remote2D.getMouseCoords().add(new Vector2(0,offset)));
			for(int x=0;x < sections.size();x++)
			{
				if(sections.get(x).pos.getColliderWithDim(sections.get(x).dim).isPointInside(mouse))
				{
					float localY = mouse.y-sections.get(x).pos.y;
					if(localY >=0 && localY <=5 && x > 0)
						Renderer.drawRect(new Vector2(pos.x,sections.get(x).pos.y-2.5f), new Vector2(dim.x,5), 0xaaaaff, 0.5f);
//					else if(localY > 5 && localY < 15 && x != getSelected())
//						Renderer.drawRect(new Vector2(pos.x,sections.get(x).pos.y), new Vector2(dim.x,sections.get(x).dim.y), 0xaaaaff, 0.5f);
					else if(localY >= 15 && localY < 20 && x < sections.size()-1)
						Renderer.drawRect(new Vector2(pos.x,sections.get(x).pos.y+17.5f), new Vector2(dim.x,5), 0xaaaaff, 0.5f);
				}
			}
		}
		Renderer.popMatrix();
		Renderer.endScissor();
	}
	
	public void updateSelected()
	{
		for(int x=0;x<sections.size();x++)
			if(sections.get(x).selected)
				getEditor().setSelectedEntity(sections.get(x).uuid);
	}
	
	public void animateSections()
	{
		int selectedIndex = getSelected();
		GuiEditorHeirarchySection sec = null;
		if(selectedIndex != -1)
			sec = sections.get(selectedIndex);
		int newSelectedIndex = -1;
		if(sec != null && pos.getColliderWithDim(dim).isPointInside(new Vector2(Remote2D.getMouseCoords())))
			newSelectedIndex = (int)(Remote2D.getMouseCoords().y-pos.y)/20;
		else if(sec != null)
			newSelectedIndex = selectedIndex;
		
		if(newSelectedIndex < 0)
			newSelectedIndex = 0;
		if(newSelectedIndex >= getEditor().getMap().getEntityList().size()-1)
			newSelectedIndex = getEditor().getMap().getEntityList().size()-1;
		
		Entity e = getEditor().getMap().getEntityList().get(selectedIndex);
		getEditor().getMap().getEntityList().removeEntityFromList(e);
		getEditor().getMap().getEntityList().addEntityToList(e,newSelectedIndex);
		
		sections.remove(sec);
		sections.add(newSelectedIndex, sec);
		
		float currentYPos = pos.y;
		for(int x=0;x<sections.size();x++)
		{
			sections.get(x).setKeyframe(new Vector2(pos.x,currentYPos), 100);
			currentYPos += 20;
		}
	}
	
	public void setAllUnselected()
	{
		for(int x=0;x<sections.size();x++)
			sections.get(x).selected = false;
	}
	
	private boolean isSecAnimating()
	{
		for(int x=0;x<sections.size();x++)
		{
			if(sections.get(x).isAnimating())
				return true;
		}
		return false;
	}
	
	private int getSelected()
	{
		for(int x=0;x<sections.size();x++)
		{
			if(sections.get(x).selected)
				return x;
		}
		return -1;
	}
	
	private void reloadSections()
	{
		sections.clear();
		if(getEditor().getMap() == null)
			return;
		
		float currentYPos = pos.y;
		for(int x=0;x<getEditor().getMap().getEntityList().size();x++)
		{
			Entity n = getEditor().getMap().getEntityList().get(x);
			String name = n.name;
			if(name.equals(""))
				name = "Untitled";
			GuiEditorHeirarchySection sec = new GuiEditorHeirarchySection(this,name,n.getUUID(),new Vector2(pos.x,currentYPos),new Vector2(dim.x,20));
			if(n.getUUID().equals(editor.getSelectedEntity()))
				sec.selected = true;
			sections.add(sec);
			
			currentYPos += 20;
		}
		
	}
	
	public Entity getEntityForSec(GuiEditorHeirarchySection section)
	{
		for(int x=0;x<sections.size();x++)
		{
			if(sections.get(x).equals(section))
				return editor.getMap().getEntityList().get(x);
		}
		return null;
	}

	public GuiEditor getEditor() {
		return editor;
	}
	
	public boolean recieveDraggableObject(DraggableObject object)
	{
		if(object != null && object instanceof DraggableObjectEntity)
		{
			Vector2 mouse = new Vector2(Remote2D.getMouseCoords());
			for(int x=0;x < sections.size();x++)
			{
				DraggableObjectEntity drag = (DraggableObjectEntity)object;
				Entity e = editor.getMap().getEntityList().getEntityWithUUID(drag.uuid);
				int index = editor.getMap().getEntityList().indexOf(e);
				if(sections.get(x).pos.subtract(new Vector2(0,offset)).getColliderWithDim(sections.get(x).dim).isPointInside(mouse))
				{
					float localY = mouse.y+offset-sections.get(x).pos.y;
					if(localY >= 0 && localY <= 5)
					{
						moveEntity(e,sections.get(x).uuid);
						return true;
					}
					else if(localY > 5 && localY < 15)
					{
						//TODO: Add children
						return false;
					}
					else if(localY >= 15 && localY < 20)
					{
						if(x == sections.size()-1)
						{
							editor.getMap().getEntityList().removeEntityFromList(e);
							editor.getMap().getEntityList().addEntityToList(e,x);
						}else
							moveEntity(e,sections.get(x+1).uuid);
						return true;
					}
				} else if (x == sections.size()-1 && pos.getColliderWithDim(dim).isPointInside(mouse) && mouse.y > sections.get(x).pos.y+sections.get(x).dim.y)
				{
					editor.getMap().getEntityList().removeEntityFromList(index);
					editor.getMap().getEntityList().addEntityToList(e,x);
					return true;
				}
			}
		}
		return false;
	}
	
	private void moveEntity(Entity before, String after)
	{
		if(before.getUUID().equals(after))
			return;
		editor.getMap().getEntityList().removeEntityFromList(before);
		editor.getMap().getEntityList().add(after, before);
	}
	
}

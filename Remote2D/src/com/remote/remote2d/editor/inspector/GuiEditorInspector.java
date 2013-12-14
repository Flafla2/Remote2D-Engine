package com.remote.remote2d.editor.inspector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import com.remote.remote2d.editor.DraggableObject;
import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.editor.operation.OperationEditEntity;
import com.remote.remote2d.editor.operation.OperationEditPrefab;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.entity.EditorObject;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiMenu;
import com.remote.remote2d.engine.io.R2DFileUtility;
import com.remote.remote2d.engine.io.R2DType;
import com.remote.remote2d.engine.io.R2DTypeCollection;
import com.remote.remote2d.engine.logic.Interpolator;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Map;

public class GuiEditorInspector extends GuiMenu {
		
	private String currentEntity;
	private String prefabPath;
	private ArrayList<EditorObjectWizard> wizards;
	private GuiButton button;
	private GuiEditor editor;
	public float offset = 0;
	private float lastOffset = 0;
	public Vector2 pos;
	public Vector2 dim;
	
	public static final String[] whitelist = {"name","pos","rotation","uuid"};

	public GuiEditorInspector(Vector2 pos, Vector2 dim, GuiEditor editor)
	{
		super();
		this.pos = pos;
		this.dim = dim;
		this.editor = editor;
		wizards = new ArrayList<EditorObjectWizard>();
		initGui();
	}
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		if(pos != null && dim != null)
		{
			button = new GuiButton(0,new Vector2(pos.x+dim.x/2-100,pos.y+dim.y-20),new Vector2(200,20),"Apply");
			button.setDisabled(currentEntity == null);
			buttonList.add(button);
		}
	}
	
	@Override
	public void tick(int i, int j, int k) {
		super.tick(i, j, k);
		
		lastOffset = offset;
		
		if(pos.getColliderWithDim(dim).isPointInside(new Vector2(i,j)))
		{
			if(Remote2D.getDeltaWheel() < 0)
				offset += 20;
			if(Remote2D.getDeltaWheel() > 0)
				offset -= 20;
			
			if(offset > getTotalComponentHeight()-dim.y+20)
				offset = getTotalComponentHeight()-dim.y+20;
			if(offset < 0)
				offset = 0;
		}
		
		for(int x=0;x<wizards.size();x++)
		{
			wizards.get(x).tick(i,(int)(j+offset),k);
			
			//int changed = wizards.get(x).hasFieldBeenChanged();
			//if(changed != -1)
			//{
			//	wizards.get(x).setComponentField(changed);
			//	if(currentEntity instanceof Entity)
			//		editor.replaceSelectedEntity((Entity)currentEntity);
			//}
		}
	}
	
	@Override
	public void renderBackground(float interpolation)
	{
		Renderer.drawRect(pos, dim, 0x000000, 0.5f);
	}
	
	@Override
	public void render(float interpolation) {
		super.render(interpolation);
		
		Renderer.pushMatrix();
		Renderer.startScissor(new Vector2(pos.x,pos.y), new Vector2(dim.x,dim.y-20));
		Renderer.translate(new Vector2(0, -(float)Interpolator.linearInterpolate(lastOffset, offset, interpolation)));
		
		for(int x=0;x<wizards.size();x++)
		{
			wizards.get(x).render(interpolation);
		}
		Renderer.endScissor();
		Renderer.popMatrix();
	}
	
	public float getScrollOffset(float interpolation)
	{
		return (float)Interpolator.linearInterpolate(lastOffset, offset, interpolation);
	}
	
	public void setCurrentEntity(String o)
	{
		this.currentEntity = o;
		
		wizards.clear();
		button.setDisabled(currentEntity == null && prefabPath == null);
		if(currentEntity==null)
			return;
		
		Entity e = editor.getMap().getEntityList().getEntityWithUUID(currentEntity);
		prefabPath = null;
		
		Vector2 currentPos = pos.copy();
		EditorObjectWizard ew = new EditorObjectWizard(editor,e,currentPos,(int)dim.x);
		wizards.add(ew);
		if(e.getPrefabPath() != null)
			ew.title += " (Prefab: "+e.getPrefabPath()+")";
		currentPos.y += ew.getHeight();
		
		for(int x=0;x<e.getComponents().size();x++)
		{
			EditorObjectWizard cw = new EditorObjectWizard(editor,e.getComponents().get(x),currentPos,(int)dim.x);
			wizards.add(cw);
			currentPos.y += cw.getHeight();
		}
	}
	
	public void setPrefab(String path)
	{
		if(path == null || !R2DFileUtility.R2DExists(path))
			return;
		this.prefabPath = path;
		wizards.clear();
		button.setDisabled(currentEntity == null && prefabPath == null);
		if(prefabPath == null)
			return;
		
		currentEntity = null;
		
		Entity e = new Entity(editor.getMap());
		e.setPrefabPath(path);
		
		Vector2 currentPos = pos.copy();
		EditorObjectWizard ew = new EditorObjectWizard(editor,e,currentPos,(int)dim.x);
		ew.title = path;
		wizards.add(ew);
		currentPos.y += ew.getHeight();
		
		for(int x=0;x<e.getComponents().size();x++)
		{
			EditorObjectWizard cw = new EditorObjectWizard(editor,e.getComponents().get(x),currentPos,(int)dim.x);
			wizards.add(cw);
			currentPos.y += cw.getHeight();
		}
	}
	
	public boolean recieveDraggableObject(DraggableObject drag)
	{
		for(int x=0;x<wizards.size();x++)
		{
			if(wizards.get(x).recieveDraggableObject(drag))
				return true;
		}
		return false;
	}
	
	public void apply()
	{
		if(currentEntity != null)
		{
			Entity before = editor.getMap().getEntityList().getEntityWithUUID(currentEntity).clone();
			Entity after = editor.getMap().getEntityList().getEntityWithUUID(currentEntity).clone();
						
			EditorObject e = wizards.get(0).getObject();
			EditorObject[] comps = new EditorObject[wizards.size()-1];
			wizards.get(0).setObject(after);
			for(int x=0;x<after.getComponents().size();x++)
			{
				comps[x] = wizards.get(x+1).getObject();
				wizards.get(x+1).setObject(after.getComponents().get(x));
			}
			for(int x=0;x<wizards.size();x++)
			{
				wizards.get(x).setComponentFields();
			}
			wizards.get(0).setObject(e);
			for(int x=0;x<after.getComponents().size();x++)
				wizards.get(x+1).setObject(comps[x]);
			
			boolean changed = hasEntityBeenChanged(before,after);
			if(changed)
				after.setPrefabPath(null);
			
			if(before.getPrefabPath() == null)
				editor.executeOperation(new OperationEditEntity(editor,before,after));
			else if(changed)
				editor.confirmOperation(new OperationEditEntity(editor,before,after));
		}
		
		if(prefabPath != null && wizards.size() > 0)
		{
			for(int x=0;x<wizards.size();x++)
			{
				wizards.get(x).setComponentFields();
			}
			
			EditorObject o = wizards.get(0).getObject();
			if(o instanceof Entity)
			{
				Entity e = (Entity)o;
				R2DTypeCollection coll = new R2DTypeCollection("Prefab");
				Map.saveEntityFull(e, coll, true);
				editor.confirmOperation(new OperationEditPrefab(editor,prefabPath,coll));
			}
		}
	}
	
	public boolean isTyping()
	{
		for(int x=0;x<wizards.size();x++)
			if(wizards.get(x).isFieldSelected())
				return true;
		return false;
	}
	
	public int getTotalComponentHeight()
	{
		int height = 0;
		for(int x=0;x<wizards.size();x++)
		{
			height += wizards.get(x).getHeight();
		}
		return height;
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			apply();
		}
	}
	
	public boolean hasEntityBeenChanged(Entity before, Entity after)
	{
		R2DTypeCollection collBefore = new R2DTypeCollection("Entity1");
		Map.saveEntityFull(before, collBefore, true);
		R2DTypeCollection collAfter = new R2DTypeCollection("Entity2");
		Map.saveEntityFull(after, collAfter, true);
		
		Iterator<Entry<String,R2DType>> iterator = collBefore.getDataIterator();
		while(iterator.hasNext())
		{
			Entry<String,R2DType> next = iterator.next();
			
			if(!Arrays.asList(whitelist).contains(next.getKey()) && !next.getValue().equals(collAfter.getType(next.getKey())))
				return true;
		}
		return false;
	}

}

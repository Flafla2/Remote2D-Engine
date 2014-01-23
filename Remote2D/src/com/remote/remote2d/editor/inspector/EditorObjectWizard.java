package com.remote.remote2d.editor.inspector;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.editor.DraggableObject;
import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Animation;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Material;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.entity.EditorObject;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.logic.Vector2;

public class EditorObjectWizard {
	
	private EditorObject object;
	private GuiEditor editor;
	private Vector2 pos;
	private int width;
	public ArrayList<GuiEditorInspectorSection> sections;
	public String title;
	
	public EditorObjectWizard(GuiEditor editor, EditorObject c, Vector2 pos, int width)
	{
		object = c;
		title = object.getClass().getSimpleName();
		this.editor = editor;
		this.pos = pos.copy();
		this.width = width;
		
		Field[] fields = object.getClass().getFields();
		sections = new ArrayList<GuiEditorInspectorSection>();
		Vector2 currentPos = pos.add(new Vector2(0,20));
		for(int x=0;x<fields.length;x++)
		{
			if(Modifier.isPublic(fields[x].getModifiers()))
			{
				try {
					Object o = fields[x].get(object);
					Class<?> type = fields[x].getType();
					String name = fields[x].getName();
					
					if(type == int.class)
					{
						GuiEditorInspectorSectionInt sec = new GuiEditorInspectorSectionInt(name,editor,currentPos,width);
						if(o != null)
							sec.textField.text = o+"";
						sections.add(sec);
						currentPos.y += sec.sectionHeight();
					} else if(type == String.class)
					{
						GuiEditorInspectorSectionString sec = new GuiEditorInspectorSectionString(name,editor,currentPos,width);
						if(o != null)
							sec.textField.text = (String)o+"";
						sections.add(sec);
						currentPos.y += sec.sectionHeight();
					} else if(type == float.class)
					{
						GuiEditorInspectorSectionFloat sec = new GuiEditorInspectorSectionFloat(name,editor,currentPos,width);
						if(o != null)
							sec.textField.text = o+"";
						sections.add(sec);
						currentPos.y += sec.sectionHeight();
					}  else if(type == double.class)
					{
						GuiEditorInspectorSectionDouble sec = new GuiEditorInspectorSectionDouble(name,editor,currentPos,width);
						if(o != null)
							sec.textField.text = o+"";
						sections.add(sec);
						currentPos.y += sec.sectionHeight();
					} else if(type == Vector2.class)
					{
						GuiEditorInspectorSectionVec2D sec = new GuiEditorInspectorSectionVec2D(name,editor,currentPos,width);
						if(o != null)
						{
							sec.textField1.text = ((Vector2)o).x+"";
							sec.textField2.text = ((Vector2)o).y+"";
						}
						sections.add(sec);
						currentPos.y += sec.sectionHeight();
					} else if(type == Texture.class)
					{
						GuiEditorInspectorSectionTexture sec = new GuiEditorInspectorSectionTexture(name,editor,currentPos,width);
						if(o != null)
							sec.textField.text = ((Texture)o).getTextureLocation();
						sections.add(sec);
						currentPos.y += sec.sectionHeight();
					} else if(type == boolean.class)
					{
						GuiEditorInspectorSectionBoolean sec = new GuiEditorInspectorSectionBoolean(name,editor,currentPos,width);
						sec.setData(o);
						sections.add(sec);
						currentPos.y += sec.sectionHeight();
					} else if(type == Animation.class)
					{
						GuiEditorInspectorSectionAnimation sec = new GuiEditorInspectorSectionAnimation(name,editor,currentPos,width);
						if(o != null)
							sec.textField.text = ((Animation)o).getPath();
						sections.add(sec);
						currentPos.y += sec.sectionHeight();
					} else if(type == Color.class)
					{
						GuiEditorInspectorSectionColor sec = new GuiEditorInspectorSectionColor(name,editor,currentPos,width);
						sec.setData(o);
						sections.add(sec);
						currentPos.y += sec.sectionHeight();
					} else if(type == Entity.class)
					{
						GuiEditorInspectorSectionEntity sec = new GuiEditorInspectorSectionEntity(name,editor,currentPos,width);
						sec.setData(o);
						sections.add(sec);
						currentPos.y += sec.sectionHeight();
					} else if(type == Material.class)
					{
						GuiEditorInspectorSectionSetMaterial sec = new GuiEditorInspectorSectionSetMaterial(name,editor,currentPos,width);
						sec.setData((Material)o);
						sections.add(sec);
						currentPos.y += sec.sectionHeight();
					}
				} catch (Exception e) {}
				
			}
		}
	}
	
	public void setComponentFields()
	{
		for(int x=0;x<sections.size();x++)
		{
			setComponentField(x);
		}
		
		object.apply();
	}
	
	public void setComponentField(int x)
	{
		try {
			Field field = object.getClass().getField(sections.get(x).name);
			field.set(object, sections.get(x).getData());
		} catch (NoSuchFieldException e) {
			Log.error("Field doesn't exist: "+sections.get(x).name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deselectFields()
	{
		for(int x=0;x<sections.size();x++)
			sections.get(x).deselect();
	}
	
	public boolean isFieldSelected()
	{
		for(int x=0;x<sections.size();x++)
			if(sections.get(x).isSelected())
				return true;
		return false;
	}
	
	public int getHeight()
	{
		int height = 20;
		for(int x=0;x<sections.size();x++)
			height += sections.get(x).sectionHeight();
		return height;
	}
	
	public int hasFieldBeenChanged()
	{
		for(int x=0;x<sections.size();x++)
			if(sections.get(x).hasFieldBeenChanged())
				return x;
		return -1;
	}
	
	public void tick(int i, int j, int k)
	{
		for(int x=0;x<sections.size();x++)
			sections.get(x).tick(i, j, k);
	}
	
	public boolean recieveDraggableObject(DraggableObject drag)
	{
		Vector2 mouse = Remote2D.getMouseCoords();
		Vector2 mouseVec = mouse.add(new Vector2(0,editor.getInspector().offset));
		for(int x=0;x<sections.size();x++)
		{
			Vector2 secDim = new Vector2(width,sections.get(x).sectionHeight());
			boolean inside = sections.get(x).pos.getColliderWithDim(secDim).isPointInside(mouseVec);
			if(inside && sections.get(x).acceptsDraggableObject(drag))
			{
				sections.get(x).acceptDraggableObject(drag);
				return true;
			}
		}
		return false;
	}
	
	public void render(float interpolation)
	{
		Vector2 mouse = Remote2D.getMouseCoords();
		Vector2 mouseVec = mouse.add(new Vector2(0,editor.getInspector().offset));
		for(int x=0;x<sections.size();x++)
		{
			
			Vector2 secDim = new Vector2(width,sections.get(x).sectionHeight());
			boolean inside = sections.get(x).pos.getColliderWithDim(secDim).isPointInside(mouseVec);
			if(editor.dragObject != null && sections.get(x).acceptsDraggableObject(editor.dragObject) && inside && !(sections.get(x) instanceof GuiEditorInspectorSectionSet))
			{
				Renderer.drawRect(sections.get(x).pos, new Vector2(width,sections.get(x).sectionHeight()), 0xffffff, 0.5f);
			}
			sections.get(x).render(interpolation);
		}
		
		Renderer.drawLine(new Vector2(pos.x,pos.y+20), new Vector2(pos.x+width,pos.y+20), 0xffffff, 1.0f);
		Renderer.drawLine(new Vector2(pos.x,pos.y+getHeight()), new Vector2(pos.x+width,pos.y+getHeight()), 0xffffff, 1.0f);
		
		Fonts.get("Arial").drawString(title, pos.x, pos.y, 20, 0xffffff);
	}

	public EditorObject getObject() {
		return object;
	}
	
	/**
	 * Sets the object to use in this wizard.
	 * @param e The object to replace with.  MUST be the same class as the original object.
	 */
	public void setObject(EditorObject e)
	{
		if(e.getClass() != object.getClass())
			return;
		object = e;
	}
	
}

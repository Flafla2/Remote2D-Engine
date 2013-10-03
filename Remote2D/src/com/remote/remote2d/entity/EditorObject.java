package com.remote.remote2d.entity;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.UUID;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.Remote2D;
import com.remote.remote2d.art.Animation;
import com.remote.remote2d.art.Material;
import com.remote.remote2d.art.Material.RenderType;
import com.remote.remote2d.art.Texture;
import com.remote.remote2d.entity.component.Component;
import com.remote.remote2d.io.R2DFileSaver;
import com.remote.remote2d.io.R2DTypeCollection;
import com.remote.remote2d.logic.Vector2;
import com.remote.remote2d.world.Map;

/**
 * If a class implements this interface, then it is able to be edited within the
 * editor.  This assumes any public variables(not methods, getters/setters, etc.)
 * are able to be edited through the inspector pane in the editor.
 * 
 * @author Flafla2
 */
public abstract class EditorObject implements R2DFileSaver {
	
	private String uuid;
	protected Map map;
	
	public EditorObject(Map map, String uuid)
	{
		this.uuid = uuid;
		if(this.uuid == null)
			this.uuid = UUID.randomUUID().toString();
		this.map = map;
	}
	
	/**
	 * Called when this object is changed in the editor.
	 */
	public abstract void apply();
	
	
	public void saveR2DFile(R2DTypeCollection collection)
	{
		collection.setString("uuid", uuid);
		Field[] fields = this.getClass().getFields();
		for(int x=0;x<fields.length;x++)
		{
			if(Modifier.isPublic(fields[x].getModifiers()))
			{
				try {
					Object o = fields[x].get(this);
					if(o instanceof Integer)
					{
						collection.setInteger(fields[x].getName(), (Integer)o);
					} else if(o instanceof String)
					{
						collection.setString(fields[x].getName(), (String)o);
					} else if(o instanceof Float)
					{
						collection.setFloat(fields[x].getName(), (Float)o);
					} else if(o instanceof Vector2)
					{
						collection.setVector2D(fields[x].getName(), (Vector2)o);
					} else if(o instanceof Texture)
					{
						collection.setString(fields[x].getName(), ((Texture)o).getTextureLocation());
					} else if(o instanceof Boolean)
					{
						collection.setBoolean(fields[x].getName(), (Boolean)o);
					} else if(o instanceof Animation)
					{
						collection.setString(fields[x].getName(), ((Animation)o).getPath());
					} else if(o instanceof Color)
					{
						collection.setInteger(fields[x].getName(), ((Color)o).getRGB());
					} else if(o instanceof Entity)
					{
						collection.setString(fields[x].getName(), ((Entity)o).getUUID());
					} else if(o instanceof Material)
					{
						Material mat = (Material)o;
						R2DTypeCollection matColl = new R2DTypeCollection(fields[x].getName());
						matColl.setByte("RenderType", mat.renderTypeToByte(mat.getRenderType()));
						matColl.setInteger("Color", mat.getColor());
						if(mat.getTexture() != null)
							matColl.setString("Texture", mat.getTexture().getTextureLocation());
						if(mat.getAnimation() != null)
							matColl.setString("Animation", mat.getAnimation().getPath());
						matColl.setFloat("Alpha", mat.getAlpha());
						collection.setCollection(matColl);
					} else
					{
						String s = "Unknown Data Type "+o.getClass().getName()+" is a public variable of Component: "+fields[x].getName()+".";
						if(o instanceof Component)
							s += "  Use Entity with getComponent() instead!";
						else if(o instanceof ArrayList)
							s += " ArrayList currently not supported!";
						Log.warn(s);
					}
				} catch (Exception e) {}
				
			}
		}
	}
	public void loadR2DFile(R2DTypeCollection collection)
	{
		uuid = collection.getString("uuid");
		Field[] fields = this.getClass().getFields();
		for(int x=0;x<fields.length;x++)
		{
			if(Modifier.isPublic(fields[x].getModifiers()))
			{
				try {
					Field field = fields[x];
					if(field.getType() == int.class)
						field.set(this, collection.getInteger(field.getName()));
					else if(field.getType() == String.class)
						field.set(this, collection.getString(field.getName()));
					else if(field.getType() == float.class)
						field.set(this, collection.getFloat(field.getName()));
					else if(field.getType() == long.class)
						field.set(this, collection.getLong(field.getName()));
					else if(field.getType() == Texture.class)
						field.set(this, new Texture(collection.getString(field.getName())));
					else if(field.getType() == Vector2.class)
						field.set(this, collection.getVector2D(field.getName()));
					else if(field.getType() == Animation.class)
						field.set(this, Remote2D.getInstance().artLoader.getAnimation(collection.getString(field.getName())));
					else if(field.getType() == boolean.class)
						field.set(this, collection.getBoolean(field.getName()));
					else if(field.getType() == Color.class)
						field.set(this, new Color(collection.getInteger(field.getName())));
					else if(field.getType() == Entity.class)
					{
						String uuid = collection.getString(field.getName());
						Entity e = null;
						if(!uuid.trim().equals(""))
							e = map.getEntityList().getEntityWithUUID(uuid,true);
						field.set(this, e);
					} else if(field.getType() == Material.class)
					{
						R2DTypeCollection matColl = collection.getCollection(fields[x].getName());
						RenderType renderType = Material.byteToRenderType(matColl.getByte("RenderType"));
						int color = matColl.getInteger("Color");
						Texture tex = null;
						if(matColl.getString("Texture") != null && !matColl.getString("Texture").trim().equals(""))
							tex = new Texture(matColl.getString("Texture"));
						Animation anim = null;
						if(matColl.getString("Animation") != null && !matColl.getString("Animation").trim().equals(""))
							anim = new Animation(matColl.getString("Animation"));
						float alpha = matColl.getFloat("Alpha");
						field.set(this, new Material(renderType,tex,anim,color,alpha));
					}
					else
					{
						String s = "Unknown Data Type "+field.getType().getName()+" is a public variable of Component: "+fields[x].getName()+".";
						if(field.getType() == Entity.class)
							s += "  Use EntityPointer instead!";
						else if(field.getType() == Component.class)
							s += "  Use EntityPointer with getComponent() instead!";
						Log.warn(s);
					}
					
				} catch (Exception e) {}
			}
		}
	}
	
	public Map getMap() {
		return map;
	}	
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof EditorObject)
			return this.getUUID().equals(((EditorObject)o).getUUID());
		return false;
	}
	
	public String getUUID()
	{
		return uuid;
	}
	
}

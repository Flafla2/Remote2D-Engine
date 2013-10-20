package com.remote.remote2d.engine.entity;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.remote.remote2d.engine.Remote2DException;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.entity.component.Component;
import com.remote.remote2d.engine.world.Map;

/**
 * A big list of all of the Entities that are available.  When spawning an Entity into
 * the world, you need to add it to the entity list if you expect it to get tick()ed
 * or render()ed.
 * 
 * @author Flafla2
 */
public class EntityList {
	
	/**
	 * The actual entity list.
	 */
	private ArrayList<Entity> entityList;
	private Map map;
	
	public EntityList(Map map)
	{
		 entityList = new ArrayList<Entity>();
		 this.map = map;
	}
	
	public void addEntityToList(Entity e)
	{
		entityList.add(e);
	}
	
	public void addEntityToList(Entity e,int i)
	{
		entityList.add(i,e);
	}
	
	/**
	 * Calls {@link Entity#spawnEntityInWorld()} for each Entity in the list, which
	 * in turn calls {@link Component#onEntitySpawn()} for each component.
	 * 
	 * @see Component#onEntitySpawn()
	 */
	public void spawn()
	{
		for(int i=0;i<entityList.size();i++)
		{
			entityList.get(i).spawnEntityInWorld();
		}
	}
	
	public void removeEntityFromList(Entity e)
	{
		for(int i=0;i<entityList.size();i++)
			if(entityList.get(i).equals(e))
				entityList.remove(i);
	}
	
	public void removeEntityFromList(int i)
	{
		entityList.remove(i);
	}
	
	/**
	 * Renders all entities in this list.
	 * 
	 * @see Entity#render(boolean, float)
	 */
	public void render(boolean editor, float interpolation)
	{
		for(int i=0;i<entityList.size();i++)
		{
			try
			{
				renderEntity(entityList.get(i),editor,interpolation);
			} catch(Exception e)
			{
				if(editor)
				{
					GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
					Renderer.drawCrossRect(entityList.get(i).pos, entityList.get(i).dim, 0xffffff, 1.0f);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				}
				else
					throw new Remote2DException(e);
			}
		}
	}
	
	/**
	 * Renders a specific entity in this list, including components.
	 * @param e Entity to render
	 * @see Entity#render(boolean, float)
	 */
	private void renderEntity(Entity e, boolean editor, float interpolation)
	{
		ArrayList<Component> components = e.getComponents();
		for(int x=0;x<components.size();x++)
			components.get(x).renderBefore(editor, interpolation);
		e.render(editor,interpolation);
		for(int x=components.size()-1;x>=0;x--)
			components.get(x).renderAfter(editor, interpolation);
	}
	
	/**
	 * Ticks all entities in this list.
	 * @see Entity#tick(int,int,int)
	 */
	public void tick(int i, int j, int k)
	{
		for(int x=0;x<entityList.size();x++)
		{
			entityList.get(x).tick(i, j, k);
		}
	}
	
	public void set(int x, Entity e)
	{
		entityList.set(x, e);
	}
	
	public int size()
	{
		return entityList.size();
	}
	
	public int indexOf(Entity e)
	{
		return entityList.indexOf(e);
	}
	
	public Entity get(int index)
	{
		return entityList.get(index);
	}
	
	/**
	 * Searches the entity list for the given UUID. Equivalent to {@link #getEntityWithUUID(String, boolean)} where mapLoad is false.
	 * @param uuid The UUID that is being searched for.
	 * @return An entity with the given UUID
	 */
	public Entity getEntityWithUUID(String uuid)
	{
		return getEntityWithUUID(uuid,false);
	}
	
	/**
	 * Searches the entity list for the given UUID.
	 * @param uuid The UUID that is being searched for.
	 * @param mapLoad This is used by the map loader to automatically create new Entities when none can be found.
	 * @return An entity with the given UUID
	 */
	public Entity getEntityWithUUID(String uuid, boolean mapLoad)
	{
		for(Entity e : entityList)
		{
			if(e.getUUID().equals(uuid))
				return e;
		}
		if(mapLoad)
		{
			Entity newEnt = new Entity(map,"",uuid);
			entityList.add(newEnt);
			return newEnt;
		}
		return null;
	}

	public void clear() {
		entityList.clear();
	}
	
}

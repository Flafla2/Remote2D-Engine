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
	
	/**
	 * Creates a new EntityList based on a parent map
	 */
	public EntityList(Map map)
	{
		 entityList = new ArrayList<Entity>();
		 this.map = map;
	}
	
	/**
	 * Adds an entity to the list at the end of the list
	 * @param e Entity to add
	 */
	public void addEntityToList(Entity e)
	{
		entityList.add(e);
	}
	
	/**
	 * Adds an entity to the list at a given index
	 * @param e Entity to add
	 * @param i Index to add to
	 */
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
	
	/**
	 * Removes a given entity from the list.
	 * @param e Entity to remove
	 */
	public void removeEntityFromList(Entity e)
	{
		for(int i=0;i<entityList.size();i++)
			if(entityList.get(i).equals(e))
				entityList.remove(i);
	}
	
	/**
	 * Removes the entity at the given index from the list
	 * @param i Index to remove from
	 */
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
				entityList.get(i).render(editor,interpolation);
			} catch(Exception e)
			{
				if(editor)
				{
					GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
					Renderer.drawCrossRect(entityList.get(i).getPosGlobal(interpolation), entityList.get(i).dim, 0xffffff, 1.0f);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				}
				else
					throw new Remote2DException(e);
			}
		}
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
	
	/**
	 * Sets the entity at the given index to the given entity
	 * @param i Index to set
	 * @param e Entity to set at the given index
	 */
	public void set(int i, Entity e)
	{
		entityList.set(i, e);
	}
	
	/**
	 * Size of the list
	 */
	public int size()
	{
		return entityList.size();
	}
	
	/**
	 * The index of the given entity in the list
	 * @param e Entity to search for
	 */
	public int indexOf(Entity e)
	{
		return entityList.indexOf(e);
	}
	
	/**
	 * @param index Index to get from
	 * @return The Entity at the given index of the list.
	 */
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
	
	/**
	 * Clears the list completely
	 */
	public void clear() {
		entityList.clear();
	}

	/**
	 * Adds the given entity just before the entity with the given UUID.  Accounts for child entities.
	 * @param uuidRep UUID of the entity to add before
	 * @param e Entity to add
	 */
	public void add(String uuidRep, Entity e)
	{
		for(int x=0;x<entityList.size();x++)
		{
			Entity ent = entityList.get(x);
			if(ent.getUUID().equals(uuidRep))
			{
				entityList.add(x, e);
				x++; // Otherwise would go into an infinite loop
			}
		}
	}
	
}

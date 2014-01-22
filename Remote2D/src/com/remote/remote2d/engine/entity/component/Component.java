package com.remote.remote2d.engine.entity.component;

import java.lang.reflect.Constructor;

import com.remote.remote2d.engine.Remote2DException;
import com.remote.remote2d.engine.entity.EditorObject;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.io.R2DTypeCollection;
import com.remote.remote2d.engine.logic.Collider;

/**
 * A component is an "attachment" to an entity, which modifies its behavior.
 * Components are advantageous because they can be reused on entities, allowing
 * more to be done in-editor and less in code.
 * 
 * A component MUST FILL these requirements:
 * <ol>
 * 	<li>MUST be a direct subclass of component (can't be subclass of subclass of Component)</li>
 * 	<li>Constructor CAN NOT have parameters, and must call super()</li>
 * </ol>
 * 
 * @author Flafla2
 */
public abstract class Component extends EditorObject{
	
	protected Entity entity;
	
	public Component()
	{
		super(null,null);
	}
	
	public Class<?> getComponentClass()
	{
		return this.getClass();
	}
	
	/**
	 * A normal tick method for this component IN GAME.  Not in the editor!  Note:
	 * if the entity is static then this isn't called.
	 */
	public abstract void tick(int i, int j, int k);
	/**
	 * A render method for before the entity renders (but not necessarily directly
	 * before; other components may get in between).
	 */
	public abstract void renderBefore(boolean editor, float interpolation);
	/**
	 * Triggered when the entity spawns into the world.  Mostly used for components
	 * that set variables for the entity.
	 */
	public abstract void onEntitySpawn();
	/**
	 * A render method for after the entity renders (not necessarily directly
	 * after; other components may get in between).
	 */
	public abstract void renderAfter(boolean editor, float interpolation);
	/**
	 * Called upon initializing this Component.  Use this instead of a constructor!
	 */
	public abstract void init();
	
	/**
	 * @return An array of colliders (local to this entity - in other words {@link Entity#pos}
	 * should be the origin of these colliders) that should be considered by {@link com.remote.remote2d.engine.world.Map#collidesWithMap(Collider)}
	 * and {@link com.remote.remote2d.engine.world.Map#getCorrection(Collider)}, or null if none exist.
	 */
	public Collider[] getColliders()
	{
		return null;
	}
	
	public static Component newInstanceWithEntity(Class<?> componentClass, Entity entity)
	{
        Constructor<?> ctor;
		try {
			ctor = componentClass.getConstructor();
			Object instance = ctor.newInstance();
			if(instance instanceof Component)
			{
				Component ret = (Component)instance;
	        	ret.setEntity(entity);
	        	ret.init();
				return ret;
			}
		} catch (Exception e) {
			throw new Remote2DException(e);
		}
		
        return null;
	}
	
	@Override
	public Component clone()
	{
		R2DTypeCollection compile = new R2DTypeCollection("Component Clone");
		saveR2DFile(compile);
		Component clone = newInstanceWithEntity(getComponentClass(),entity);
		clone.loadR2DFile(compile);
  		return clone;
	}
		
	public void setEntity(Entity e)
	{
		this.entity = e;
		this.map = e.getMap();
	}
	
}

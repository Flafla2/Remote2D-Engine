package com.remote.remote2d.engine.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.remote.remote2d.engine.entity.component.Component;

/**
 * A list of all components that can be inserted within the editor, as
 * well as saved/loaded in the engine.  You MUST add a component to this
 * list if you wish to use it.
 * 
 * @author Flafla2
 */
public class InsertableComponentList {
	
	private HashMap<String,Class<?>> insertableComponents;
	
	public InsertableComponentList()
	{
		insertableComponents = new HashMap<String,Class<?>>();
	}
	
	public void addInsertableComponent(String s, Class<?> e)
	{
		if(Component.class.isAssignableFrom(e))
			insertableComponents.put(s, e);
		else
			throw new IllegalArgumentException("Class "+e.getSimpleName()+" does not directly extend Component!");
	}
	
	public Component getComponentWithEntity(String s,Entity entity)
	{
		return Component.newInstanceWithEntity(insertableComponents.get(s), entity);
	}
	
	public boolean containsComponent(String s)
	{
		return insertableComponents.containsKey(s);
	}
	
	public String getComponentID(Component c)
	{
		return getComponentID(c.getClass());
	}
	
	public String getComponentID(Class<?> e)
	{
		Iterator<Entry<String, Class<?>>> iterator = insertableComponents.entrySet().iterator();
		while(iterator.hasNext())
		{
			Entry<String, Class<?>> entry = iterator.next();
			if(entry.getValue().equals(e))
				return entry.getKey();
		}
		return null;
	}
	
	public Iterator<Entry<String,Class<?>>> getIterator()
	{
		return insertableComponents.entrySet().iterator();
	}
}

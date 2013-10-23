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
	
	/**
	 * Adds a Class to the component list
	 * @param s Unique, human-readable name of this component.  This is what the class will show up as in the editor/while saving.
	 * @param c Class to add to the list.  Class MUST extend {@link com.remote.remote2d.engine.entity.component.Component}.
	 * @throws java.lang.IllegalArgumentException.IllegalArgumentException if <i>e</i> does not extend {@link com.remote.remote2d.engine.entity.component.Component}
	 */
	public void addInsertableComponent(String s, Class<?> c)
	{
		if(Component.class.isAssignableFrom(c))
			insertableComponents.put(s, c);
		else
			throw new IllegalArgumentException("Class "+c.getSimpleName()+" does not directly extend Component!");
	}
	
	/**
	 * Creates a new component with the given name
	 * @param s Component name
	 * @param entity Entity to add this component to
	 * @return A new instance of the class associated with <i>s</i>.
	 */
	public Component getComponentWithEntity(String s,Entity entity)
	{
		return Component.newInstanceWithEntity(insertableComponents.get(s), entity);
	}
	
	/**
	 * Whether or not the given identifier is in the component list.
	 * @param s possible identifier
	 */
	public boolean containsComponent(String s)
	{
		return insertableComponents.containsKey(s);
	}
	
	/**
	 * The identifier of the given Component on the list.
	 * @param c Component to test.
	 */
	public String getComponentID(Component c)
	{
		return getComponentID(c.getClass());
	}
	
	/**
	 * The identifier of the given class
	 * @param c Any class that extends {@link com.remote.remote2d.engine.entity.component.Component}.
	 */
	public String getComponentID(Class<?> c)
	{
		Iterator<Entry<String, Class<?>>> iterator = insertableComponents.entrySet().iterator();
		while(iterator.hasNext())
		{
			Entry<String, Class<?>> entry = iterator.next();
			if(entry.getValue().equals(c))
				return entry.getKey();
		}
		return null;
	}
	
	/**
	 * An instance of {@link java.util.Iterator} for each Class/ID pair in the list
	 */
	public Iterator<Entry<String,Class<?>>> getIterator()
	{
		return insertableComponents.entrySet().iterator();
	}
}

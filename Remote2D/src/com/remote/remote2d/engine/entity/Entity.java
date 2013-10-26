package com.remote.remote2d.engine.entity;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Material;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.entity.component.Component;
import com.remote.remote2d.engine.entity.component.ComponentCollider;
import com.remote.remote2d.engine.io.R2DTypeCollection;
import com.remote.remote2d.engine.logic.Collider;
import com.remote.remote2d.engine.logic.Collision;
import com.remote.remote2d.engine.logic.Interpolator;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Map;

/**
 * The basic class for all moving things in the game.  Things such as the player,
 * bullets, enemies, mechanized objects, etc. Anything that needs to tick().  Note,
 * that Entities themselves DO NOT tick, their components do.  Entities themselves
 * <i>are</i> the GameObject, and Components <i>control</i> the Entities.
 * 
 * @author Flafla2
 */
public class Entity extends EditorObject {
	
	public String name;
	/**
	 * This entity's local position, relative to its parent entity.  DO NOT USE THIS
	 * for calculating the global position of an entity (for rendering, etc.) use
	 * {@link #getGlobalPos()}.
	 * 
	 * @see #getGlobalPos()
	 */
	public Vector2 pos;
	/**
	 * This entity's dimensions
	 */
	public Vector2 dim;
	/**
	 * This entity's, rotation, local to its parent entity, in degrees.  DO NOT USE THIS
	 * for calculating the global rotation of an entity (for rendering, etc.) use
	 * {@link #getGlobalRotation()}.
	 */
	public float rotation;
	/**
	 * The material of this entity - what color, textures, etc. This entity uses.
	 */
	public Material material;
	public boolean repeatTex = false;
	public boolean linearScaling = false;
	
	private Texture slashTex;
	
	private Vector2 oldPos;
	
	private static final String slashLoc = "/res/gui/slash.png";
	
	protected ArrayList<Entity> children;
	protected ArrayList<Component> components;
	protected Entity parent;
		
	public Entity(Map map, String name)
	{
		super(map,null);
		this.name = name;
		children = new ArrayList<Entity>();
		components = new ArrayList<Component>();
		
		slashTex = new Texture(slashLoc,false,true);
		material = new Material(0xaaaaaa,1);
		
		pos = new Vector2(0,0);
		oldPos = new Vector2(0,0);
		dim = new Vector2(50,50);
	}
	
	public Entity(Map map, String name, String uuid)
	{
		super(map,uuid);
		this.name = name;
		children = new ArrayList<Entity>();
		components = new ArrayList<Component>();
		slashTex = new Texture(slashLoc,false,true);
		
		pos = new Vector2(0,0);
		oldPos = new Vector2(0,0);
		dim = new Vector2(50,50);
	}
	
	public Entity(Map map)
	{
		this(map, "");
	}
	
	/**
	 * Tells the entity (and more importantly its components) that they have spawned.
	 * In other words, calls {@link Component#onEntitySpawn()} for all components.
	 */
	public void spawnEntityInWorld()
	{
		oldPos = pos.copy();
		
		for(int x=0;x<components.size();x++)
			components.get(x).onEntitySpawn();
	}
	
	/**
	 * Removes this entity from its map's entity list.
	 */
	public void removeEntityFromWorld()
	{
		map.getEntityList().removeEntityFromList(this);
	}
	
	/**
	 * Gets a child entity at the specified index.
	 * @param index The index of a child entity
	 */
	public Entity getChild(int index)
	{
		return children.get(index);
	}
	
	/**
	 * @param e Any entity
	 * @return If <i>e</i> is a child of this entity.
	 */
	public boolean isChild(Entity e)
	{
		return children.contains(e);
	}
	
	/**
	 * How many children this entity has.
	 */
	public int getChildrenSize()
	{
		return children.size();
	}
	
	/**
	 * The position of this entity, given any interpolation value.
	 */
	public Vector2 getPosLocal(float interpolation)
	{
		return Interpolator.linearInterpolate(oldPos, pos, interpolation);
	}
	
	/**
	 * Adds a new component to this entity.
	 * @param c The component
	 */
	public void addComponent(Component c)
	{
		Component cnew = c.clone();
		cnew.setEntity(this);
		components.add(cnew);
	}
	
	/**
	 * Adds a child to this entity
	 * @param e The child
	 */
	public void addChild(Entity e)
	{
		if(e.getParent() != null)
			e.parent.removeChild(this);
		e.parent = this;
		children.add(e);
	}
	
	/**
	 * Removes a child from this entity
	 * @param e The child to remove
	 */
	public void removeChild(Entity e)
	{
		if(children.contains(e))
		{
			children.remove(e);
			e.parent = null;
		}
	}
	
	/**
	 * A list of all of the components of this entity
	 */
	public ArrayList<Component> getComponents()
	{
		return components;
	}
	
	/**
	 * A list of all colliders associated with this entity (through a ComponentCollider).
	 */
	public ArrayList<Collider> getColliders()
	{
		ArrayList<Collider> colliders = new ArrayList<Collider>();
		for(Component c : components)
		{
			if(c instanceof ComponentCollider)
				colliders.add(((ComponentCollider)c).getCollider());
		}
		return colliders;
	}
	
	/**
	 * Assuming this Entity is STATIC (not moving):
	 * Calculates any potential colliders from this Entity that would collide with the given Collider
	 * @param coll Any moving collider
	 * @param movement The movement vector of said collider
	 * @return If this Entity collides with coll, list of all colliders involved with this Entity.  Otherwise, null.
	 */
	public ArrayList<Collider> getPossibleColliders(Collider coll, Vector2 movement)
	{
		
		Collider mainCollider = getBroadPhaseCollider();
		if(mainCollider == null)
			return null;
		
		mainCollider = mainCollider.getTransformedCollider(pos);
		Collision mainColliderCollision = coll.getCollision(mainCollider, movement);
		if(!mainColliderCollision.collides)
			return null;
		
		ArrayList<Collider> colliders = getColliders();
		ArrayList<Collider> retColliders = new ArrayList<Collider>();
		for(int x=0;x<colliders.size();x++)
		{
			retColliders.add(colliders.get(x).getTransformedCollider(pos));
		}
		
		return retColliders;
	}
	
	/**
	 * The parent of this entity.
	 */
	public Entity getParent()
	{
		return parent;
	}
	
	/**
	 * Calculates if a given Vector2 is inside this entity, using its colliders.
	 * @param vec Any point
	 */
	public boolean isPointInside(Vector2 vec)
	{
		Collider mainCollider = getBroadPhaseCollider().getTransformedCollider(pos);
		if(mainCollider.isPointInside(vec))
		{
			ArrayList<Collider> colliders = getColliders();
			for(int x=0;x<colliders.size();x++)
				if(colliders.get(x).isPointInside(vec))
					return true;
			
			return false;
		} else
			return false;
	}
	
	/**
	 * Renders all of this entity's colliders for debugging purposes.
	 */
	public void renderColliders()
	{
		Collider mainCollider = getBroadPhaseCollider();
		ArrayList<Collider> colliders = getColliders();
		Renderer.pushMatrix();
			Renderer.translate(new Vector2(pos.x,pos.y));
			if(mainCollider != null)
				mainCollider.drawCollider(0xffff00);
			for(int x=0;x<colliders.size();x++)
				colliders.get(x).drawCollider(0xffffff);
		Renderer.popMatrix();
	}
	
	/**
	 * All of this Entity's colliders are guaranteed to be inside this collider.
	 * 
	 * @return null if there are no colliders, otherwise the broad phase collider.
	 */
	public Collider getBroadPhaseCollider()
	{
		ArrayList<Collider> colliders = getColliders();
		if(colliders.size()==0)
			return null;
		Vector2 v1 = null;
		Vector2 v2 = null;
		
		for(int x=0;x<colliders.size();x++)
		{
			for(int y=0;y<colliders.get(x).verts.length;y++)
			{
				if(v1 == null)
					v1 = colliders.get(x).verts[y].copy();
				if(v2 == null)
					v2 = colliders.get(x).verts[y].copy();
				
				if(colliders.get(x).verts[y].x < v1.x)
					v1.x = colliders.get(x).verts[y].x;
				if(colliders.get(x).verts[y].x > v2.x)
					v2.x = colliders.get(x).verts[y].x;
				if(colliders.get(x).verts[y].y < v1.y)
					v1.y = colliders.get(x).verts[y].y;
				if(colliders.get(x).verts[y].y > v2.y)
					v2.y = colliders.get(x).verts[y].y;
			}
		}
		
		return v1.getColliderWithDim(v2.subtract(v1));
	}
	
	/**
	 * A general collider that encompasses this Entity's rendered area.  NOT to be used
	 * for collision detection.
	 */
	public Collider getGeneralCollider()
	{
		return pos.getColliderWithDim(getDim());
	}
	
	/**
	 * This entity's dimensions.
	 */
	public Vector2 getDim()
	{
		return dim;
	}
	
	/**
	 * Responsible for all logic.  This should only be called by {@link Remote2D#tick(int, int, int)}.
	 * Also calls {@link Component#tick(int, int, int)} for all components.
	 * 
	 * @param i Mouse X
	 * @param j Mouse Y
	 * @param k Mouse Down (1 if left mouse, 2 if right mouse, 0 if no mouse)
	 */
	public void tick(int i, int j, int k)
	{
		oldPos = pos.copy();
		
		for(int x=0;x<components.size();x++)
			components.get(x).tick(i, j, k);
	}
	
	/**
	 * Responsible for all rendering.  This should only be called by {@link Remote2D#render(float)}.
	 * DOES NOT call rendering for components.
	 * @param editor If the Entity is being rendered in the editor (useful for debug graphics)
	 * @param interpolation A float between 0.0 - 1.0 detailing how far in between the last tick and the next tick this render is.
	 */
	public void render(boolean editor, float interpolation)
	{
		Vector2 pos = Interpolator.linearInterpolate(oldPos, this.pos, interpolation);
		
		if(editor)
			oldPos = pos.copy();
		
		boolean selected = false;
		if(editor)
			if(Remote2D.guiList.peek() instanceof GuiEditor)
				if(((GuiEditor)Remote2D.guiList.peek()).getSelectedEntity() == this)
					selected = true;
		
		for(int x=0;x<components.size();x++)
			components.get(x).renderBefore(editor, interpolation);
		
		Renderer.pushMatrix();
			Renderer.translate(new Vector2(-pos.x, -pos.y));
			Renderer.rotate(rotation);
			Renderer.translate(new Vector2(pos.x, pos.y));
			if(editor)
			{
				float maxX = (dim.x)/32f;
				float maxY = (dim.y)/32f;
				int color = 0xffffff;
				if(selected)
					color = 0xff0000;
				else
					color = 0xffaaaa;
				Renderer.drawRect(pos, dim, new Vector2(0,0), new Vector2(maxX, maxY), slashTex, color, 1);
			}
			
			material.render(pos, dim);
			
			if(editor && selected)
				Renderer.drawLineRect(pos, dim, 1, 0, 0, 1);
		Renderer.popMatrix();
		
		
		
		for(int x=components.size()-1;x>=0;x--)
			components.get(x).renderAfter(editor, interpolation);
	}
	
	/**
	 * The global position of this Entity. {@link #pos} is local, so it does not
	 * account for the position of its parents.  This does account for this.
	 * 
	 * @see #pos
	 */
	public Vector2 getPosGlobal()
	{
		return Renderer.matrixMultiply(pos);
	}
	
	/**
	 * The global rotation of this Entity. {@link #rotation} is local, so it does not
	 * account for the rotation of its parents.  This does account for this.
	 * 
	 * @see #rotation
	 */
	public float getGlobalRotation()
	{
		if(parent == null)
			return rotation;
		return parent.getGlobalRotation() + rotation;
	}
	
	/**
	 * The final matrix made up of the matrices of the combined transformations
	 * of this Entity and its parents.
	 */
	public Matrix4f getTransformMatrix()
	{
		Matrix4f mat = new Matrix4f();
		Matrix4f.rotate((float)(rotation*Math.PI/180), new Vector3f(0,0,1), mat, mat);
		Matrix4f.translate(new Vector2f(pos.x,pos.y), mat, mat);
		if(parent == null)
			return mat;
		
		return Matrix4f.mul(mat, parent.getTransformMatrix(), null);
	}
	
	@Override
	public Entity clone()
	{
		R2DTypeCollection compile = new R2DTypeCollection("Entity Clone");
		saveR2DFile(compile);
		Entity clone = new Entity(map);
		clone.loadR2DFile(compile);
		
		for(Component c : components)
		{
			clone.addComponent(c.clone());
		}
		return clone;
	}
	
	/**
	 * Gets all components of this of a specific type
	 * @param type Any Class that extends Component
	 * @see Component
	 */
	@SuppressWarnings("unchecked")
	public <T extends Component> ArrayList<T> getComponentsOfType(Class<T> type)
	{
		ArrayList<T> returnComponents = new ArrayList<T>();
		for(int x=0;x<components.size();x++)
			if(type.isInstance(components.get(x)))
				returnComponents.add((T) components.get(x));
		
		return returnComponents;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Entity)
			return ((Entity)o).getUUID().equals(getUUID());
		return false;
	}

	@Override
	public void apply() {
		
	}
	
	/**
	 * Ignores interpolation for rendering this tick.  Useful after teleporting
	 * an Entity; to not make it quickly move to its new position but rather
	 * pop up right there.
	 */
	public void updatePos()
	{
		oldPos = pos.copy();
	}
	
	/**
	 * Renders a preview of this entity
	 */
	public void renderPreview(float interpolation) {
		Renderer.pushMatrix();
		Renderer.translate(new Vector2(-pos.x, -pos.y));
		try
		{
			for(int x=0;x<getComponents().size();x++)
				getComponents().get(x).renderBefore(false,interpolation);
			render(false,interpolation);
			for(int x=0;x<getComponents().size();x++)
				getComponents().get(x).renderAfter(false,interpolation);
		} catch(Exception e)
		{
			GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
			Renderer.drawCrossRect(pos, dim, 0xffffff, 1.0f);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		Renderer.popMatrix();
	}

	public static String getExtension() {
		return ".entity";
	}
		
}

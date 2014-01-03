package com.remote.remote2d.engine.entity;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Material;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.ResourceLoader;
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
	
	public static String getExtension() {
		return ".entity";
	}
	
	public static boolean isValidFile(String name)
	{
		String lc = name.toLowerCase();
		return lc.endsWith(getExtension()+".xml") || lc.endsWith(getExtension()+".bin");
	}
	
	public String name;
	/**
	 * This entity's local position, relative to its parent entity.  DO NOT USE THIS
	 * for calculating the global position of an entity (for rendering, etc.) use
	 * {@link #getPosGlobal()}.
	 * 
	 * @see #getPosGlobal()
	 */
	public Vector2 pos;
	/**
	 * This entity's dimensions, in pixels
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
	//TODO: Readd parents
	
	private Texture slashTex;
	private String prefabPath = null;
	private Vector2 oldPos;
	private static final String slashLoc = "res/gui/slash.png";
	protected ArrayList<Component> components;
	
	public Entity(Map map)
	{
		this(map, "");
	}
	
	public Entity(Map map, String name)
	{
		super(map,null);
		this.name = name;
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
		components = new ArrayList<Component>();
		slashTex = new Texture(slashLoc,false,true);
		
		pos = new Vector2(0,0);
		oldPos = new Vector2(0,0);
		dim = new Vector2(50,50);
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
	
	@Override
	public void apply() {
		
	}
	
	@Override
	public Entity clone()
	{
		Entity clone = new Entity(map,name,getUUID());
		clone.transpose(this);
		return clone;
	}
	
	/**
	 * Takes all the attributes of the given entity and transposes it to this
	 * entity.  Keep in mind that this does NOT transpose the UUID of the other
	 * entity.
	 * @param e Entity to transpose over to this one.
	 */
	public void transpose(Entity e)
	{
		R2DTypeCollection compile = new R2DTypeCollection("Entity Clone");
		Map.saveEntityFull(e, compile, false);
		compile.setString("uuid", getUUID());
		Map.loadEntityFull(this, compile, false);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Entity)
			return ((Entity)o).getUUID().equals(getUUID());
		return false;
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
	 * A list of all of the components of this entity
	 */
	public ArrayList<Component> getComponents()
	{
		return components;
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
	
	/**
	 * This entity's dimensions.
	 */
	public Vector2 getDim()
	{
		return dim;
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
	 * The global rotation of this Entity. {@link #rotation} is local, so it does not
	 * account for the rotation of its parents.  This does account for this.
	 * 
	 * NOTE: Because parents have not been implemented yet this is equivalent to {@link #rotation}
	 * 
	 * @see #rotation
	 */
	public float getGlobalRotation()
	{
		return rotation;
	}
	
	/**
	 * The global position of this Entity, given any interpolation value.
	 * {@link #pos} is local, so it does not account for the position of
	 * its parents.  This does account for this.
	 * 
	 * @see #pos
	 */
	public Vector2 getPosGlobal(float interpolation)
	{
		return Renderer.matrixMultiply(new Vector2(0,0), getTransformMatrix());
	}
	
	/**
	 * The global position of this Entity. {@link #pos} is local, so it does not
	 * account for the position of its parents.  This does account for this.
	 * 
	 * @see #pos
	 */
	public Vector2 getPosGlobal()
	{
		return Renderer.matrixMultiply(new Vector2(0,0), getTransformMatrix());
	}
	
	/**
	 * The local position of this entity, given any interpolation value.
	 */
	public Vector2 getPosLocal(float interpolation)
	{
		return Interpolator.linearInterpolate(oldPos, pos, interpolation);
	}
	
	/**
	 * The local position of this entity
	 */
	public Vector2 getPosLocal()
	{
		return pos.copy();
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
	 * The final matrix made up of the matrices of the combined transformations
	 * of this Entity and its parents.
	 * 
	 * NOTE: Because parents have not been implemented yet this is equivalent to {@link #getLocalTransformMatrix()}
	 */
	public Matrix4f getTransformMatrix()
	{
		Matrix4f mat = getLocalTransformMatrix();

		return mat;
	}
	
	public Matrix4f getLocalTransformMatrix() {
		Matrix4f mat = new Matrix4f();
		Matrix4f.translate(new Vector3f(pos.x,pos.y,0), mat, mat);
		Matrix4f.rotate((float)((rotation*Math.PI)/180f), new Vector3f(0,0,1), mat, mat);
		return mat;
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
	 * Removes this entity from its map's entity list.
	 */
	public void removeEntityFromWorld()
	{
		map.getEntityList().removeEntityFromList(this);
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
				if(getUUID().equals(((GuiEditor)Remote2D.guiList.peek()).getSelectedEntity()))
					selected = true;
		
		for(int x=0;x<components.size();x++)
			components.get(x).renderBefore(editor, interpolation);
		
		Renderer.pushMatrix();
			Renderer.mult(getTransformMatrix());
			
			if(editor)
			{
				float maxX = (dim.x)/32f;
				float maxY = (dim.y)/32f;
				int color = 0xffffff;
				if(selected)
					color = 0xff0000;
				else
					color = 0xffaaaa;
				Renderer.drawRect(new Vector2(0,0), dim, new Vector2(0,0), new Vector2(maxX, maxY), slashTex, color, 1);
			}
			
			material.render(new Vector2(0,0), dim);
			
			if(editor && selected)
				Renderer.drawLineRect(new Vector2(0,0), dim, 1, 0, 0, 1);
		Renderer.popMatrix();
		
		for(int x=components.size()-1;x>=0;x--)
			components.get(x).renderAfter(editor, interpolation);
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
	 * Renders a preview of this entity
	 */
	public void renderPreview(float interpolation) {
		Renderer.pushMatrix();
		Renderer.translate(new Vector2(-pos.x, -pos.y));
		try
		{
			render(false,1);
		} catch(Exception e)
		{
			GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
			Renderer.drawCrossRect(pos, dim, 0xffffff, 1.0f);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		Renderer.popMatrix();
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
	 * Ignores interpolation for rendering this tick.  Useful after teleporting
	 * an Entity; to not make it quickly move to its new position but rather
	 * pop up right there.
	 */
	public void updatePos()
	{
		oldPos = pos.copy();
	}
	
	/**
	 * Assigns a prefab to this Entity.  This means that, the entity should be exactly the same as its prefab counterpart, until
	 * its connection with the prefab is broken.
	 * @param path Path (locally to the jar file) to the prefab file.
	 */
	public void setPrefabPath(String path)
	{
		this.prefabPath = path;
		if(path == null)
			return;
		R2DTypeCollection coll = ResourceLoader.getCollection(path).clone();
		coll.setString("uuid", getUUID());
		coll.setVector2D("pos", pos);
		coll.setFloat("rotation", rotation);
		coll.setString("name", name);
		
		Map.loadEntityFull(this, coll, true);
	}
	
	public String getPrefabPath()
	{
		return prefabPath;
	}
		
}

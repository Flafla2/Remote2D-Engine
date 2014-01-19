package com.remote.remote2d.engine.world;

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.entity.EntityList;
import com.remote.remote2d.engine.entity.InsertableComponentList;
import com.remote.remote2d.engine.entity.component.Component;
import com.remote.remote2d.engine.gui.Gui;
import com.remote.remote2d.engine.io.R2DFileSaver;
import com.remote.remote2d.engine.io.R2DFileUtility;
import com.remote.remote2d.engine.io.R2DTypeCollection;
import com.remote.remote2d.engine.logic.Collider;
import com.remote.remote2d.engine.logic.Collision;
import com.remote.remote2d.engine.logic.CollisionComparator;
import com.remote.remote2d.engine.logic.Vector2;

public class Map implements R2DFileSaver {
	
	private EntityList entities;
	public Camera camera;
	public int backgroundColor = 0xffffff;
	public int gridSize = 16;
	public String path = null;
	
	public boolean debug = false;
	
	public Map()
	{
		entities = new EntityList(this);
		camera = new Camera();
	}
	
	public Map(EntityList list)
	{
		entities = list;
		camera = new Camera();
	}
	
	public void render(boolean editor, float interpolation)
	{
		//drawGrid(interpolation);
		camera.renderBefore(interpolation);
		entities.render(editor,interpolation);
		camera.renderAfter(interpolation);
	}
	
	public void drawGrid(float interpolation)
	{		
		camera.renderBefore(interpolation);
		Vector2 pos = camera.getTruePos(interpolation).subtract(new Vector2(Gui.screenWidth()/2,Gui.screenHeight()/2));
		Vector2 currentPos = new Vector2(0,0);
		currentPos.x = pos.x-pos.x%gridSize-gridSize;
		currentPos.y = pos.y-pos.y%gridSize-gridSize;
		
		for(int x=0;x<Gui.screenHeight()/camera.scale/gridSize+2;x++)
		{
			int color = 0x000000;
			float alpha = 0.25f;
			if(currentPos.y == 0)
			{
				color = 0xff0000;
				alpha = 0.5f;
				GL11.glLineWidth(3);
			}
			Renderer.drawLine(currentPos, new Vector2((Gui.screenWidth())/camera.scale+gridSize*2+currentPos.x,currentPos.y), color, alpha);
			currentPos.y+=gridSize;
			GL11.glLineWidth(1);
		}
		
		currentPos.y = pos.y-pos.y%gridSize-gridSize;
		for(int y=0;y<Gui.screenWidth()/camera.scale/gridSize+2;y++)
		{
			int color = 0x000000;
			float alpha = 0.25f;
			if(currentPos.x == 0)
			{
				color = 0x00ff00;
				alpha = 0.5f;
				GL11.glLineWidth(3);
			}
			Renderer.drawLine(currentPos, new Vector2(currentPos.x,(Gui.screenHeight())/camera.scale+gridSize*2+currentPos.y), color, alpha);
			currentPos.x+=gridSize;
			GL11.glLineWidth(1);
		}
		
		Renderer.popMatrix();
	}
	
	public void spawn()
	{
		entities.spawn();
	}
	
	public void tick(int i, int j, int k, boolean editor)
	{
		camera.tick(i, j, k);
		if(!editor)
			entities.tick(i, j, k);
	}
	
	public Vector2 getCorrection(Entity e, Vector2 movement)
	{
		Vector2 ret = movement.copy();
		for(Collider c : e.getColliders())
		{
			ret = ret.add(getCorrection(c,movement));
		}
		return ret;
	}
	
	/**
	 * Gives you the correction if you have a moving collider somewhere in the map.
	 * @param coll The moving collider (before moving)
	 * @param velocity Said collider's movement vector
	 * @return The "correction" - just add this to your movement vector and you won't collide with anything
	 */
	public Vector2 getCorrection(Collider coll,Vector2 velocity)
	{
		ArrayList<Collider> allColliders = new ArrayList<Collider>();
		
		//Broad phase: check the main collider of each worldelement.
		for(int x=0;x<entities.size();x++)
		{
			ArrayList<Collider> elementColliders = entities.get(x).getPossibleColliders(coll, velocity);
			if(elementColliders != null)
			{
				allColliders.addAll(elementColliders);
			}
		}
		
		//First collision pass: Test to see which element will be hit first, and if they are hit at all
		ArrayList<Collision> allCollision = new ArrayList<Collision>();
		ArrayList<Collider> colliding = new ArrayList<Collider>();
		for(int x=0;x<allColliders.size();x++)
		{
			Collider other = allColliders.get(x);
			Collision collision = Collider.getCollision(other, coll.getTransformedCollider(velocity));
			if(collision.collides)
			{
				allCollision.add(collision);
				colliding.add(other);
			}
		}
		
		Collections.sort(allCollision, new CollisionComparator());
		
		//Second collsion pass: Get the actual collision vectors of each collider and add it to the main return vector
		Vector2 correction = new Vector2(0,0);
		for(int x=0;x<allCollision.size();x++)
		{
			Collider other = colliding.get(x);
			Collision collision = Collider.getCollision(other, coll.getTransformedCollider(velocity.add(correction)));
			if(collision.collides)
			{
				correction = correction.add(collision.correction);
			}
		}
		return correction;
	}
	
	/**
	 * Returns if the given collider collides with any other collider in the map
	 * @param coll Collider to test
	 */
	public boolean collidesWithMap(Collider coll)
	{
		for(int x=0;x<entities.size();x++)
		{
			Entity e = entities.get(x);
			ArrayList<Collider> colliders = e.getColliders();
			for(Collider c : colliders)
				if(Collider.collides(coll, c.getTransformedCollider(e.pos)))
					return true;
		}
		return false;
	}
	
	public Entity getTopEntityAtPoint(Vector2 vec)
	{
		Entity top = null;
		for(int x=0;x<entities.size();x++)
		{
			if(entities.get(x).getPosGlobal().getColliderWithDim(entities.get(x).getDim()).isPointInside(vec))
				top = entities.get(x);
		}
		return top;
	}
	
	@Override
	public void saveR2DFile(R2DTypeCollection collection) {
		collection.setInteger("entityCount", entities.size());
		for(int x=0;x<entities.size();x++)
		{
			R2DTypeCollection c = new R2DTypeCollection("entity_"+x);
			
			saveEntityFull(entities.get(x),c,false);
			
			collection.setCollection(c);
		}
	}
	
	/**
	 * Saves an entity into the given collection completely - meaning that it not only saves the Entity's data, but the data of its components as well.
	 * @param e Entity to save / compile
	 * @param c Collection to save to
	 * @param ignorePrefab Whether or not to ignore being a prefab.  If this is false and an entity is a prefab, only the path to the prefab will be saved, as well as certain essential variables (pos,name,rotation,uuid).
	 */
	public static void saveEntityFull(Entity e, R2DTypeCollection c, boolean ignorePrefab)
	{
		if(e.getPrefabPath() != null && !ignorePrefab)
		{
			c.setString("prefabPath", R2DFileUtility.getStandardPath(e.getPrefabPath()));
			c.setVector2D("pos", e.pos);
			c.setFloat("rotation", e.rotation);
			c.setString("name", e.name);
			c.setString("uuid", e.getUUID());
			return;
		}
		
		e.saveR2DFile(c);
		ArrayList<Component> components = e.getComponents();
		c.setInteger("componentCount", components.size());
		for(int y=0;y<components.size();y++)
		{
			R2DTypeCollection cComp = new R2DTypeCollection("component_"+y);
			cComp.setString("className", InsertableComponentList.getComponentID(components.get(y)));
			components.get(y).saveR2DFile(cComp);
			c.setCollection(cComp);
		}
	}
	
	@Override
	public void loadR2DFile(R2DTypeCollection collection) {
		entities.clear();
		int entityCount = collection.getInteger("entityCount");
		for(int x=0;x<entityCount;x++)
		{
			R2DTypeCollection c = collection.getCollection("entity_"+x);
			Entity e = entities.getEntityWithUUID(c.getString("uuid"));
			
			if(e == null)
				e = new Entity(this,"",c.getString("uuid"));
			else
				entities.removeEntityFromList(e);	//Will be moved to the top after we load.
													//This is done in order to keep all Entity pointers intact.
			
			loadEntityFull(e,c,false);
			
			entities.addEntityToList(e);
		}
		//collection.printContents();
	}
	
	public static void loadEntityFull(Entity e, R2DTypeCollection c, boolean ignorePrefab)
	{
		if(c.hasKey("prefabPath") && !ignorePrefab)
		{
			String prefabPath = c.getString("prefabPath");
			
			e.setPrefabPath(prefabPath);
			e.name = c.getString("name");
			e.pos = c.getVector2D("pos");
			e.rotation = c.getFloat("rotation");
			e.setUUID(c.getString("uuid"));
			return;
		} else if(!ignorePrefab)
			e.setPrefabPath(null);
		
		e.loadR2DFile(c);
		e.getComponents().clear();
		int componentCount = c.getInteger("componentCount");
		for(int y=0;y<componentCount;y++)
		{
			R2DTypeCollection cComp = c.getCollection("component_"+y);
			Component comp = InsertableComponentList.getComponentWithEntity(cComp.getString("className"),e);
			comp.loadR2DFile(cComp);
			comp.apply();
			e.addComponent(comp);
		}
	}
	
	public Vector2 screenToWorldCoords(Vector2 vec)
	{
		Matrix4f matrix = camera.getInverseMatrix();
		
		Vector4f oldCoords = new Vector4f(vec.x,vec.y,0,1);
		Vector4f newCoords = Matrix4f.transform(matrix, oldCoords, null);
		return new Vector2(newCoords.x,newCoords.y);
	}
	
	public Vector2 worldToScreenCoords(Vector2 vec)
	{
		Matrix4f matrix = camera.getMatrix();
		
		Vector4f oldCoords = new Vector4f(vec.x,vec.y,0,1);
		Vector4f newCoords = Matrix4f.transform(matrix, oldCoords, null);
		return new Vector2(newCoords.x,newCoords.y);
	}
	
	public void setScaleAroundScreenPoint(Vector2 point, float scale)
	{
		Vector2 mousePos = screenToWorldCoords(point);
		camera.scale = scale;
		Vector2 mousePos2 = screenToWorldCoords(point);
		camera.pos = camera.pos.add(mousePos.subtract(mousePos2));
	}
	
	public Map copy()
	{
		R2DTypeCollection compile = new R2DTypeCollection("Map");
		saveR2DFile(compile);
		Map map = new Map();
		map.loadR2DFile(compile);
		return map;
	}
	
	public EntityList getEntityList() {
		return entities;
	}

	public static String getExtension() {
		return ".r2d";
	}
	
	public static boolean isValidFile(String name)
	{
		String lc = name.toLowerCase();
		return lc.endsWith(getExtension()+".xml") || lc.endsWith(getExtension()+".bin");
	}
	
}

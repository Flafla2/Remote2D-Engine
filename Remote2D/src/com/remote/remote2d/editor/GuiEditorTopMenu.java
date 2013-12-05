package com.remote.remote2d.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.editor.operation.OperationAddComponent;
import com.remote.remote2d.editor.operation.OperationDeleteEntity;
import com.remote.remote2d.editor.operation.OperationNewEntity;
import com.remote.remote2d.editor.operation.OperationNewMap;
import com.remote.remote2d.editor.operation.OperationSaveMap;
import com.remote.remote2d.engine.DisplayHandler;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.entity.InsertableComponentList;
import com.remote.remote2d.engine.gui.Gui;
import com.remote.remote2d.engine.gui.GuiInGame;
import com.remote.remote2d.engine.gui.KeyShortcut;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorTopMenu extends Gui {
	
	public ArrayList<GuiEditorTopMenuSection> sections;
	public GuiEditor editor;
	
	private int height = 20;
	
	public GuiEditorTopMenu(GuiEditor editor)
	{		
		this.editor = editor;
		
		sections = new ArrayList<GuiEditorTopMenuSection>();
				
		int currentX = 0;
				
		String[] fileContents = {"New Map","Open Map","Save Map","Save Map As..."};
		GuiEditorTopMenuSection file = new GuiEditorTopMenuSection(currentX, 0, height, fileContents, "File", this);
		
		file.keyCombos[0] = new KeyShortcut(new int[]{Keyboard.KEY_N});
		file.keyCombos[1] = new KeyShortcut(new int[]{Keyboard.KEY_O});
		file.keyCombos[2] = new KeyShortcut(new int[]{Keyboard.KEY_S});
		file.keyCombos[2] = new KeyShortcut(new int[]{Keyboard.KEY_S}).setUseShift(true);
		
		file.reloadSubWidth();
		
		if(file.getEnabled())
			currentX += file.width;
		
		String[] editContents = {"Undo", "Redo", "Create Animated Sprite", "Optimize Spritesheet"};
		GuiEditorTopMenuSection edit = new GuiEditorTopMenuSection(currentX, 0, height, editContents, "Edit", this);
		if(edit.getEnabled())
			currentX += edit.width;
		
		edit.keyCombos[0] = new KeyShortcut(new int[]{Keyboard.KEY_Z});
		edit.keyCombos[1] = new KeyShortcut(new int[]{Keyboard.KEY_Y});
		edit.keyCombos[2] = new KeyShortcut(new int[]{Keyboard.KEY_S}).setUseShift(true);
		edit.keyCombos[3] = new KeyShortcut(new int[]{Keyboard.KEY_O}).setUseShift(true);
		
		edit.reloadSubWidth();
		
		String[] worldContents = {"Toggle Grid", "Increase Grid Size", "Decrease Grid Size", "Zoom In", "Zoom Out", "Revert Zoom", "Run Map"};
		GuiEditorTopMenuSection world = new GuiEditorTopMenuSection(currentX, 0, height, worldContents, "World", this);
		if(world.getEnabled())
			currentX += world.width;
		
		world.keyCombos[0] = new KeyShortcut(new int[]{Keyboard.KEY_G}).setMetaOrControl(false);
		world.keyCombos[1] = new KeyShortcut(new int[]{Keyboard.KEY_RBRACKET}).setMetaOrControl(false);
		world.keyCombos[2] = new KeyShortcut(new int[]{Keyboard.KEY_LBRACKET}).setMetaOrControl(false);
		world.keyCombos[3] = new KeyShortcut(new int[]{Keyboard.KEY_EQUALS}).setMetaOrControl(false);
		world.keyCombos[4] = new KeyShortcut(new int[]{Keyboard.KEY_MINUS}).setMetaOrControl(false);
		world.keyCombos[5] = new KeyShortcut(new int[]{Keyboard.KEY_0}).setMetaOrControl(false);
		world.keyCombos[6] = new KeyShortcut(new int[]{Keyboard.KEY_R});
		
		world.reloadSubWidth();
		
		String[] entityContents = {"Insert Entity", "Delete Entity", "Create Prefab from Selected"};
		GuiEditorTopMenuSection entity = new GuiEditorTopMenuSection(currentX, 0, height, entityContents, "Entity", this);
		if(world.getEnabled())
			currentX += world.width;
		
		entity.keyCombos[0] = new KeyShortcut(new int[]{Keyboard.KEY_E});
		entity.keyCombos[1] = new KeyShortcut(new int[]{Keyboard.KEY_DELETE}).setMetaOrControl(false);
		entity.keyCombos[2] = new KeyShortcut(new int[]{Keyboard.KEY_P}).setUseShift(true);
		
		entity.reloadSubWidth();
		
		Iterator<Entry<String,Class<?>>> iterator = InsertableComponentList.getIterator();
		ArrayList<String> contents = new ArrayList<String>();
		while(iterator.hasNext())
			contents.add(iterator.next().getKey());
		
		String[] componentContents = new String[contents.size()];
		componentContents = contents.toArray(componentContents);
		GuiEditorTopMenuSection component = new GuiEditorTopMenuSection(currentX, 0, height, componentContents, "Component", this);
		if(component.getEnabled())
			currentX += component.width;
		
		String[] windowContents = {"Toggle Fullscreen","Console","Exit"};
		GuiEditorTopMenuSection window = new GuiEditorTopMenuSection(currentX, 0, height, windowContents, "Window", this);
		if(window.getEnabled())
			currentX += window.width;
		window.keyCombos[0] = new KeyShortcut(new int[]{Keyboard.KEY_F});
		window.keyCombos[1] = new KeyShortcut(new int[]{Keyboard.KEY_C}).setUseShift(true);
		
		window.reloadSubWidth();
		
		String[] devContents = {"Reinitialize Editor", "View Art Asset", "Fancypants Collider Test","Normal Collider Test", "1D Perlin Noise", "2D Perlin Noise", "Toggle Wireframe"};
		GuiEditorTopMenuSection dev = new GuiEditorTopMenuSection(currentX, 0, height, devContents, "Developer", this);
		if(dev.getEnabled())
			currentX += dev.width;
		
		dev.keyCombos[6] = new KeyShortcut(new int[]{Keyboard.KEY_W});
		
		sections.add(file);
		sections.add(world);
		sections.add(edit);
		sections.add(entity);
		sections.add(component);
		sections.add(window);
		sections.add(dev);
	}
	
	public void initSections()
	{
		int currentX = 0;
		for(int x=0;x<sections.size();x++)
		{
			if(sections.get(x).getEnabled())
			{
				sections.get(x).x = currentX;
				currentX += sections.get(x).width;
			}
		}
		
	}
	
	public GuiEditorTopMenuSection getSectionWithName(String s)
	{
		for(int x=0;x<sections.size();x++)
		{
			if(sections.get(x).title.equals(s))
				return sections.get(x);
		}
		return null;
	}
	
	public boolean isMenuHovered(int i, int j)
	{
		boolean z = false;
		for(int x=0;x<sections.size();x++)
		{
			if(sections.get(x).isSelected)
				z = true;
		}
		return (i>0 && j>0 && i<screenWidth() && j<height) || z;
	}

	@Override
	public void render(float interpolation) {
		Renderer.drawRect(new Vector2(0,0), new Vector2(screenWidth(),height), 1, 0.2f, 0.2f, 1);
		
		for(int x=0;x<sections.size();x++)
		{
			sections.get(x).render(interpolation);
		}
		
		String fps = "FPS: "+Remote2D.getFPS();
		int width = Fonts.get("Arial").getStringDim(fps, 20)[0];
		Fonts.get("Arial").drawString(fps, DisplayHandler.getDimensions().x-width, 0, 20, 0xffffff);
	}

	@Override
	public void tick(int i, int j, int k) {
		getSectionWithName("World").setEnabled(editor.getMap() != null);
		getSectionWithName("Component").setEnabled(editor.getMap() != null);
		getSectionWithName("Entity").setEnabled(editor.getMap() != null);
		
		for(int x=0;x<sections.size();x++)
		{
			sections.get(x).tick(i,j,k);
		}
		
		String secTitle = "NONE";
		String secSubTitle = "NONE";
		
		for(int x=0;x<sections.size();x++)
		{
			int selectedSubSec = sections.get(x).popSelectedBox();
			if(selectedSubSec != -1)
			{
				secTitle = sections.get(x).title;
				secSubTitle = sections.get(x).values[selectedSubSec];
				//Log.info("Selected box: " + secTitle + ">" + secSubTitle+" ");
			}
		}
		
		if(secTitle.equalsIgnoreCase("File"))
		{
			if(secSubTitle.equalsIgnoreCase("New Map"))
			{
				if(editor.getMap() != null)
					editor.confirmOperation(new OperationNewMap(editor));
				else
					editor.executeOperation(new OperationNewMap(editor));
			} else if(secSubTitle.equalsIgnoreCase("Open Map"))
			{
				Log.info("Opening!");
				editor.pushWindow(new GuiWindowOpenMap(editor, new Vector2(i,j), editor.getWindowBounds()));
			} else if(secSubTitle.equalsIgnoreCase("Save Map") && editor.getMap() != null)
			{
				if(editor.getMap().path != null)
					editor.confirmOperation(new OperationSaveMap(editor,editor.getMap().path));
				else
					editor.pushWindow(new GuiWindowSaveMap(editor,new Vector2(i,j),editor.getWindowBounds()));
			} else if(secSubTitle.equalsIgnoreCase("Save Map As...") && editor.getMap() != null)
			{
				editor.pushWindow(new GuiWindowSaveMap(editor,new Vector2(i,j),editor.getWindowBounds()));
			}
		} else if(secTitle.equalsIgnoreCase("Edit"))
		{
			if(secSubTitle.equalsIgnoreCase("Create Animated Sprite"))
			{
				Remote2D.guiList.push(new GuiCreateSpriteSheet());
			} else if(secSubTitle.equalsIgnoreCase("Optimize Spritesheet"))
			{
				Remote2D.guiList.push(new GuiOptimizeSpriteSheet());
			} else if(secSubTitle.startsWith("Undo"))
			{
				editor.undo();
			} else if(secSubTitle.startsWith("Redo"))
			{
				editor.redo();
			}
		} else if(secTitle.equalsIgnoreCase("Window"))
		{
			if(secSubTitle.equalsIgnoreCase("Toggle Fullscreen"))
			{
				DisplayHandler.setDisplayMode(Display.getDesktopDisplayMode().getWidth(),
						Display.getDesktopDisplayMode().getHeight(), !Display.isFullscreen(), 
						false);
			} else if(secSubTitle.equalsIgnoreCase("Console"))
			{
				editor.pushWindow(new GuiWindowConsole(editor, new Vector2(100), new Vector2(400), editor.getWindowBounds()));
			} else if(secSubTitle.equalsIgnoreCase("Exit"))
			{
				Remote2D.guiList.pop();
			}
		} else if(secTitle.equalsIgnoreCase("Developer"))
		{
			if(secSubTitle.equals("Reinitialize Editor"))
			{
				Remote2D.guiList.pop();
				Remote2D.guiList.push(new GuiEditor());
			} else if(secSubTitle.equals("View Art Asset"))
			{
				GuiWindowViewArtAsset window = new GuiWindowViewArtAsset(editor, new Vector2(200,200), editor.getWindowBounds());
				window.setSelected(true);
				editor.pushWindow(window);
			} else if(secSubTitle.equalsIgnoreCase("Fancypants Collider Test"))
			{
				GuiWindowCollisionTest window = new GuiWindowCollisionTest(editor, new Vector2(300,300), editor.getWindowBounds());
				window.setSelected(true);
				editor.pushWindow(window);
			} else if(secSubTitle.equalsIgnoreCase("Normal Collider Test"))
			{
				GuiWindowGeneralColliderTest window = new GuiWindowGeneralColliderTest(editor, new Vector2(300,300), editor.getWindowBounds());
				window.setSelected(true);
				editor.pushWindow(window);
			} else if(secSubTitle.equalsIgnoreCase("1D Perlin Noise"))
			{
				GuiWindowPerlin1D window = new GuiWindowPerlin1D(editor, new Vector2(10,30), editor.getWindowBounds());
				window.setSelected(true);
				editor.pushWindow(window);
			} else if(secSubTitle.equalsIgnoreCase("2D Perlin Noise"))
			{
				GuiWindowPerlin2D window = new GuiWindowPerlin2D(editor, new Vector2(20,30), editor.getWindowBounds());
				window.setSelected(true);
				editor.pushWindow(window);
			} else if(secSubTitle.equalsIgnoreCase("Toggle Wireframe"))
			{
				Renderer.setWireframe(!Renderer.isWireframe());
					
			}
		} else if(secTitle.equalsIgnoreCase("World"))
		{
			if(secSubTitle.equalsIgnoreCase("Run Map"))
			{
				Remote2D.guiList.push(new GuiInGame(editor.getMap().copy()));
			} else if(secSubTitle.equalsIgnoreCase("Toggle Grid"))
			{
				editor.grid = !editor.grid;
			} else if(secSubTitle.equalsIgnoreCase("Increase Grid Size"))
			{
				editor.getMap().gridSize *= 2;
			} else if(secSubTitle.equalsIgnoreCase("Decrease Grid Size"))
			{
				if(editor.getMap().gridSize > 1)
					editor.getMap().gridSize /= 2;
			} else if(secSubTitle.equalsIgnoreCase("Zoom In"))
			{
				if(editor.getMap().camera.scale < 16)
					editor.getMap().setScaleAroundScreenPoint(new Vector2(Gui.screenWidth()/2,Gui.screenHeight()/2), editor.getMap().camera.scale*2);
			} else if(secSubTitle.equalsIgnoreCase("Zoom Out"))
			{
				if(editor.getMap().camera.scale > 0.25)
					editor.getMap().setScaleAroundScreenPoint(new Vector2(Gui.screenWidth()/2,Gui.screenHeight()/2), editor.getMap().camera.scale/2);
			} else if(secSubTitle.equalsIgnoreCase("Revert Zoom"))
				editor.getMap().setScaleAroundScreenPoint(new Vector2(Gui.screenWidth()/2,Gui.screenHeight()/2), 1);
		} else if(secTitle.equalsIgnoreCase("Component"))
		{
			if(editor.getSelectedEntity() != null)
				editor.executeOperation(new OperationAddComponent(editor,editor.getSelectedEntity(),InsertableComponentList.getComponentWithEntity(secSubTitle, editor.getMap().getEntityList().getEntityWithUUID(editor.getSelectedEntity()))));
		} else if(secTitle.equalsIgnoreCase("Entity"))
		{
			if(secSubTitle.equalsIgnoreCase("Insert Entity"))
			{
				editor.confirmOperation(new OperationNewEntity(editor));
			} else if(secSubTitle.equalsIgnoreCase("Delete Entity"))
			{
				OperationDeleteEntity delete = new OperationDeleteEntity(editor);
				editor.confirmOperation(delete);
			} else if(secSubTitle.equalsIgnoreCase("Create Prefab from Selected") && editor.getSelectedEntity() != null)
			{
				editor.pushWindow(new GuiWindowCreatePrefab(editor,new Vector2(i,j),editor.getWindowBounds(),editor.getMap().getEntityList().getEntityWithUUID(editor.getSelectedEntity())));
			}
		}
	}
	
	public void updateUndo()
	{
		sections.get(2).values[0] = "Undo";
		
		if(editor.peekUndoStack() != null)
			sections.get(2).values[0] = "Undo "+editor.peekUndoStack().name();
		
		sections.get(2).values[1] = "Redo";
		
		if(editor.peekRedoStack() != null)
			sections.get(2).values[1] = "Redo "+editor.peekRedoStack().name();
	}
	
}

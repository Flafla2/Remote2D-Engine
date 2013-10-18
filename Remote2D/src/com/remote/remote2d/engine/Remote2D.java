package com.remote.remote2d.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.editor.GuiWindowConsole;
import com.remote.remote2d.engine.art.ArtLoader;
import com.remote.remote2d.engine.art.CursorLoader;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.entity.InsertableComponentList;
import com.remote.remote2d.engine.entity.component.ComponentCamera;
import com.remote.remote2d.engine.entity.component.ComponentColliderBox;
import com.remote.remote2d.engine.gui.GuiMenu;
import com.remote.remote2d.engine.gui.MapHolder;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Map;

public class Remote2D {
	
	/*----------CORE VARIABLES--------------*/
	public static DisplayHandler displayHandler;
	private static Remote2DGame game;
	public static final boolean RESIZING_ENABLED = true;
	
	/*----------GAMELOOP VARIABLES----------*/
	public static boolean running = true;
	private static int fpsCounter = 0;
	private static int fps = 0;
	
	/**
	 * The speed at which the tick() function runs, in hertz.  In other words, the target "Ticks per second"
	 */
	private static final double GAME_HERTZ = 30.0;
	/**
	 * The calculated value in nanoseconds on how much time <i>should</i> be in between tick functions, based on GAME_HERTZ.
	 */
	private static final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
	/**
	 * An arbitrary value dictating the max amount of ticks() we should do if we are playing catchup.  The lower this is, the better
	 * render quality on slower machines, but physics/game logic will appear to slow down.  Set this to -1 for 100% accuracy.
	 */
	private static final int MAX_UPDATES_BEFORE_RENDER = 5;
	/**
	 * When the last time we ticked was.  This is used to determine how many times we need to tick().
	 */
	private static double lastUpdateTime = System.nanoTime();
	/**
	 * The last time that we rendered, in nanoseconds.  This is used to maintain a stable FPS using TARGET_FPS.
	 */
	private static double lastRenderTime = System.nanoTime();
	
	private static final double TARGET_FPS = 60;
	private static final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;
	
	/*----------GAME VARIABLES--------------*/
	/**
	 * ALL rendering and ticking, besides REALLY basic stuff goes through here.  Even in-game!
	 */
	public static Stack<GuiMenu> guiList;
	public static ArtLoader artLoader;	
	public static InsertableComponentList componentList;
	
	private static boolean mousePressed = false;
	private static boolean mouseReleased = false;
	private static int deltaWheel = 0;
	private static ArrayList<Character> charList;
	private static ArrayList<Character> charListLimited;
	private static ArrayList<Integer> keyboardList;
	private static String allowedChars = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-_=+\\][';:\"/.,?><`~ ";
	
	public static void startRemote2D(Remote2DGame game) {
				
		Thread thread = new Thread("Remote2D Thread") {
			
			@Override
			public void run()
			{
				Remote2D.start();
			}
			
		};
		
		Remote2D.game = game;
		thread.start();
		
	}
	
	public static void start()
	{
		Vector2 gameDim = game.getDefaultResolution();
		Vector2 winDim = game.getDefaultScreenResolution();
		displayHandler = new DisplayHandler((int)winDim.x,(int)winDim.y,(int)gameDim.x,(int)gameDim.y,game.getDefaultStretchType(),false,false);
		
		initGame();
		
		gameLoop();
		
		displayHandler.setDisplayMode(1024,576,false,false);
		Display.destroy();
		System.exit(0);
		
	}
	
	public static void initGame()
	{
		Log.setLogger(new ConsoleLogger());
		
		guiList = new Stack<GuiMenu>();
		charList = new ArrayList<Character>();
		charListLimited = new ArrayList<Character>();
		keyboardList = new ArrayList<Integer>();
		
		componentList = new InsertableComponentList();
		componentList.addInsertableComponent("Box Collider", ComponentColliderBox.class);
		componentList.addInsertableComponent("Camera", ComponentCamera.class);
				
		artLoader = new ArtLoader();
		
		game.initGame();
	}
	
	public static void gameLoop()
	{
		int lastSecondTime = (int) (lastUpdateTime / 1000000000);
		
		while (running)
		{
			if(Display.isCloseRequested())
				running = false;
			
			double now = System.nanoTime();
			int updateCount = 0;
			
			while( now - lastUpdateTime > TIME_BETWEEN_UPDATES && (updateCount < MAX_UPDATES_BEFORE_RENDER || MAX_UPDATES_BEFORE_RENDER == -1) )
			{
				int i = getMouseCoords()[0];
				int j = getMouseCoords()[1];
				try
				{
					tick(i, j, getMouseDown());
				} catch(Exception e)
				{
					GuiEditor editor = getEditor();
					if(editor != null)
					{
						Log.error("Exception detected on tick()!", e);
						while(!(guiList.peek() instanceof GuiEditor))
						{
							guiList.pop();
						}
						editor.pushWindow(new GuiWindowConsole(editor,new Vector2(100),new Vector2(300),editor.getWindowBounds()));
					} else if(!(e instanceof Remote2DException))
						throw new Remote2DException(e);
				}
				lastUpdateTime += TIME_BETWEEN_UPDATES;
				updateCount++;
			}
			
			if ( now - lastUpdateTime > TIME_BETWEEN_UPDATES)
			{
				lastUpdateTime = now - TIME_BETWEEN_UPDATES;
			}
			
			try
			{
				float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
				render(interpolation);
				fpsCounter++;
			} catch(Exception e)
			{
				GuiEditor editor = getEditor();
				if(editor != null)
				{
					Log.error("Exception detected on render()!", e);
					while(!(guiList.peek() instanceof GuiEditor))
					{
						guiList.pop();
					}
					editor.pushWindow(new GuiWindowConsole(editor,new Vector2(100),new Vector2(300),editor.getWindowBounds()));
				} else if(!(e instanceof Remote2DException))
					throw new Remote2DException(e);
			}
			Display.update();
			lastRenderTime = now;
			   
			int thisSecond = (int) (lastUpdateTime / 1000000000);
			if (thisSecond > lastSecondTime)
			{
				fps = fpsCounter;
				fpsCounter = 0;
				lastSecondTime = thisSecond;
			}
			   
			while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES)
			{
				Thread.yield();
				
				try {Thread.sleep(1);} catch(Exception e) {} 
				
				now = System.nanoTime();
			}
		}
	}
	
	public static int[] getMouseCoords()
	{
		Vector2 scale = displayHandler.getRenderScale();
		ColliderBox renderArea = displayHandler.getScreenRenderArea();
		int[] r = {(Mouse.getX()),(Mouse.getY())};
		r[0] -= renderArea.pos.x;
		r[1] -= renderArea.pos.y;
		r[0] /= scale.x;
		r[1] /= scale.y;
		r[1] = (int) (displayHandler.getDimensions().y-r[1]);
		return r;
	}
	
	public static int getMouseDown()
	{
		if(Mouse.isButtonDown(0))
			return 1;
		if(Mouse.isButtonDown(1))
			return 2;
		return 0;
	}
	
	public static void tick(int i, int j, int k)
	{
		updateKeyboardList();
		guiList.peek().tick(i, j, k);
	}
	
	public static File getJarPath()
	{
		return new File(Remote2D.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	}
	
	public static boolean hasMouseBeenPressed()
	{
		return mousePressed;
	}
	
	public static boolean hasMouseBeenReleased()
	{
		return mouseReleased;
	}
	
	private static void updateKeyboardList()
	{
		mousePressed = false;
		mouseReleased = false;
		while(Mouse.next())
		{
			boolean button = Mouse.getEventButtonState();
			int eventButton = Mouse.getEventButton();
			if(button && eventButton == 0)
			{
				mousePressed = true;
			} else if(!button && eventButton == 0)
				mouseReleased = true;
		}
		
		deltaWheel = Mouse.getDWheel();
		
		charList.clear();
		charListLimited.clear();
		keyboardList.clear();
		while(Keyboard.next())
		{
			if(Keyboard.getEventKeyState())
			{
				char c = Keyboard.getEventCharacter();
				if(allowedChars.contains((""+c)))
					charListLimited.add(c);
				if(Keyboard.getEventKey() == Keyboard.KEY_BACK)
					charListLimited.add('\b');
				charList.add(c);
				keyboardList.add(Keyboard.getEventKey());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Character> getKeyboardList()
	{
		return (ArrayList<Character>) charList.clone();
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Character> getLimitedKeyboardList()
	{
		return (ArrayList<Character>) charListLimited.clone();
	}
	
	public static ArrayList<Integer> getIntegerKeyboardList()
	{
		return keyboardList;
	}
	
	public static int getDeltaWheel()
	{
		return deltaWheel;
	}
	
	public static void render(float interpolation)
	{
		StretchType stretch = guiList.peek().getOverrideStretchType();
		if(stretch == null)
			stretch = game.getDefaultStretchType();
		if(stretch != displayHandler.getStretchType())
			displayHandler.setStretchType(stretch);
		
		GL11.glLoadIdentity();
		
		int color = guiList.peek().backgroundColor;
		float r = ((color >> 16) & 0xff)/255f;
		float g = ((color >> 8) & 0xff)/255f;
		float b = (color & 0xff)/255f;
		
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		Renderer.drawRect(new Vector2(0,0), displayHandler.getDimensions(), r, g, b, 1.0f);
		
		if(RESIZING_ENABLED)
			displayHandler.checkDisplayResolution();
				
		guiList.peek().render(interpolation);
		
		CursorLoader.render(interpolation);
	}
	
	/**
	 * Returns the topmost instance of the Editor in the Gui Stack.
	 * @return Instance of GuiEditor, or null if none exist.
	 */
	public static GuiEditor getEditor()
	{
		for(int x=0;x<guiList.size();x++)
			if(guiList.get(x) instanceof GuiEditor)
				return (GuiEditor)guiList.get(x);
		return null;
	}
	
	public static Remote2DGame getGame()
	{
		return game;
	}
	
	public static int getFPS()
	{
		return fps;
	}
	
	/**
	 * Finds the best version of the map by scanning through all available GUIs.
	 * The first one that it finds (starting from the top of the stack) that implements
	 * MapHolder is assumed to be the most plausible map to use.
	 * 
	 * @return The Map on the top of the stack.
	 */
	public static Map getMap()
	{
		for(GuiMenu menu : guiList)
		{
			if(menu instanceof MapHolder)
				return ((MapHolder)menu).getMap();
		}
		return null;
	}
	
	public static void shutDown()
	{
		Log.info("Remote2D Engine Shutting Down");
	}

}

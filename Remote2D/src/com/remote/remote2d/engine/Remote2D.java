package com.remote.remote2d.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.DefaultApplication;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.editor.GuiWindowConsole;
import com.remote.remote2d.engine.art.ArtLoader;
import com.remote.remote2d.engine.art.CursorLoader;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.TextureLoader;
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
	/**
	 * Whether or not the game is running.  If this is false, the game loop will
	 * stop after the current iteration of the loop and the game will shut down.
	 */
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
	private static final String allowedChars = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-_=+\\][';:\"/.,?><`~ ";
	
	private static void gameLoop()
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
				Vector2 mouseCoords = getMouseCoords();
				try
				{
					tick((int)mouseCoords.x, (int)mouseCoords.y, getMouseDown());
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
	
	private static void initGame()
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
	
	private static void render(float interpolation)
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
	
	private static void shutDown()
	{
		Log.info("Remote2D Engine Shutting Down");
	}
	
	private static void start()
	{
		Vector2 gameDim = game.getDefaultResolution();
		Vector2 winDim = game.getDefaultScreenResolution();
		displayHandler = new DisplayHandler((int)winDim.x,(int)winDim.y,(int)gameDim.x,(int)gameDim.y,game.getDefaultStretchType(),false,false);
		
		initGame();
		
		gameLoop();
		
		shutDown();
		displayHandler.setDisplayMode(1024,576,false,false);
		Display.destroy();
		System.exit(0);
		
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
	
	/**
	 * How much the mouse wheel has moved since the last tick.
	 */
	public static int getDeltaWheel()
	{
		return deltaWheel;
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
	
	public static int getFPS()
	{
		return fps;
	}
	
	/**
	 * The current instance of {@link Remote2DGame}.
	 * @see Remote2DGame
	 */
	public static Remote2DGame getGame()
	{
		return game;
	}
	
	/**
	 * The same thing as {@link #getKeyboardList()} except it contains LWJGL's integer index
	 * of that character instead of Java's Character class.
	 * 
	 * @see Keyboard#isKeyDown(int)
	 */
	public static ArrayList<Integer> getIntegerKeyboardList()
	{
		return keyboardList;
	}
	
	public static File getJarPath()
	{
		return new File(Remote2D.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	}
	
	/**
	 * A list of all keys that have been pressed this tick.
	 * 
	 * NOTE: This does NOT include keys that are held down.  Use {@link Keyboard#isKeyDown(int)}
	 * for seeing if keys are held down.
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Character> getKeyboardList()
	{
		return (ArrayList<Character>) charList.clone();
	}
	
	/**
	 * A limited list of all keys that have been pressed this tick.  Similar to
	 * {@link #getKeyboardList()}, however this only counts keys that are included
	 * in {@link #allowedChars}:<br /><br />
	 * 
	 * {@value #allowedChars}
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Character> getLimitedKeyboardList()
	{
		return (ArrayList<Character>) charListLimited.clone();
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
	
	/**
	 * The current coordinates of the mouse on-screen.  This is the same thing as <i>i</i> and <i>j</i>
	 * in {@link #tick(int, int, int)}.  Useful if mouse coordinates are needed while rendering.<br />
	 * 
	 * NOTE: You MUST use this instead of {@link Mouse#getX()} and {@link Mouse#getY()}
	 * due to the fact that 
	 */
	public static Vector2 getMouseCoords()
	{
		Vector2 scale = displayHandler.getRenderScale();
		ColliderBox renderArea = displayHandler.getScreenRenderArea();
		Vector2 r = new Vector2(Mouse.getX(),Mouse.getY());
		r.x -= renderArea.pos.x;
		r.y -= renderArea.pos.y;
		r.x /= scale.x;
		r.y /= scale.y;
		r.y = (int) (displayHandler.getDimensions().y-r.y);
		return r;
	}
	
	/**
	 * If the mouse has been pressed.  This is the same thing as <i>k</i>
	 * in {@link #tick(int, int, int)}.  Useful if mouse button state is needed while rendering.
	 */
	public static int getMouseDown()
	{
		if(Mouse.isButtonDown(0))
			return 1;
		if(Mouse.isButtonDown(1))
			return 2;
		return 0;
	}
	
	/**
	 * @return True if the mouse button has been pressed this tick (NOT held down, just pressed).  Otherwise, false.
	 */
	public static boolean hasMouseBeenPressed()
	{
		return mousePressed;
	}
	
	/**
	 * @return True if the mouse button has been released this tick.  Otherwise, false.
	 */
	public static boolean hasMouseBeenReleased()
	{
		return mouseReleased;
	}
	
	/**
	 * Starts Remote2D.  This initializes all engine-related tasks, opens up the
	 * display, and gets your game up and running.
	 * @param game This game's {@link Remote2DGame} instance.
	 * @see Remote2DGame
	 */
	public static void startRemote2D(Remote2DGame game) {
		
		if(System.getProperty("os.name").toLowerCase().indexOf("mac") != -1)
		{
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Remote2D");
			Application application = new DefaultApplication();
			application.addApplicationListener(new MacUIHandler());
			application.setEnabledPreferencesMenu(false);
			
			application.setApplicationIconImage(TextureLoader.loadImage(game.getIconPath()[0]));
		}
				
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
	
	public static void tick(int i, int j, int k)
	{
		updateKeyboardList();
		guiList.peek().tick(i, j, k);
	}
	
	public static String getVersion()
	{
		return "1.0 BETA";
	}

}

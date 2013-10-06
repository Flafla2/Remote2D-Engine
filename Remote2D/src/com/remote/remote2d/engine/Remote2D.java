package com.remote.remote2d.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.esotericsoftware.minlog.Log;
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
	private Remote2DGame game;
	private static final Remote2D instance = new Remote2D();
	public static final boolean RESIZING_ENABLED = true;

	/*----------GAMELOOP VARIABLES----------*/
	public boolean running = true;
	private int fpsCounter = 0;
	private int fps = 0;

	/**
	 * The speed at which the tick() function runs, in hertz. In other words,
	 * the target "Ticks per second"
	 */
	private final double GAME_HERTZ = 30.0;
	/**
	 * The calculated value in nanoseconds on how much time <i>should</i> be in
	 * between tick functions, based on GAME_HERTZ.
	 */
	private final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
	/**
	 * An arbitrary value dictating the max amount of ticks() we should do if we
	 * are playing catchup. The lower this is, the better render quality on
	 * slower machines, but physics/game logic will appear to slow down. Set
	 * this to -1 for 100% accuracy.
	 */
	private final int MAX_UPDATES_BEFORE_RENDER = 5;
	/**
	 * When the last time we ticked was. This is used to determine how many
	 * times we need to tick().
	 */
	private double lastUpdateTime = System.nanoTime();
	/**
	 * The last time that we rendered, in nanoseconds. This is used to maintain
	 * a stable FPS using TARGET_FPS.
	 */
	private double lastRenderTime = System.nanoTime();

	private final double TARGET_FPS = 60;
	private final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

	/*----------GAME VARIABLES--------------*/
	/**
	 * ALL rendering and ticking, besides REALLY basic stuff goes through here.
	 * Even in-game!
	 */
	public Stack<GuiMenu> guiList;
	public ArtLoader artLoader;
	public InsertableComponentList componentList;

	private boolean mousePressed = false;
	private boolean mouseReleased = false;
	private int deltaWheel = 0;
	private ArrayList<Character> charList;
	private ArrayList<Character> charListLimited;
	private ArrayList<Integer> keyboardList;
	private String allowedChars = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-_=+\\][';:\"/.,?><`~ ";

	public static void startRemote2D(Remote2DGame game) {

		Thread thread = new Thread("Remote2D Thread") {

			@Override
			public void run() {
				instance.start();
			}

		};

		instance.game = game;
		thread.start();

	}

	public static Remote2D getInstance() {
		return instance;
	}

	public void start() {
		Vector2 gameDim = game.getDefaultResolution();
		Vector2 winDim = game.getDefaultScreenResolution();
		DisplayHandler.initDisplayHandler((int) winDim.x, (int) winDim.y,
				(int) gameDim.x, (int) gameDim.y, game.getDefaultStretchType(),
				false, false);

		initGame();
		gameLoop();

		Display.destroy();
		System.exit(0);
	}

	public void initGame() {
		Log.setLogger(new ConsoleLogger());
		guiList = new Stack<GuiMenu>();
		charList = new ArrayList<Character>();
		charListLimited = new ArrayList<Character>();
		keyboardList = new ArrayList<Integer>();

		componentList = new InsertableComponentList();
		componentList.addInsertableComponent("Box Collider",
				ComponentColliderBox.class);
		componentList.addInsertableComponent("Camera", ComponentCamera.class);

		artLoader = new ArtLoader();

		game.initGame();
	}

	public void gameLoop() {
		int lastSecondTime = (int) (lastUpdateTime / 1000000000);

		while (running) {
			if (Display.isCloseRequested())
				running = false;
			else { // don't do all this if we're closing
				double now = System.nanoTime();
				int updateCount = 0;

				while (now - lastUpdateTime > TIME_BETWEEN_UPDATES
						&& (updateCount < MAX_UPDATES_BEFORE_RENDER || MAX_UPDATES_BEFORE_RENDER == -1)) {
					int i = getMouseCoords()[0];
					int j = getMouseCoords()[1];
					try {
						tick(i, j, getMouseDown());
					} catch (Exception e) {
						Log.error("Exception detected on tick()!", e);
					}
					lastUpdateTime += TIME_BETWEEN_UPDATES;
					updateCount++;
				}

				if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
					lastUpdateTime = now - TIME_BETWEEN_UPDATES;
				}

				try {
					float interpolation = Math
							.min(1.0f,
									(float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES));
					render(interpolation);
					fpsCounter++;
				} catch (Exception e) {
					Log.error("Exception detected on render()!", e);
				}
				Display.update();
				lastRenderTime = now;

				int thisSecond = (int) (lastUpdateTime / 1000000000);
				if (thisSecond > lastSecondTime) {
					fps = fpsCounter;
					fpsCounter = 0;
					lastSecondTime = thisSecond;
				}

				while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS
						&& now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
					Thread.yield();

					try {
						Thread.sleep(1);
					} catch (Exception e) {
					}

					now = System.nanoTime();
				}
			}
		}
	}

	public int[] getMouseCoords() {
		Vector2 scale = DisplayHandler.getRenderScale();
		ColliderBox renderArea = DisplayHandler.getScreenRenderArea();
		int[] r = { (Mouse.getX()), (Mouse.getY()) };
		r[0] -= renderArea.pos.x;
		r[1] -= renderArea.pos.y;
		r[0] /= scale.x;
		r[1] /= scale.y;
		r[1] = (int) (DisplayHandler.getDimensions().y - r[1]);
		return r;
	}

	public int getMouseDown() {
		if (Mouse.isButtonDown(0))
			return 1;
		if (Mouse.isButtonDown(1))
			return 2;
		return 0;
	}

	public void tick(int i, int j, int k) {
		updateKeyboardList();
		guiList.peek().tick(i, j, k);
	}

	public static File getJarPath() {
		return new File(Remote2D.class.getProtectionDomain().getCodeSource()
				.getLocation().getPath());
	}

	public boolean hasMouseBeenPressed() {
		return mousePressed;
	}

	public boolean hasMouseBeenReleased() {
		return mouseReleased;
	}

	private void updateKeyboardList() {
		mousePressed = false;
		mouseReleased = false;
		while (Mouse.next()) {
			boolean button = Mouse.getEventButtonState();
			int eventButton = Mouse.getEventButton();
			if (button && eventButton == 0) {
				mousePressed = true;
			} else if (!button && eventButton == 0)
				mouseReleased = true;
		}

		deltaWheel = Mouse.getDWheel();

		charList.clear();
		charListLimited.clear();
		keyboardList.clear();
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				char c = Keyboard.getEventCharacter();
				if (allowedChars.contains(("" + c)))
					charListLimited.add(c);
				if (Keyboard.getEventKey() == Keyboard.KEY_BACK)
					charListLimited.add('\b');
				charList.add(c);
				keyboardList.add(Keyboard.getEventKey());
			}
		}
	}

	public ArrayList<Character> getKeyboardList() {
		return charList;
	}

	public ArrayList<Character> getLimitedKeyboardList() {
		return charListLimited;
	}

	public ArrayList<Integer> getIntegerKeyboardList() {
		return keyboardList;
	}

	public int getDeltaWheel() {
		return deltaWheel;
	}

	public void render(float interpolation) {
		// StretchType stretch = guiList.peek().getOverrideStretchType();
		// if (stretch == null)
		// stretch = game.getDefaultStretchType();
		// if (stretch != displayHandler.getStretchType())
		// displayHandler.setStretchType(stretch);

		GL11.glLoadIdentity();

		int color = guiList.peek().backgroundColor;
		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;

		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		Renderer.drawRect(new Vector2(0, 0), DisplayHandler.getDimensions(), r,
				g, b, 1.0f);

		if (RESIZING_ENABLED)
			DisplayHandler.checkDisplayResolution();

		guiList.peek().render(interpolation);

		CursorLoader.render(interpolation);
	}

	public Remote2DGame getGame() {
		return game;
	}

	public int getFPS() {
		return fps;
	}

	/**
	 * Finds the best version of the map by scanning through all available GUIs.
	 * The first one that it finds (starting from the top of the stack) that
	 * implements MapHolder is assumed to be the most plausible map to use.
	 * 
	 * @return The Map on the top of the stack.
	 */
	public Map getMap() {
		for (GuiMenu menu : guiList) {
			if (menu instanceof MapHolder)
				return ((MapHolder) menu).getMap();
		}
		return null;
	}

	public void shutDown() {
		Log.info("Remote2D Engine Shutting Down");
	}
}

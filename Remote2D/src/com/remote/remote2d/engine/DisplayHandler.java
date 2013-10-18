package com.remote.remote2d.engine;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.art.TextureLoader;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;

public class DisplayHandler {
	
	private int screenWidth;
	private int screenHeight;
	private int gameWidth;
	private int gameHeight;
	private boolean fullscreen;
	private boolean borderless;
	private StretchType type;
	private long lastTexReload;
	
	public DisplayHandler(int width, int height, int gameWidth, int gameHeight, StretchType type, boolean fullscreen, boolean borderless)
	{
		this.screenWidth = borderless ? Display.getDesktopDisplayMode().getWidth() : width;
		this.screenHeight = borderless ? Display.getDesktopDisplayMode().getHeight() : height;
		this.fullscreen = fullscreen;
		this.borderless = borderless;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.type = type;
		
		try {
			Display.setDisplayMode(new DisplayMode(width,height));
			Display.setTitle("Remote2D");
			Display.setResizable(Remote2D.RESIZING_ENABLED);
			Display.setLocation(0, 0);
			Display.setFullscreen(fullscreen && !borderless);
			
			setIcons(Remote2D.getGame().getIconPath());
			
			Display.create();
		} catch (LWJGLException e) {
			throw new Remote2DException(e,"Failed to create LWJGL Display");
		}
		
		initGL();
	}
	
	public void setIcons(String[] icons)
	{
		if(icons == null)
			return;
		ByteBuffer[] buffers = new ByteBuffer[icons.length];
		for(int x=0;x<icons.length;x++)
			buffers[x] = getBufferFromImage(TextureLoader.loadImage(icons[x]),4);
		
		Display.setIcon(buffers);
	}
	
	public Vector2 getDimensions()
	{
		if(type != StretchType.NONE)
			return new Vector2(gameWidth,gameHeight);
		else
			return new Vector2(screenWidth,screenHeight);
	}
	
	public Vector2 getGameDimensions()
	{
		if(Remote2D.getGame().getDefaultStretchType() != StretchType.NONE)
			return new Vector2(gameWidth,gameHeight);
		else
			return new Vector2(screenWidth,screenHeight);
	}
	
	public Vector2 getScreenDimensions()
	{
		return new Vector2(screenWidth,screenHeight);
	}
	
	public boolean getFullscreen()
	{
		return fullscreen;
	}
	
	public StretchType getStretchType()
	{
		return type;
	}
	
	public boolean getBorderless()
	{
		return borderless;
	}
	
	public ByteBuffer getBufferFromImage(BufferedImage image, int BYTES_PER_PIXEL)
	{
		int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB
        
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip();
        
        return buffer;
	}
	
	public void checkDisplayResolution()
	{
		if(Display.getWidth() != screenWidth || Display.getHeight() != screenHeight)
		{
			Log.debug("Resolution not in sync!  LWJGL: "+Display.getWidth()+"x"+Display.getHeight()+" � Remote2D: "+screenWidth+"x"+screenHeight);
			screenWidth = Display.getWidth();
			screenHeight = Display.getHeight();
			initGL();
			
			for(int x=0;x<Remote2D.guiList.size();x++)
				Remote2D.guiList.get(x).initGui();
		}
	}
	
	public Vector2 getRenderScale()
	{
		ColliderBox renderArea = getScreenRenderArea();
		return renderArea.dim.divide(getDimensions());
	}
	
	public void initGL()
	{
		Log.debug("Initializing OpenGL");
		Log.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		Log.info("GLSL version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		GL11.glEnable(GL11.GL_TEXTURE_2D);               
        
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);          
        
    	// enable alpha blending
    	GL11.glEnable(GL11.GL_BLEND);
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    	ColliderBox renderArea = getScreenRenderArea();
    	GL11.glViewport((int)renderArea.pos.x,(int)renderArea.pos.y,(int)renderArea.dim.x,(int)renderArea.dim.y);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		Vector2 dim = getDimensions();
		GL11.glOrtho(0, dim.x, dim.y, 0, 1, -1);//Note, the GL coordinates are flipped!
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		lastTexReload = System.currentTimeMillis();
		
		//CursorLoader.setCursor(new Texture("/res/gui/mouse.png"), new Vector2D(22,22));
	}
	
	public ColliderBox getScreenRenderArea()
	{
		if(type == StretchType.NONE || type == StretchType.STRETCH)
			return new Vector2(0,0).getColliderWithDim(new Vector2(screenWidth,screenHeight));
		
		float targetAspectRatio = ((float)gameWidth)/((float)gameHeight);
		int width = screenWidth;
		int height = (int)(screenWidth/targetAspectRatio+0.5f);
		
		if(height > screenHeight)
		{
			height = screenHeight;
			width = (int)(height*targetAspectRatio+0.5f);
		}
		
		if(type == StretchType.MULTIPLES)
		{
			if(gameWidth <= screenWidth && gameHeight <= screenHeight)
			{
				width -= width%gameWidth;
				height -= height%gameHeight;
			} 
			else
			{
				width = gameWidth;
				height = gameHeight;
				while(width > screenWidth || height > screenHeight)
				{
					width /= 2;
					height /= 2;
				}
			}
		}
		
		Vector2 winPos = new Vector2(screenWidth/2-width/2,screenHeight/2-height/2);
		Vector2 winDim = new Vector2(width,height);
		
		return winPos.getColliderWithDim(winDim);
	}
	
	/**
	 * Sets the target resolution and Stretch Type of the game.  This is relatively expensive (requires reloading OpenGL and therefore all pictures) so use
	 * this sparingly.
	 * @param resx The target width of the game
	 * @param resy The target height of the game
	 * @param stretch Sets the stretch mode of the game
	 * @see StretchType
	 */
	public void setGameResolution(int resx, int resy, StretchType stretch)
	{
		gameWidth = resx;
		gameHeight = resy;
		type = stretch;
		initGL();
		
		for(int x=0;x<Remote2D.guiList.size();x++)
			Remote2D.guiList.get(x).initGui();
	}
	
	public void setStretchType(StretchType stretch)
	{
		type = stretch;
		initGL();
		
		for(int x=0;x<Remote2D.guiList.size();x++)
			Remote2D.guiList.get(x).initGui();
	}
	
	/**
	 * Set the display mode to be used 
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public void setDisplayMode(int width, int height, boolean fullscreen, boolean borderless) {
		int posX = Display.getX();
		int posY = Display.getY();
		
		if(borderless && fullscreen)
		{
			posX = 0;
			posY = 0;
			width = Display.getDesktopDisplayMode().getWidth();
			height = Display.getDesktopDisplayMode().getWidth();
			fullscreen = false;
		}

	    // return if requested DisplayMode is already set
	    if ((Display.getDisplayMode().getWidth() == width) && 
	        (Display.getDisplayMode().getHeight() == height) && 
	        (Display.isFullscreen() == fullscreen) &&
	        this.borderless == borderless)
	    {
		    return;
	    }

	    try {
	        DisplayMode targetDisplayMode = null;
			
		if (fullscreen) {
		DisplayMode[] modes = Display.getAvailableDisplayModes();
		int freq = 0;
		
		for (int i=0;i<modes.length;i++) {
			DisplayMode current = modes[i];
						
			if ((current.getWidth() == width) && (current.getHeight() == height)) {
				if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
					if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
						targetDisplayMode = current;
						freq = targetDisplayMode.getFrequency();
                }
            }

			    // if we've found a match for bpp and frequence against the 
			    // original display mode then it's probably best to go for this one
			    // since it's most likely compatible with the monitor
			    if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
	                        (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
	                            targetDisplayMode = current;
	                            break;
	                    }
	                }
	            }
	        } else {
	            targetDisplayMode = new DisplayMode(width,height);
	        }

	        if (targetDisplayMode == null) {
	            Log.warn("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
	            return;
	        }
	        	        
	        this.screenWidth = targetDisplayMode.getWidth();
	        this.screenHeight = targetDisplayMode.getHeight();
	        this.fullscreen = fullscreen;
	        
	        if(fullscreen == true)
	        	Display.destroy();
	        System.setProperty("org.lwjgl.opengl.Window.undecorated", borderless ? "true" : "false");
	        Display.setDisplayMode(targetDisplayMode);
	        Display.setFullscreen(fullscreen);
	        Display.setLocation(posX, posY);
	        setIcons(Remote2D.getGame().getIconPath());
	        Display.setVSyncEnabled(fullscreen);
	        if(fullscreen == true)
	        {
	        	Display.create();
	        }
	        
	        initGL();
	    } catch (LWJGLException e) {
	        Log.warn("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
	    }
	    
	    for(int x=0;x<Remote2D.guiList.size();x++)
			Remote2D.guiList.get(x).initGui();
	}
	
	/**
	 * The time, in milliseconds since the last OpenGL reload.  At this time,
	 * all textures clear and must be reloaded.
	 * @return The time in milliseconds.
	 */
	public long getLastTexReload()
	{
		return lastTexReload;
	}

}

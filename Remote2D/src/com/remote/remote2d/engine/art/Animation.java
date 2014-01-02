package com.remote.remote2d.engine.art;

import java.awt.image.BufferedImage;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.io.R2DFileSaver;
import com.remote.remote2d.engine.io.R2DTypeCollection;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;

/**
 * Sprite-based Animation system.  Animations work on a grid-based Spritesheet system.
 * This means that each frame in a spritesheet must:<br />
 * <ul>
 * 	<li>Be equal in size</li>
 * 	<li>Be equally spaced apart</li>
 * 	<li>Form a 2-dimensional grid at least 1 row high and 1 row long</li>
 * </ul>
 * 
 * Remote2D offers a sprite optimization tool to convert non-conforming spritesheets to a neat grid format.
 * 
 * NOTE: There can be more than one spritesheet on one image.  This can help save space and memory.
 * @author Flafla2
 */
public class Animation implements R2DFileSaver {
	
	private String texPath;
	private Vector2 startPos;
	private Vector2 spriteDim;
	private Vector2 padding;
	private Vector2 frames;
	private ColliderBox[] framePos;
	private int currentframe = 0;
	private int framelength;
	private long lastFrameTime;
	private String path;
	private Texture tex;
	
	/**
	 * If this is false the animation does not advance frames when rendering
	 */
	public boolean animate = true;
	/**
	 * If this is true, flips the sprite along the X-axis.  Useful for sprites
	 * which are identical for the "walk left" and "walk right" animations, etc.
	 * @see #flippedY
	 */
	public boolean flippedX = false;
	/**
	 * If this is true, flips the sprite along the Y-axis.
	 * @see #flippedX
	 */
	public boolean flippedY = false;
	
	/**
	 * All parameters pertain to the sprite's dimensions on the actual IMAGE, not in game
	 * while rendering.
	 * @param texPath Path to the animation's texture
	 * @param startPos Starting position of the sprite grid.
	 * @param spriteDim Dimensions of each individual frame of the animation.
	 * @param padding Padding in between each sprite
	 * @param frames Number of frames in the X and Y axis.  The animation system iterates from left to right -> up to down.
	 * @param framelength How long each frame is, in milliseconds.
	 */
	public Animation(String texPath, Vector2 startPos, Vector2 spriteDim, Vector2 padding, Vector2 frames, int framelength)
	{
		this.texPath = texPath;
		this.tex = ResourceLoader.getTexture(texPath);
		this.startPos = startPos;
		this.spriteDim = spriteDim;
		this.padding = padding;
		this.frames = frames;
		generateFrames();
		
		this.framelength = framelength;
		lastFrameTime = System.currentTimeMillis();
	}
	
	/**
	 * @param path Path to any Animation ".anim" file to load from, relative to the jar path
	 */
	public Animation(String path)
	{
		loadR2DFile(ResourceLoader.getCollection(path));
		this.path = path;
		this.tex = ResourceLoader.getTexture(texPath);
	}
	
	@Override
	public void saveR2DFile(R2DTypeCollection collection) {
		collection.setString("texture", texPath);
		collection.setVector2D("startPos", startPos);
		collection.setVector2D("spriteDim", spriteDim);
		collection.setVector2D("padding", padding);
		collection.setVector2D("frames", frames);
		collection.setInteger("framelength", framelength);
	}

	@Override
	public void loadR2DFile(R2DTypeCollection collection) {
		texPath = collection.getString("texture");
		startPos = collection.getVector2D("startPos");
		spriteDim = collection.getVector2D("spriteDim");
		padding = collection.getVector2D("padding");
		frames = collection.getVector2D("frames");
		framelength = collection.getInteger("framelength");
		generateFrames();
	}
	
	private void generateFrames()
	{
		framePos = new ColliderBox[(int) (frames.x*frames.y)];
		for(int x=0;x<frames.x;x++)
		{
			for(int y=0;y<frames.y;y++)
			{
				Vector2 pos = startPos.add(spriteDim.add(padding).multiply(new Vector2(x,y)));
				ColliderBox collider = pos.getColliderWithDim(spriteDim);
				framePos[(int) (frames.x*y+x)] = collider;
			}
		}
	}
	
	/**
	 * Path to the saved animation file of this animation, if it exists.
	 */
	public String getPath()
	{
		return path;
	}
	
	/**
	 * Renders this animation.  Note: The animation WILL stretch if the dimensions are not proportional!
	 * @param pos Position (top left corner) to render
	 * @param dim Dimensions of rendered sprite
	 * @see #render(Vector2, Vector2, int, float)
	 */
	public void render(Vector2 pos, Vector2 dim)
	{
		render(pos,dim,0xffffff,1);
	}
	
	/**
	 * Renders this animation.  Note: The animation WILL stretch if the dimensions are not proportional!
	 * @param pos Position (top left corner) to render
	 * @param dim Dimensions of rendered sprite
	 * @param color Color (in hex) to render as.  For example 0xff5555 will render as a red tint
	 * @param alpha How opaque it is (0.0-1.0)
	 * @see #render(Vector2, Vector2)
	 */
	public void render(Vector2 pos, Vector2 dim, int color, float alpha)
	{
		if(framePos == null)
		{
			Log.debug("framePos == null!");
			return;
		}
		if(!animate)
			lastFrameTime += System.currentTimeMillis()-lastFrameTime;
		if(System.currentTimeMillis()-lastFrameTime > framelength)
		{
			currentframe++;
			if(currentframe >= framePos.length)
				currentframe = 0;
			lastFrameTime = System.currentTimeMillis();
		}
		
		ColliderBox collider = framePos[currentframe];
		BufferedImage image = tex.getImage();
		Vector2 imgPos = collider.pos.divide(new Vector2(image.getWidth(),image.getHeight()));
		Vector2 imgDim = collider.dim.divide(new Vector2(image.getWidth(),image.getHeight()));
		image.flush();
		image = null;
		
		if(flippedX)
		{
			imgPos.x += imgDim.x;
			imgDim.x *= -1;
		}
		
		if(flippedY)
		{
			imgPos.y += imgDim.y;
			imgDim.y *= -1;
		}
		
		Renderer.drawRect(pos, dim, imgPos, imgDim, tex, color, alpha);
	}
	
	/**
	 * The top left corner of the first sprite, on the sprite sheet.
	 */
	public Vector2 getStartPos() {
		return startPos;
	}
	
	/**
	 * The top left corner of the first sprite, on the sprite sheet.
	 */
	public void setStartPos(Vector2 startPos) {
		this.startPos = startPos;
		generateFrames();
	}
	
	/**
	 * The dimensions of the each sprite, on the sprite sheet.
	 */
	public Vector2 getSpriteDim() {
		return spriteDim;
	}
	
	/**
	 * The dimensions of the each sprite, on the sprite sheet.
	 */
	public void setSpriteDim(Vector2 spriteDim) {
		this.spriteDim = spriteDim;
		generateFrames();
	}
	
	/**
	 * The distance between each frame, on the sprite sheet.
	 */
	public Vector2 getPadding() {
		return padding;
	}
	
	/**
	 * The distance between each frame, on the sprite sheet.
	 */
	public void setPadding(Vector2 padding) {
		this.padding = padding;
		generateFrames();
	}
	
	/**
	 * How many frames there are on the sprite sheet, in both X and Y directions.
	 * For example, if there are 10 frames, with 5 frames on each row on the sprite sheet,
	 * this would be put (5,2)
	 */
	public Vector2 getFrames() {
		return frames;
	}
	
	/**
	 * How many frames there are on the sprite sheet, in both X and Y directions.
	 * For example, if there are 10 frames, with 5 frames on each row on the sprite sheet,
	 * this would be put (5,2)
	 */
	public void setFrames(Vector2 frames) {
		this.frames = frames;
		generateFrames();
	}
	
	/**
	 * Length of each frame, in ms
	 */
	public int getFramelength() {
		return framelength;
	}
	
	/**
	 * Length of each frame, in ms
	 */
	public void setFramelength(int framelength) {
		this.framelength = framelength;
		generateFrames();
	}
	
	/**
	 * Path to the source texture, relative to the Remote2D jar file
	 */
	public String getTexPath()
	{
		return texPath;
	}
	
	/**
	 * Time when the last frame rendered, in ms
	 */
	public long getLastFrameTime() {
		return lastFrameTime;
	}
	
	/**
	 * Set the time when the last frame rendered, in ms.  This can
	 * be used to reset the animation to the beginning of the current
	 * frame.
	 */
	public void setLastFrameTime(long lastFrameTime) {
		this.lastFrameTime = lastFrameTime;
		generateFrames();
	}
	
	/**
	 * Renders the position of every frame (on the spritesheet) as a green box.  Useful
	 * for debugging purposes.
	 */
	public void renderFrames() {
		
		if(framePos == null)
			return;
		
		for(int x = 0;x<framePos.length;x++)
		{
			framePos[x].drawCollider(0x00ff00);
		}
	}
	
	public void setCurrentFrame(int frame)
	{
		if(frame < frames.x*frames.y && frame >= 0)
		{
			lastFrameTime = System.currentTimeMillis();
			currentframe = frame;
		}
	}

	public static String getExtension() {
		return ".anim";
	}
	
	@Override
	public Animation clone()
	{
		Animation newAnim = new Animation(texPath, startPos.copy(), spriteDim.copy(), padding.copy(), frames.copy(), framelength);
		return newAnim;
	}
	
}

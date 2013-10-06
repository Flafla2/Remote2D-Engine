package com.remote.remote2d.engine.art;

import org.lwjgl.opengl.GL11;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.io.R2DFileSaver;
import com.remote.remote2d.engine.io.R2DTypeCollection;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;

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
	
	public boolean flippedX = false;
	public boolean flippedY = false;
	
	
	public Animation(String texPath, Vector2 startPos, Vector2 spriteDim, Vector2 padding, Vector2 frames, int framelength)
	{
		this.texPath = texPath;
		this.tex = new Texture(texPath);
		this.startPos = startPos;
		this.spriteDim = spriteDim;
		this.padding = padding;
		this.frames = frames;
		generateFrames();
		
		this.framelength = framelength;
		lastFrameTime = System.currentTimeMillis();
	}
	
	public Animation(String path)
	{
		R2DFileManager manager = new R2DFileManager(path,this);
		manager.read();
		this.path = path;
		this.tex = new Texture(texPath);
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
	
	public void generateFrames()
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
	
	public String getPath()
	{
		return path;
	}
	
	public void render(Vector2 pos, Vector2 dim)
	{
		render(pos,dim,0xffffff,1);
	}
	
	public void render(Vector2 pos, Vector2 dim, int color, float alpha)
	{
		if(framePos == null)
		{
			Log.debug("framePos == null!");
			return;
		}
		if(System.currentTimeMillis()-lastFrameTime > framelength)
		{
			currentframe++;
			if(currentframe >= framePos.length)
				currentframe = 0;
			lastFrameTime = System.currentTimeMillis();
		}
		
		ColliderBox collider = framePos[currentframe];
		Vector2 imgPos = collider.pos.divide(new Vector2(tex.image.getWidth(),tex.image.getHeight()));
		Vector2 imgDim = collider.dim.divide(new Vector2(tex.image.getWidth(),tex.image.getHeight()));
		
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
	
	public Vector2 getStartPos() {
		return startPos;
	}

	public void setStartPos(Vector2 startPos) {
		this.startPos = startPos;
		generateFrames();
	}

	public Vector2 getSpriteDim() {
		return spriteDim;
	}

	public void setSpriteDim(Vector2 spriteDim) {
		this.spriteDim = spriteDim;
		generateFrames();
	}

	public Vector2 getPadding() {
		return padding;
	}

	public void setPadding(Vector2 padding) {
		this.padding = padding;
		generateFrames();
	}

	public Vector2 getFrames() {
		return frames;
	}

	public void setFrames(Vector2 frames) {
		this.frames = frames;
		generateFrames();
	}

	public int getFramelength() {
		return framelength;
	}

	public void setFramelength(int framelength) {
		this.framelength = framelength;
		generateFrames();
	}
	
	public String getTexPath()
	{
		return texPath;
	}

	public long getLastFrameTime() {
		return lastFrameTime;
	}

	public void setLastFrameTime(long lastFrameTime) {
		this.lastFrameTime = lastFrameTime;
		generateFrames();
	}

	public void renderFrames() {
		
		if(framePos == null)
			return;
		
		for(int x = 0;x<framePos.length;x++)
		{
			framePos[x].drawCollider(0x00ff00);
		}
	}

	public static String getExtension() {
		return ".anim";
	}
	
	public Animation clone()
	{
		Animation newAnim = new Animation(texPath, startPos.copy(), spriteDim.copy(), padding.copy(), frames.copy(), framelength);
		return newAnim;
	}
	
}

package com.remote.remote2d.editor;

import java.awt.image.BufferedImage;

import org.lwjgl.input.Keyboard;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.StretchType;
import com.remote.remote2d.engine.art.Animation;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiMenu;
import com.remote.remote2d.engine.gui.GuiTextField;
import com.remote.remote2d.engine.gui.TextLimiter;
import com.remote.remote2d.engine.io.R2DFileManager;
import com.remote.remote2d.engine.io.R2DFileUtility;
import com.remote.remote2d.engine.logic.Interpolator;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiCreateSpriteSheet extends GuiMenu {
	
	Animation animation;
	GuiTextField texID;
	GuiTextField startX;
	GuiTextField startY;
	GuiTextField dimX;
	GuiTextField dimY;
	GuiTextField framesX;
	GuiTextField framesY;
	GuiTextField paddingX;
	GuiTextField paddingY;
	GuiTextField frameLength;
	GuiTextField animSave;
	
	GuiButton createButton;
	GuiButton regenButton;
	
	Vector2 oldOffset;
	Vector2 offset;
	
	int scale = 1;
	
	Texture tex;
	
	String message = "";
	long messageTime = 5000;
	long lastMessageTime = 0;
	
	public GuiCreateSpriteSheet()
	{
		backgroundColor = 0x7f9ddf;
		texID = new GuiTextField(new Vector2(100,10), new Vector2(200,40), 20);
		
		startX = new GuiTextField(new Vector2(100,55), new Vector2(100,40), 20);
		startX.limitToDigits = TextLimiter.LIMIT_TO_INTEGER;
		startX.defaultText = "X";
		startY = new GuiTextField(new Vector2(200,55), new Vector2(100,40), 20);
		startY.defaultText = "Y";
		startY.limitToDigits = TextLimiter.LIMIT_TO_INTEGER;
		
		dimX = new GuiTextField(new Vector2(100,100), new Vector2(100,40), 20);
		dimX.limitToDigits = TextLimiter.LIMIT_TO_INTEGER;
		dimX.defaultText = "X";
		dimY = new GuiTextField(new Vector2(200,100), new Vector2(100,40), 20);
		dimY.defaultText = "Y";
		dimY.limitToDigits = TextLimiter.LIMIT_TO_INTEGER;
		
		framesX = new GuiTextField(new Vector2(100,145), new Vector2(100,40), 20);
		framesX.defaultText = "X";
		framesX.limitToDigits = TextLimiter.LIMIT_TO_INTEGER;
		framesY = new GuiTextField(new Vector2(200,145), new Vector2(100,40), 20);
		framesY.defaultText = "Y";
		framesY.limitToDigits = TextLimiter.LIMIT_TO_INTEGER;
		
		paddingX = new GuiTextField(new Vector2(100,190), new Vector2(100,40), 20);
		paddingX.defaultText = "X";
		paddingX.limitToDigits = TextLimiter.LIMIT_TO_INTEGER;
		paddingY = new GuiTextField(new Vector2(200,190), new Vector2(100,40), 20);
		paddingY.defaultText = "Y";
		paddingY.limitToDigits = TextLimiter.LIMIT_TO_INTEGER;
		
		frameLength = new GuiTextField(new Vector2(100,235), new Vector2(200,40), 20);
		frameLength.defaultText = "(ms)";
		frameLength.limitToDigits = TextLimiter.LIMIT_TO_INTEGER;
		
		animSave = new GuiTextField(new Vector2(100,280), new Vector2(200,40), 20);
		
		offset = new Vector2(300,0);
		oldOffset = new Vector2(300,0);
	}
	
	public GuiCreateSpriteSheet(Animation anim)
	{
		this();
		texID.text = anim.getTexPath();
		startX.text = (int)anim.getStartPos().x+"";
		startY.text = (int)anim.getStartPos().y+"";
		dimX.text = (int)anim.getSpriteDim().x+"";
		dimY.text = (int)anim.getSpriteDim().y+"";
		framesX.text = (int)anim.getFrames().x+"";
		framesY.text = (int)anim.getFrames().y+"";
		paddingX.text = (int)anim.getPadding().x+"";
		paddingY.text = (int)anim.getPadding().y+"";
		frameLength.text = anim.getFramelength()+"";
		animSave.text = anim.getPath();
	}
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		
		createButton = new GuiButton(0,new Vector2(5,screenHeight()-45),new Vector2(100,40),"Create");
		buttonList.add(createButton);
		buttonList.add(new GuiButton(1,new Vector2(110,screenHeight()-45),new Vector2(100,40),"Done"));
		buttonList.add(regenButton = new GuiButton(2,new Vector2(5,screenHeight()-90),new Vector2(205,40),"Regenerate"));
	}
	
	@Override
	public void render(float interpolation)
	{
		super.render(interpolation);
		texID.render(interpolation);
		startX.render(interpolation);
		startY.render(interpolation);
		dimX.render(interpolation);
		dimY.render(interpolation);
		framesX.render(interpolation);
		framesY.render(interpolation);
		paddingX.render(interpolation);
		paddingY.render(interpolation);
		frameLength.render(interpolation);
		animSave.render(interpolation);
		
		Fonts.get("Arial").drawString("Tex. Path", 5, 20, 20, 0xffffff);
		Fonts.get("Arial").drawString("Start Pos", 5, 65, 20, 0xffffff);
		Fonts.get("Arial").drawString("Sprite Dim", 5, 110, 20, 0xffffff);
		Fonts.get("Arial").drawString("# Frames", 5, 155, 20, 0xffffff);
		Fonts.get("Arial").drawString("Padding", 5, 200, 20, 0xffffff);
		Fonts.get("Arial").drawString("Length", 5, 245, 20, 0xffffff);
		Fonts.get("Arial").drawString("Path", 5, 290, 20, 0xffffff);
		if(!isReady() || !animSave.hasText())
			Fonts.get("Arial").drawString("Fill out all fields!", 5, 335, 20, 0xff0000);
		else if(!animSave.text.endsWith(Animation.getExtension()))
			Fonts.get("Arial").drawString("Use "+Animation.getExtension()+" extension", 5, 335, 20, 0xff0000);
		if(System.currentTimeMillis()-lastMessageTime <= messageTime)
			Fonts.get("Arial").drawString(message, 5, screenHeight()-110, 20, 0xffffff);
	}
	
	@Override
	public void renderBackground(float interpolation)
	{
		Vector2 realOffset = Interpolator.linearInterpolate(oldOffset, offset, interpolation);
		
		drawBlueprintBackground();
		if(R2DFileUtility.textureExists(texID.text) && tex != null)
		{
			if(!tex.getTextureLocation().equals(texID.text))
			{
				tex.removeTexture();
				tex = new Texture(texID.text);
			}
			BufferedImage image = tex.getImage();
			Vector2 dim = new Vector2(image.getWidth(),image.getHeight());
			image.flush();
			image = null;
			
			Renderer.startScissor(new Vector2(300,0), new Vector2(screenWidth()-300,screenHeight()));
			
			Renderer.pushMatrix();
				Renderer.translate(realOffset);
				Renderer.scale(scale);
				Renderer.drawRect(new Vector2(0,0), dim, tex, 0xffffff, 1.0f);
			Renderer.popMatrix();
			if(animation != null)
			{
				Renderer.pushMatrix();
					Renderer.translate(realOffset);
					Renderer.scale(scale);
					animation.renderFrames();
				Renderer.popMatrix();
				Renderer.endScissor();
				
				Vector2 spriteDim = animation.getSpriteDim();
				animation.render(new Vector2(10,360), spriteDim);
			}
			Renderer.endScissor();
		}
	}
	
	@Override
	public void tick(int i, int j, int k)
	{
		super.tick(i, j, k);
		texID.tick(i, j, k);
		startX.tick(i, j, k);
		startY.tick(i, j, k);
		dimX.tick(i, j, k);
		dimY.tick(i, j, k);
		framesX.tick(i, j, k);
		framesY.tick(i, j, k);
		paddingX.tick(i, j, k);
		paddingY.tick(i, j, k);
		frameLength.tick(i, j, k);
		animSave.tick(i, j, k);
		
		oldOffset = offset.copy();
		
		createButton.setDisabled(!(isReady() && animSave.hasText() && animSave.text.endsWith(Animation.getExtension())));
		regenButton.setDisabled(!isReady());
		
		if(Remote2D.getKeyboardList().contains('[') && scale >= 2)
			scale /= 2;
		else if(Remote2D.getKeyboardList().contains(']'))
			scale *= 2;

		if(R2DFileUtility.textureExists(texID.text) )
		{
			reloadTex();
			boolean up = Keyboard.isKeyDown(Keyboard.KEY_UP);
			boolean down = Keyboard.isKeyDown(Keyboard.KEY_DOWN);
			boolean left = Keyboard.isKeyDown(Keyboard.KEY_LEFT);
			boolean right = Keyboard.isKeyDown(Keyboard.KEY_RIGHT);
			if(up)
				offset.y += 5;
			if(down)
				offset.y -= 5;
			if(left)
				offset.x += 5;
			if(right)
				offset.x -= 5;
			
			BufferedImage image = tex.getImage();
			if(offset.x+image.getWidth()*scale < screenWidth())
				offset.x = screenWidth()-image.getWidth();
			if(offset.y+image.getHeight()*scale < screenHeight())
				offset.y = screenHeight()-image.getHeight();
			image.flush();
			image = null;
			
			if(offset.x > 300)
				offset.x = 300;
			if(offset.y > 0)
				offset.y = 0;
		}
	}
	
	public void reloadTex()
	{
		if(tex == null || !tex.getTextureLocation().equals(texID.text))
		{
			if(tex != null)
				tex.removeTexture();
			tex = new Texture(texID.text);
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			R2DFileManager manager = new R2DFileManager(animSave.text,animation);
			manager.write();
			lastMessageTime = System.currentTimeMillis();
			message = "File "+manager.getFile().getName()+" saved.";
			//Remote2D.guiList.pop();
		} else if(button.id == 1)
		{
			Remote2D.guiList.pop();
		} else if(button.id == 2)
		{
			if(isReady())
			{
				animation = new Animation(
						texID.text,
						new Vector2(Integer.parseInt(startX.text),Integer.parseInt(startY.text)),
						new Vector2(Integer.parseInt(dimX.text),Integer.parseInt(dimY.text)),
						new Vector2(Integer.parseInt(paddingX.text),Integer.parseInt(paddingY.text)),
						new Vector2(Integer.parseInt(framesX.text),Integer.parseInt(framesY.text)),
						Integer.parseInt(frameLength.text)
				);
			}
		}
	}
	
	private boolean isReady()
	{
		boolean containsKey = R2DFileUtility.textureExists(texID.text);
		boolean total = texID.hasText() && startX.hasText() && startY.hasText() 
				&& dimX.hasText() && dimY.hasText() && framesX.hasText() &&
				framesY.hasText() && paddingX.hasText() && paddingY.hasText()
				&& containsKey && frameLength.hasText();
		return total;
	}
	
	@Override
	public StretchType getOverrideStretchType()
	{
		return StretchType.NONE;
	}

}

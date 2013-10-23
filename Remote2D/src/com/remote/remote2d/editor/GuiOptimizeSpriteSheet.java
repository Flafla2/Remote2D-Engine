package com.remote.remote2d.editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.Remote2DException;
import com.remote.remote2d.engine.StretchType;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiMenu;
import com.remote.remote2d.engine.gui.GuiTextField;
import com.remote.remote2d.engine.gui.TextLimiter;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Interpolator;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiOptimizeSpriteSheet extends GuiMenu {
	
	GuiTextField removeField;
	GuiTextField texturePath;
	GuiTextField savePath;
	GuiButton button;
	GuiButton bgButton;
	Vector2 oldOffset;
	Vector2 offset;
	ArrayList<ColliderDefinerBox> frameDefiners;
	ColliderDefinerBox activeDefiner = null;
	int scale = 1;
	boolean isPickingBG = false;
	int bgColor = 0xffffff;
	Texture tex;
	
	
	HorizontalPositioning horizontal = HorizontalPositioning.MIDDLE;
	VerticalPositioning vertical = VerticalPositioning.MIDDLE;
	
	public GuiOptimizeSpriteSheet()
	{
		backgroundColor = 0x7f9ddf;
		frameDefiners = new ArrayList<ColliderDefinerBox>();
		removeField = new GuiTextField(new Vector2(10,50),new Vector2(200,40),20);
		removeField.limitToDigits = TextLimiter.LIMIT_TO_INTEGER;
		texturePath = new GuiTextField(new Vector2(10,120),new Vector2(280,40),20);
		savePath = new GuiTextField(new Vector2(10,270),new Vector2(280,40),20);
		offset = new Vector2(300,0);
		oldOffset = new Vector2(300,0);
	}
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		buttonList.add(bgButton = new GuiButton(0,new Vector2(10,10),new Vector2(240,40),"Pick Background Color").setDisabled(true));
		buttonList.add(new GuiButton(1,new Vector2(210,50),new Vector2(80,40),"Remove"));
		buttonList.add(new GuiButton(2,new Vector2(5,170),new Vector2(90,40),"Left"));
		buttonList.add(new GuiButton(3,new Vector2(105,170),new Vector2(90,40),"Mid"));
		buttonList.add(new GuiButton(4,new Vector2(205,170),new Vector2(90,40),"Right"));
		buttonList.add(new GuiButton(5,new Vector2(5,220),new Vector2(90,40),"Top"));
		buttonList.add(new GuiButton(6,new Vector2(105,220),new Vector2(90,40),"Mid"));
		buttonList.add(new GuiButton(7,new Vector2(205,220),new Vector2(90,40),"Bot"));
		buttonList.add(button = new GuiButton(8,new Vector2(10,320),new Vector2(280,40),"Done").setDisabled(true));
		buttonList.add(new GuiButton(9,new Vector2(10,370),new Vector2(280,40),"Cancel"));
	}
	
	@Override
	public void render(float interpolation)
	{
		super.render(interpolation);
		removeField.render(interpolation);
		Fonts.get("Arial").drawString("Texture Path", 10, 95, 20, 0xffffff);
		texturePath.render(interpolation);
		savePath.render(interpolation);
		
		Renderer.drawRect(new Vector2(250,10), new Vector2(40,40), bgColor, 1.0f);
		
		String infoH = "Horizontal:";
		switch(horizontal)
		{
		case LEFT:
			infoH += "LEFT";
			break;
		case MIDDLE:
			infoH += "MIDDLE";
			break;
		case RIGHT:
			infoH += "RIGHT";
			break;
		}
		
		String infoV = "Vertical: ";
		switch(vertical)
		{
		case TOP:
			infoV += "TOP";
			break;
		case MIDDLE:
			infoV += "MIDDLE";
			break;
		case BOTTOM:
			infoV += "BOTTOM";
			break;
		}
		Fonts.get("Arial").drawString(infoH, 10, 420, 20, 0xffffff);
		Fonts.get("Arial").drawString(infoV, 10, 440, 20, 0xffffff);
	}
	
	@Override
	public void renderBackground(float interpolation)
	{
		Vector2 iOffset = Interpolator.linearInterpolate(oldOffset, offset, interpolation);
		
		drawBlueprintBackground();
		if(Remote2D.artLoader.textureExists(texturePath.text))
		{
			if(!tex.getTextureLocation().equals(texturePath.text))
			{
				tex.removeTexture();
				tex = new Texture(texturePath.text);
			}
			tex.bind();			
			BufferedImage image = tex.getImage();
			Vector2 dim = new Vector2(image.getWidth(),image.getHeight());
			image.flush();
			image = null;
			
			Renderer.startScissor(new Vector2(300,0), new Vector2(screenWidth()-300,screenHeight()));
			
			GL11.glPushMatrix();
				GL11.glTranslatef(iOffset.x, iOffset.y, 0);
				GL11.glScalef(scale, scale, 1);
				
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex2f(0, 0);
					GL11.glTexCoord2f(1, 0);
					GL11.glVertex2f(dim.x, 0);
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex2f(dim.x,dim.y);
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex2f(0, dim.y);
				GL11.glEnd();
				
				for(int x=0;x<frameDefiners.size();x++)
				{
					ColliderBox coll = (ColliderBox) frameDefiners.get(x).getCollider();
					coll.drawCollider(0xff0000);
					Fonts.get("Logo").drawString(""+x, coll.pos.x+coll.dim.x, coll.pos.y+coll.dim.y, 10, 0xff0000);
				}
				if(activeDefiner != null)
					activeDefiner.getCollider().drawCollider(0xff0000);
	 		GL11.glPopMatrix();
	 		
	 		if(Mouse.getX() > 300)
	 		{
		 		GL11.glColor3f(1,0,0);
				GL11.glBegin(GL11.GL_LINES);
					int y = screenHeight()-Mouse.getY();
					int x = Mouse.getX();
					boolean right = ((x-offset.x)%scale)/(scale) >= 0.5;
					boolean bottom = ((y-offset.y)%scale)/(scale) >= 0.5;
					GL11.glVertex2f(0, y-(y-offset.y)%scale+(bottom?scale:0));
					GL11.glVertex2f(screenWidth(), y-(y-offset.y)%scale+(bottom?scale:0));
					
					GL11.glVertex2f(x-(x-offset.x)%scale+(right?scale:0), 0);
					GL11.glVertex2f(x-(x-offset.x)%scale+(right?scale:0), screenHeight());
				GL11.glEnd();
				GL11.glColor3f(1,1,1);
	 		}
			Renderer.endScissor();
		}
	}
	
	@Override
	public void tick(int i, int j, int k)
	{
		super.tick(i, j, k);
		removeField.tick(i, j, k);
		texturePath.tick(i,j,k);
		savePath.tick(i,j,k);
		
		oldOffset = offset.copy();
		
		if(i > 300 && activeDefiner != null)
		{
			boolean right = ((i-offset.x)%scale)/(scale) >= 0.5;
			boolean bottom = ((j-offset.y)%scale)/(scale) >= 0.5;
			
			activeDefiner.hover((i-offset.x)/scale+(right?1:0), (j-offset.y)/scale+(bottom?1:0));
			if(Remote2D.hasMouseBeenPressed())
			{
				if(isPickingBG)
				{
					if(!tex.getTextureLocation().equals(texturePath.text))
					{
						tex.removeTexture();
						tex = new Texture(texturePath.text);
					}
					int x = (int) ((i-offset.x)/scale);
					int y = (int) ((j-offset.y)/scale);
					if(tex != null && x < tex.getImage().getWidth() && y < tex.getImage().getHeight())
					{
						bgColor = tex.getImage().getRGB(x, y);
					}
					
					isPickingBG = !isPickingBG;
					bgButton.text = isPickingBG ? "Cancel" : "Pick Background Color";
					activeDefiner = new ColliderDefinerBox();
				} else
				{
					activeDefiner.click();
					if(activeDefiner.isDefined())
					{
						frameDefiners.add(activeDefiner);
						activeDefiner = null;
					}
				}
			}
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			activeDefiner = null;
		
		if(activeDefiner == null)
			activeDefiner = new ColliderDefinerBox();
		
		if(Remote2D.getKeyboardList().contains('[') && scale >= 2)
			scale /= 2;
		else if(Remote2D.getKeyboardList().contains(']'))
			scale *= 2;
		
		if(!Remote2D.artLoader.textureExists(texturePath.text) || !savePath.text.startsWith("/") || !savePath.text.endsWith(".png") || savePath.text.endsWith("/.png"))
			button.setDisabled(true);
		else
		{
			if(button.getDisabled())
				button.setDisabled(false);
		}
		
		if(!Remote2D.artLoader.textureExists(texturePath.text))
			bgButton.setDisabled(true);
		else
		{
			if(bgButton.getDisabled())
				bgButton.setDisabled(false);
		}
			
		
		if(Remote2D.artLoader.textureExists(texturePath.text) )
		{
			if(!tex.getTextureLocation().equals(texturePath.text))
			{
				tex.removeTexture();
				tex = new Texture(texturePath.text);
			}
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
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			isPickingBG = !isPickingBG;
			bgButton.text = isPickingBG ? "Cancel" : "Pick Background Color";
			activeDefiner = new ColliderDefinerBox();
		} else if(button.id == 1)
		{
			if(removeField.text.trim().equals(""))
				return;
			int parse = Integer.parseInt(removeField.text);
			if(parse >= frameDefiners.size())
				return;
			frameDefiners.remove(parse);
		} else if(button.id == 2)
			horizontal = HorizontalPositioning.LEFT;
		 else if(button.id == 3)
				horizontal = HorizontalPositioning.MIDDLE;
		 else if(button.id == 4)
				horizontal = HorizontalPositioning.RIGHT;
		 else if(button.id == 5)
				vertical = VerticalPositioning.TOP;
		 else if(button.id == 6)
				vertical = VerticalPositioning.MIDDLE;
		 else if(button.id == 7)
				vertical = VerticalPositioning.BOTTOM;
		 else if(button.id == 8)
		 {
			if(!tex.getTextureLocation().equals(texturePath.text))
			{
				tex.removeTexture();
				tex = new Texture(texturePath.text);
			}
			 BufferedImage source = tex.getImage();
			 Vector2 frameSize = new Vector2(0,0);
			 for(int x=0;x<frameDefiners.size();x++)
			 {
				 ColliderBox coll = (ColliderBox)frameDefiners.get(x).getCollider();
				 if(coll.dim.x > frameSize.x)
					 frameSize.x = (int)coll.dim.x;
				 if(coll.dim.y > frameSize.y)
					 frameSize.y = (int)coll.dim.y;
			 }
			 frameSize.print();
			 BufferedImage image = new BufferedImage((int)(frameSize.x*frameDefiners.size()),(int)frameSize.y,BufferedImage.TYPE_INT_ARGB);
			 Graphics2D graphics = image.createGraphics();
			 graphics.setPaint(new Color(bgColor));
			 graphics.fillRect(0,0,image.getWidth(),image.getHeight());
			 Vector2 currentPos = new Vector2(0,0);//destination
			 for(int x=0;x<frameDefiners.size();x++)
			 {
				 Vector2 startPos = new Vector2(0,0);//source
				 ColliderBox coll = (ColliderBox)frameDefiners.get(x).getCollider();
				 switch(horizontal)
				 {
				 case MIDDLE:
					 startPos.x += frameSize.x/2-coll.dim.x/2;
					 break;
				 case RIGHT:
					 startPos.x += frameSize.x-coll.dim.x;
					 break;
				default:
					break;
				 }
				 
				 switch(vertical)
				 {
				 case MIDDLE:
					 startPos.y += frameSize.y/2-coll.dim.y/2;
					 break;
				 case BOTTOM:
					 startPos.y += frameSize.y-coll.dim.y;
					 break;
				default:
					break;
				 }
				 
				 for(int i=(int)startPos.x;i<frameSize.x;i++)
				 {
					 for(int j=(int)startPos.y;j<frameSize.y;j++)
					 {
						 int rgb = source.getRGB((int)(i-startPos.x+coll.pos.x),(int)(j-startPos.y+coll.pos.y));
						 image.setRGB((int)(currentPos.x+i), (int)(currentPos.y+j), rgb);
					 }
				 }
				 currentPos.x += frameSize.x;
			 }
			 
			 try
			 {
				 File saveFile = new File(Remote2D.getJarPath()+savePath.text);
				 saveFile.getParentFile().mkdirs();
				 saveFile.createNewFile();
				 ImageIO.write(image, "png", saveFile);
				// Remote2D.guiList.pop();
			 } catch(Exception e)
			 {
				 throw new Remote2DException(e,"Error Saving Optimized Sprite Sheet!");
			 }
		 } else if(button.id == 9)
		 {
			 Remote2D.guiList.pop();
		 }
	}
	
	@Override
	public StretchType getOverrideStretchType()
	{
		return StretchType.NONE;
	}
	
	enum HorizontalPositioning
	{
		LEFT, MIDDLE, RIGHT;
	}
	
	enum VerticalPositioning
	{
		TOP, MIDDLE, BOTTOM;
	}
	
}

package com.remote.remote2d.engine.art;

import java.awt.Color;

import com.remote.remote2d.engine.logic.Vector2;

/**
 * Holder class to contain all pertinent color, texture, and animation data needed
 * to render an Entity.
 * 
 * @author Flafla2
 */
public class Material {
	
	/**
	 * Color of the Material, in hex.  If RenderType is not set to SOLID, the texture
	 * is tinted in this color.
	 */
	private int color;
	/**
	 * Alpha (how clear it is) of the Material, from 0.0(invisible) - 1.0(opaque)
	 */
	private float alpha;
	/**
	 * Texture of this material.  Irrelevant if RenderType is not set to TEX.
	 */
	private Texture tex;
	/**
	 * Animation of this Material.  Irrelevant if RenderType is not set to ANIM.
	 */
	private Animation anim;
	/**
	 * Render Type of this material.  If it is set to TEX, {@link #tex} will render.
	 * If it is set to ANIM, {@link #anim} will render.  If it is set to SOLID,
	 * no texture or animation will render.
	 */
	private RenderType renderType;
	
	public Material(RenderType renderType, Texture tex, Animation anim, int color, float alpha)
	{
		this.renderType = renderType;
		this.tex = tex;
		this.anim = anim;
		this.color = color;
		this.alpha = alpha;
	}
	
	public Material(Texture tex, int color, float alpha)
	{
		this(RenderType.TEX,tex,null,color,alpha);
	}
	
	public Material(Texture tex)
	{
		this(tex,0xffffff,1);
	}
	
	public Material(Animation anim, int color, float alpha)
	{
		this(RenderType.ANIM,null,anim,color,alpha);
	}
	
	public Material(Animation anim)
	{
		this(anim,0xffffff,1);
	}
	
	public Material(int color, float alpha)
	{
		this(RenderType.SOLID,null,null,color,alpha);
	}
	
	public void setColor(Color color)
	{
		this.color = color.getRGB();
	}
	
	public void setColor(int color)
	{
		this.color = color;
	}
	
	public int getColor()
	{
		return color;
	}

	public Texture getTexture() {
		return tex;
	}
	
	public void setTexture(Texture tex)
	{
		this.tex.removeTexture();
		this.tex = tex;
	}
	
	public void setTexture(String path)
	{
		setTexture(ResourceLoader.getTexture(path));
	}
	
	public void setAnimation(Animation anim)
	{
		this.anim = anim;
	}
	
	public void setAnimation(String path)
	{
		setAnimation(new Animation(path));
	}
	
	public Animation getAnimation()
	{
		return anim;
	}
	
	public void setRenderType(RenderType type)
	{
		this.renderType = type;
	}
	
	public RenderType getRenderType()
	{
		return renderType;
	}
	
	public static byte renderTypeToByte(RenderType renderType)
	{
		switch(renderType)
		{
		case SOLID:
			return 0;
		case TEX:
			return 1;
		case ANIM:
			return 2;
		}
		return -1;
	}
	
	public static RenderType byteToRenderType(byte b)
	{
		switch(b)
		{
		case 0:
			return RenderType.SOLID;
		case 1:
			return RenderType.TEX;
		case 2:
			return RenderType.ANIM;
		}
		return null;
	}
	
	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}
	
	public float getAlpha()
	{
		return alpha;
	}
	
	/**
	 * Renders this material based on the dimensions given and its RenderType.
	 * @param pos Top left corner of rendered rectangle.
	 * @param dim Dimensions of rendered rectangle.
	 */
	public void render(Vector2 pos, Vector2 dim)
	{
		switch(renderType)
		{
		case SOLID:
			Renderer.drawRect(pos, dim, color, alpha);
			break;
		case TEX:
			if(tex != null)
				Renderer.drawRect(pos, dim, tex, color, alpha);
			break;
		case ANIM:
			if(anim != null)
				anim.render(pos, dim, color, alpha);
			break;
		}
	}
	
	/**
	 * Dictates how this material will render.
	 * 
	 * @author Flafla2
	 * @see Material#setRenderType(RenderType)
	 * @see Material#getRenderType()
	 */
	public static enum RenderType
	{
		SOLID, TEX, ANIM
	}

}

package com.remote.remote2d.engine.particles;

import java.awt.Color;

import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.logic.Interpolator;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Map;

public class Particle {
	
	private Vector2 pos;
	private int dim;
	private Color color;
	private float alpha;
	private long startTime;
	
	public Vector2 velocity;
	public Vector2 environment;
	public Color startColor;
	public Color endColor;
	public int startSize;
	public int endSize;
	public float startAlpha;
	public float endAlpha;
	public long life;
	public long lifeLength;
	
	public Particle(Vector2 startPos, Vector2 velocity, Vector2 environment, Color startColor, Color endColor, int startSize, int endSize, float startAlpha, float endAlpha, long lifeLength)
	{
		this.pos = startPos.copy();
		this.velocity = velocity.copy();
		this.environment = environment.copy();
		this.startColor = new Color(startColor.getRGB());
		this.endColor = new Color(endColor.getRGB());
		this.startSize = startSize;
		this.endSize = endSize;
		this.startAlpha = startAlpha;
		this.endAlpha = endAlpha;
		this.life = 0;
		this.lifeLength = lifeLength;
		
		startTime = System.currentTimeMillis();
	}
	
	public boolean tick(Map map)
	{
		life = System.currentTimeMillis()-startTime;
		
		if(life >= lifeLength)
			return false;
			
		
		velocity = velocity.add(environment);
		Vector2 correction = new Vector2(0,0);
		if(map != null)
			correction = map.getCorrection(pos.add(velocity).subtract(new Vector2(dim/2,dim/2)).getColliderWithDim(new Vector2(dim,dim)));
		pos = pos.add(velocity.add(correction));
		
		if(!correction.equals(new Vector2(0,0)))
		{
			velocity = velocity.add(velocity.multiply(correction.normalize()).multiply(new Vector2(2,2)));
		}
		
		float percentage = ((float)life)/((float)lifeLength);
		float red = (float) Interpolator.linearInterpolate(startColor.getRed(), endColor.getRed(), percentage)/255f;
		float green = (float) Interpolator.linearInterpolate(startColor.getGreen(), endColor.getGreen(), percentage)/255f;
		float blue = (float) Interpolator.linearInterpolate(startColor.getBlue(), endColor.getBlue(), percentage)/255f;
		color = new Color(red,green,blue);
		
		dim = (int) Interpolator.linearInterpolate(startSize, endSize, percentage);
		alpha = (float) Interpolator.linearInterpolate(startAlpha, endAlpha, percentage);
		
		return true;
	}
	
	public void render()
	{
		Renderer.drawRect(new Vector2(pos.x-dim/2,pos.y-dim/2), new Vector2(dim), this.color.getRGB(), alpha);
	}
	
}

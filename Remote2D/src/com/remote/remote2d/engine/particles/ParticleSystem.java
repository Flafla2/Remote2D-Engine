package com.remote.remote2d.engine.particles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.entity.EditorObject;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Map;

public class ParticleSystem extends EditorObject {
	
	public Vector2 pos;
	
	public ArrayList<Particle> particles;
	public int maxParticles = 10000;
	public int maxSpawnRate = 100;
	public int spawnRateDeviation = 0;
	public int systemLength = -1;
	
	public Vector2 startPosDeviation = new Vector2(0,0);
	
	public Color startColor = new Color(0xff9900);
	public Color endColor = new Color(0xff0000);
	public int colorDeviation = 0;
	
	public Vector2 baseVelocity = new Vector2(0,0);
	public Vector2 velocityDeviation = new Vector2(10,10);
	public Vector2 environment = new Vector2(0,1);
	
	public float startAlpha = 1.0f;
	public float endAlpha = 0.5f;
	public float startAlphaDeviation = 0;
	public float endAlphaDeviation = 0;
	
	public int startSize = 20;
	public int endSize = 10;
	public int startSizeDeviation = 0;
	public int endSizeDeviation = 0;
	
	public long particleLife = 1000;
	public long particleLifeDeviation = 500l;
	
	private long startTime;
	private Random random;
	
	public ParticleSystem(Map map)
	{
		super(map,null);
		particles = new ArrayList<Particle>();
		random = new Random();
		startTime = System.currentTimeMillis();
	}
	
	public boolean spawnParticle()
	{
		if(particles.size() >= maxParticles)
			return false;
		
		Vector2 randomVec = new Vector2((random.nextFloat()-0.5f)*2f,(random.nextFloat()-0.5f)*2f);
		
		Vector2 startPos = pos.add(startPosDeviation.multiply(randomVec));
		randomVec = new Vector2((random.nextFloat()-0.5f)*2f,(random.nextFloat()-0.5f)*2f);
		Vector2 velocity = baseVelocity.add(velocityDeviation.multiply(randomVec));
		
		float red = (startColor.getRed()+getDeviation(colorDeviation))/255f;
		float green = (startColor.getGreen()+getDeviation(colorDeviation))/255f;
		float blue = (startColor.getBlue()+getDeviation(colorDeviation))/255f;
		
		red = Math.max(Math.min(red, 1.0f),0);
		green = Math.max(Math.min(green, 1.0f),0);
		blue = Math.max(Math.min(blue, 1.0f),0);
		
		Color trueStartColor = new Color(red,green,blue);
		
		red = (endColor.getRed()+getDeviation(colorDeviation))/255f;
		green = (endColor.getGreen()+getDeviation(colorDeviation))/255f;
		blue = (endColor.getBlue()+getDeviation(colorDeviation))/255f;
		
		red = Math.max(Math.min(red, 1.0f),0);
		green = Math.max(Math.min(green, 1.0f),0);
		blue = Math.max(Math.min(blue, 1.0f),0);
		
		Color trueEndColor = new Color(red,green,blue);
		
		int trueStartSize = (int) (startSize+getDeviation(startSizeDeviation));
		int trueEndSize = (int) (endSize+getDeviation(endSizeDeviation));
		float trueStartAlpha = startAlpha+getDeviation(startAlphaDeviation);
		float trueEndAlpha = endAlpha+getDeviation(endAlphaDeviation);
		long lifeLength = (long)(particleLife+getDeviation(particleLifeDeviation));
		
		Particle particle = new Particle(startPos,velocity,environment,trueStartColor,trueEndColor,trueStartSize,trueEndSize,trueStartAlpha,trueEndAlpha,lifeLength);
		particles.add(particle);
		return true;
	}
	
	private float getDeviation(float deviation)
	{
		return deviation*((random.nextFloat()-0.5f)*2f);
	}
	
	public void tick(boolean preview)
	{
		if(System.currentTimeMillis()-startTime < systemLength || systemLength == -1)
		{
			for(int x=0;x<maxSpawnRate;x++)
				spawnParticle();
		}
		
		for(int x=0;x<particles.size();x++)
		{
			boolean despawn = !particles.get(x).tick(map);
			if(despawn)
			{
				particles.remove(x);
				x--;
			}
		}
	}
	
	public void render()
	{
		for(int x=0;x<particles.size();x++)
			particles.get(x).render();
	}

	@Override
	public void apply() {
		
	}

	public static String getExtension() {
		return ".particle";
	}


}

package com.remote.remote2d.engine.logic;

public class Collision {
	
	public double lengthSquared = -1.0d;
	public Vector2 correction = new Vector2(0,0);
	public Vector2 movementVector = new Vector2(0,0);
	public boolean collides = false;
	
	public int min = -1;
	public int max = -1;
	
	public Collider idle;
	public Collider moving;
	
}

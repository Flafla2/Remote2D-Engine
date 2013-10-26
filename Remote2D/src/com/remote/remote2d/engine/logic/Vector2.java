package com.remote.remote2d.engine.logic;

import com.esotericsoftware.minlog.Log;

public class Vector2 extends Vector1 {
	
	public float y;
	
	public Vector2(float x, float y)
	{
		super(x);
		this.y = y;
	}
	
	public Vector2(Vector2 vec)
	{
		super(vec);
		this.y = vec.y;
	}
	
	public Vector2(float[] x)
	{
		this(x[0],x[1]);
	}
	
	public Vector2(int[] x) {
		this(x[0],x[1]);
	}

	public Vector2(float x) {
		this(x,x);
	}
	
	@Override
	public Vector2 abs()
	{
		return new Vector2(Math.abs(x),Math.abs(y));
	}
	
	public Vector2 add(Vector2 vec)
	{
		return new Vector2(x+vec.x,y+vec.y);
	}
	
	public Vector2 subtract(Vector2 vec)
	{
		return new Vector2(x-vec.x,y-vec.y);
	}
	
	public Vector2 multiply(Vector2 vec)
	{
		return new Vector2(x*vec.x,y*vec.y);
	}
	
	public Vector2 divide(Vector2 vec)
	{
		return new Vector2(x/vec.x,y/vec.y);
	}
	
	public float dot(Vector2 vec)
	{
		return x*vec.x+y*vec.y;
	}
	
	@Override
	public float[] getElements()
	{
		return new float[]{x,y};
	}
	
	@Override
	public Vector2 copy()
	{
		return new Vector2(x,y);
	}
	
	@Override
	public Vector2 print()
	{
		Log.info(toString());
		return this;
	}
	
	@Override
	public String toString()
	{
		return "("+x+","+y+")";
	}
	
	public ColliderBox getColliderWithDim(Vector2 dim)
	{
		return new ColliderBox(new Vector2(x,y), new Vector2(dim.x,dim.y));
	}
	
	public Vector2 normalize() {
		float distance = ColliderLogic.getDistBetweenPoints(0, 0, x, y);
		if(distance == 0)
			return new Vector2(0,0);
		return new Vector2(x/distance,y/distance);
	}
	
	public Vector2 perp()
	{
		return new Vector2(-y,x);
	}

	public Vector2 mod(float mod) {
		return new Vector2(x%mod,y%mod);
	}
	
	public Vector2 mod(Vector2 mod) {
		return new Vector2(x%mod.x,y%mod.y);
	}
	
}

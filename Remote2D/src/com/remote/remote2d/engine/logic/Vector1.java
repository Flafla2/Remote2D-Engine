package com.remote.remote2d.engine.logic;

import com.esotericsoftware.minlog.Log;

public class Vector1 {
	
	public float x = 0;
	
	public Vector1(float x)
	{
		this.x = x;
	}
	
	public Vector1(Vector1 vec)
	{
		x = vec.x;
	}
	
	public Vector1 abs()
	{
		return new Vector1(Math.abs(x));
	}
	
	public Vector1 add(Vector1 vec)
	{
		return new Vector1(x+vec.x);
	}
	
	public Vector1 subtract(Vector1 vec)
	{
		return new Vector1(x-vec.x);
	}
	
	public Vector1 multiply(Vector1 vec)
	{
		return new Vector1(x*vec.x);
	}
	
	public Vector1 divide(Vector1 vec)
	{
		return new Vector1(x/vec.x);
	}
	
	public float dot(Vector1 vec)
	{
		return x*vec.x;
	}
	
	public float[] getElements()
	{
		return new float[]{x};
	}
	
	public Vector1 copy()
	{
		return new Vector1(x);
	}
	
	public Vector1 print()
	{
		Log.debug(toString());
		return this;
	}
	
	@Override
	public String toString()
	{
		return "("+x+")";
	}

}

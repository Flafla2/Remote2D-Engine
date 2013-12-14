package com.remote.remote2d.engine.logic;

public class Vector3 extends Vector2 {
	
	public float z;
	
	public Vector3(float x, float y, float z)
	{
		super(x,y);
		this.z = z;
	}
	
	public Vector3(Vector3 vec)
	{
		super(vec);
		this.z = vec.z;
	}
	
	public Vector3 add(Vector3 vec)
	{
		return new Vector3(x+vec.x,y+vec.y,z+vec.z);
	}
	
	public Vector3 subtract(Vector3 vec)
	{
		return new Vector3(x-vec.x,y-vec.y,z-vec.z);
	}
	
	public Vector3 multiply(Vector3 vec)
	{
		return new Vector3(x*vec.x,y*vec.y,z*vec.z);
	}
	
	public Vector3 divide(Vector3 vec)
	{
		return new Vector3(x/vec.x,y/vec.y,z/vec.z);
	}
	
	public float dot(Vector3 vec)
	{
		return x*vec.x+y*vec.y+z*vec.z;
	}
	
	@Override
	public float[] getElements()
	{
		return new float[]{x,y,z};
	}
	
	@Override
	public String toString()
	{
		return "("+x+","+y+","+z+")";
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Vector3))
			return false;
		Vector3 vec = (Vector3)o;
		return vec.x == this.x && vec.y == this.y && vec.z == this.z;
	}
}

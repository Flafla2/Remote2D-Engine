package com.remote.remote2d.engine.logic;

public class Interpolator {
	
	public static double linearInterpolate(double y1, double y2, double mu)
	{
		return(y1*(1-mu)+y2*mu);
	}
	
	private static float[] linearInterpolate(float[] y1, float[] y2, double mu)
	{
		float[] interp = new float[Math.max(y1.length, y2.length)];
		int max = Math.min(y1.length, y2.length);
		for(int x=0;x<max;x++)
			interp[x] = (float)linearInterpolate(y1[x],y2[x],mu);
		
		for(int x=max;x<interp.length;x++)
			interp[x] = y1.length > y2.length ? y1[x] : y2[x];
		return interp;
	}
	
	public static Vector1 linearInterpolate(Vector1 y1, Vector1 y2, double mu)
	{
		return new Vector1((float)linearInterpolate(y1.x,y2.x,mu));
	}
	
	public static Vector2 linearInterpolate(Vector2 y1, Vector2 y2, double mu)
	{
		return new Vector2((float)linearInterpolate(y1.x,y2.x,mu),(float)linearInterpolate(y1.y,y2.y,mu));
	}
	
	public static Vector3 linearInterpolate(Vector3 y1, Vector3 y2, double mu)
	{
		return new Vector3((float)linearInterpolate(y1.x,y2.x,mu),(float)linearInterpolate(y1.y,y2.y,mu),(float)linearInterpolate(y1.z,y2.z,mu));
	}
	
	public static double cosineInterpolate(double y1, double y2, double mu)
	{
		double mu2 = (1-Math.cos(mu*3.1415926535d))/2;
		return(y1*(1-mu2)+y2*mu2);
	}
	
	public static double cubicInterpolate(double y0, double y1, double y2, double y3, double mu)
	{
		double a0,a1,a2,a3,mu2;
		
		mu2 = mu*mu;
		a0 = y3 - y2 - y0 + y1;
		a1 = y0 - y1 - a0;
		a2 = y2 - y0;
		a3 = y1;
		return(a0*mu*mu2+a1*mu2+a2*mu+a3);
	}
}

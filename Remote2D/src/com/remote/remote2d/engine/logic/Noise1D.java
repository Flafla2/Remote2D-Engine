package com.remote.remote2d.engine.logic;

import java.util.Random;

import com.esotericsoftware.minlog.Log;

public class Noise1D{
	
	public static float[] GeneratePerlinNoise(float[] baseNoise, int octaveCount)
	{
	   int width = baseNoise.length;
	 
	   float[][] smoothNoise = new float[octaveCount][]; //an array of arrays containing
	 
	   float persistance = 0.5f;
	 
	   //generate smooth noise
	   for (int i = 0; i < octaveCount; i++)
	   {
	       smoothNoise[i] = GenerateSmoothNoise(baseNoise, i);
	   }
	 
	    float[] perlinNoise = GetEmptyArray(width);
	    float amplitude = 1.0f;
	    float totalAmplitude = 0.0f;
	 
	    //blend noise together
	    for (int octave = octaveCount - 1; octave >= 0; octave--)
	    {
	       amplitude *= persistance;
	       totalAmplitude += amplitude;
	 
	       for (int i = 0; i < width; i++)
	       {
	    	   perlinNoise[i] += smoothNoise[octave][i] * amplitude;
	       }
	    }
	 
	   //normalization
	   for (int i = 0; i < width; i++)
	   {
	       perlinNoise[i] /= totalAmplitude;
	   }
	 
	   return perlinNoise;
	}
	
	public static float Interpolate(float x0, float x1, float alpha)
	{
	   return x0 * (1 - alpha) + alpha * x1;
	}
	
	public static float[] GenerateSmoothNoise(float[] baseNoise, int octave)
	{
		int width = baseNoise.length;
		float[] smoothNoise = GetEmptyArray(width);
		int samplePeriod = 1 << octave; // calculates 2 ^ k
		float sampleFrequency = 1.0f / samplePeriod;
		for (int i = 0; i < width; i++)
		{
			//calculate the horizontal sampling indices
			int sample_i0 = (i / samplePeriod) * samplePeriod;
			int sample_i1 = (sample_i0 + samplePeriod) % width; //wrap around
			float horizontal_blend = (i - sample_i0) * sampleFrequency;
			
			smoothNoise[i] = Interpolate(baseNoise[sample_i0],baseNoise[sample_i1],horizontal_blend);
		}
		return smoothNoise;
	}
	
	public static float[] GenerateWhiteNoise(int width, int seed)
	{
		Random random = new Random(seed);
	    float[] noise = GetEmptyArray(width);
	 
	    for (int i = 0; i < width; i++)
	    {
            noise[i] = (float)random.nextDouble() % 1;
	    }
	 
	    return noise;
	}
	
	public static float[] GetEmptyArray(int x)
	{
		return new float[x];
	}
}

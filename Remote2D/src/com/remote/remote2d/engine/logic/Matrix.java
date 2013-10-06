package com.remote.remote2d.engine.logic;

public class Matrix {
	
	float[][] matrix;
	
	public Matrix()
	{
		matrix = getIdentityMatrix();
	}
	
	public Matrix(float[] array) {
		if(array.length == 9)
		{
			float[][] arr = new float[3][3];
			for(int x=0;x<3;x++)
			{
				for(int y=0;y<3;y++)
				{
					arr[x][y] = array[x*3+y];
				}
			}
			matrix = arr;
		} else if(array.length == 16)
		{
			float[] repArr = new float[9];
			int index = 0;
			int thisIndex = 0;
			for(int x=0;x<4;x++)
			{
				for(int y=0;y<4;y++)
				{
					if(x != 3 && y != 3)
					{
						repArr[thisIndex] = array[index];
						thisIndex++;
					}
					index++;
				}
			}
			
			float[][] arr = new float[3][3];
			for(int x=0;x<3;x++)
			{
				for(int y=0;y<3;y++)
				{
					arr[x][y] = repArr[x*3+y];
				}
			}
			matrix = arr;
		} else
		{
			matrix = getIdentityMatrix();
		}
	}

	public void multiply(float[][] m)
	{
		matrix = matrixMultiply(matrix,m);
	}
	
	public static float[][] matrixMultiply(float[][] mat1, float[][] mat2) {
		float[][] matrix = new float[3][3];
		
		
		for(int i = 0; i < 3; i++)//row
		{
			for(int j = 0;j<3;j++)//col
			{
				matrix[i][j] = 0;
				for(int k=0;k<3;k++)//index
				{
					matrix[i][j] += mat1[k][j]*mat2[i][k];
				}
			}
		}

		return matrix;
	}
	
	public static Vector3 vertexMultiply(float[][] matrix, Vector3 vert) {
	  	Vector3 v = new Vector3(0,0,1);

	  	v.x = vert.x * matrix[0][0] +
			  vert.y * matrix[1][0] +
			  vert.z * matrix[2][0];

	  	v.y = vert.x * matrix[0][1] +
	  		  vert.y * matrix[1][1] +
			  vert.z * matrix[2][1];
	  	
	  	v.z = vert.x * matrix[0][2] +
			  vert.y * matrix[1][2] +
			  vert.z * matrix[2][2];

	  	return v;
	}
	
	public static float[][] getIdentityMatrix()
	{
		float[][] matrix = {{ 1, 0, 0 },
							{ 0, 1, 0 },
							{ 0, 0, 1 }};
		return matrix;
	}
	
	public static float[][] getTranslationMatrix(Vector2 t)
	{
		float[][] matrix = {{ 1, 0, t.x },
							{ 0, 1, t.y },
							{ 0, 0, 1   }};
		return matrix;
	}
	
	public static float[][] getScaleMatrix(Vector2 s)
	{
		float[][] matrix = {{ s.x, 0,   0 },
							{ 0,   s.y, 0 },
							{ 0,   0,   1 }};
		return matrix;
	}
	
	public static float[][] getRotMatrix(float rot)
	{
		float[][] matrix = {{ (float) Math.cos(rot), (float) -Math.sin(rot), 0 },
							{ (float) Math.sin(rot), (float) Math.cos(rot), 0 },
							{ 0, 0, 1 }};
		return matrix;
	}
	
}

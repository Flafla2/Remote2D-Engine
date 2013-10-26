package com.remote.remote2d.engine.logic;

import com.remote.remote2d.engine.art.Renderer;

public class ColliderMatrixBox extends Collider {
	
	public Vector2 pos;
	public Vector2 dim;
	public Vector2 vec;
	
	private float angle = 0;
	private Vector2 scale = new Vector2(1,1);
	//private Vector2 translate = new Vector2(0,0);//used to rotate/scale around something that isn't the origin
	private Matrix matrix;
	private Matrix inverse;
	
	public ColliderMatrixBox(Vector2 pos, Vector2 dim)
	{
		this.pos = pos;
		this.dim = dim;
		
		matrix = calculateMatrix();
		inverse = calculateInverseMatrix();
		updateVerts();
	}

	@Override
	public boolean isPointInside(Vector2 vec) {
		this.vec = new Vector2(Matrix.vertexMultiply(inverse.matrix, new Vector3(vec.x-pos.x,vec.y-pos.y,1))).add(pos);
		if(pos.getColliderWithDim(dim).isPointInside(this.vec))
			return true;
		
		return false;
	}
	
	public Matrix calculateMatrix()
	{
		Matrix matrix = new Matrix();
		angle %= 360;
		
		//Convert to local rot/scale
		//matrix.multiply(Matrix.getTranslationMatrix(new Vector2DF(-pos.x,-pos.y)));
		//Rotate based on given angle
		matrix.multiply(Matrix.getRotMatrix(angle));
		//Scale based on given Vector
		matrix.multiply(Matrix.getScaleMatrix(scale));
		//Convert to global coordinates
		//matrix.multiply(Matrix.getTranslationMatrix(pos));
		
		return matrix;
	}
	
	public Matrix calculateInverseMatrix()
	{
		angle %= 360;
		
		Matrix matrix = new Matrix();
		
		//Convert to local rot/scale
		//matrix.multiply(Matrix.getTranslationMatrix(new Vector2DF(-pos.x,-pos.y)));
		//Rotate based on given angle
		matrix.multiply(Matrix.getRotMatrix(-angle));
		//Scale based on given Vector
		matrix.multiply(Matrix.getScaleMatrix(new Vector2(1f/scale.x,1f/scale.y)));
		//Convert to global coordinates
		//matrix.multiply(Matrix.getTranslationMatrix(pos));
		return matrix;
	}
	
	public void printMatrix(Matrix m)
	{
		System.out.println();
		for(int x=0;x<m.matrix.length;x++)
		{
			for(int y=0;y<m.matrix[x].length;y++)
			{
				System.out.print(m.matrix[x][y]+" ");
			}
			System.out.println();
		}
	}
	
	public Vector2[] getPoints()
	{
		
		Vector2[] r = new Vector2[4];
		r[0] = new Vector2(Matrix.vertexMultiply(matrix.matrix,new Vector3(0,0,1)));
		r[1] = new Vector2(Matrix.vertexMultiply(matrix.matrix,new Vector3(dim.x,0,1)));
		r[2] = new Vector2(Matrix.vertexMultiply(matrix.matrix,new Vector3(dim.x,dim.y,1)));
		r[3] = new Vector2(Matrix.vertexMultiply(matrix.matrix,new Vector3(0,dim.y,1)));
		
		
		return r;
	}
	
	@Override
	public void drawCollider(int color) {
		Renderer.translate(new Vector2(pos.x,pos.y));
		Renderer.drawLinePoly(getPoints(), 0xffffff, 1.0f);
		Renderer.translate(new Vector2(-pos.x,-pos.y));
		
		Renderer.drawLineRect(pos, dim, 0x00ff00, 1.0f);
		
		if(vec != null)
			Renderer.drawRect(new Vector2(vec.x-3,vec.y-3), new Vector2(6), 0x000000, 1.0f);
	}
	
	public float getAngle() {
		return angle;
	}

	public ColliderMatrixBox setAngle(float angle) {
		this.angle = angle;
		matrix = calculateMatrix();
		inverse = calculateInverseMatrix();
		return this;
	}

	public Vector2 getScale() {
		return scale;
	}

	public ColliderMatrixBox setScale(Vector2 scale) {
		this.scale = scale;
		matrix = calculateMatrix();
		inverse = calculateInverseMatrix();
		return this;
	}

	@Override
	public Collider getTransformedCollider(Vector2 trans) {
		ColliderMatrixBox box = new ColliderMatrixBox(pos.add(new Vector2(trans.getElements())),dim);
		box.setAngle(angle);
		box.setScale(scale);
		return box;
	}

	@Override
	public void updateVerts() {
		Vector2[] ver = getPoints();
		verts = new Vector2[4];
		verts[0] = ver[0];
		verts[1] = ver[1];
		verts[2] = ver[2];
		verts[3] = ver[3];
	}

}

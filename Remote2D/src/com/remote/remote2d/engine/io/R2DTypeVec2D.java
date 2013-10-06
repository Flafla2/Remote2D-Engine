package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.remote.remote2d.engine.logic.Vector2;

public class R2DTypeVec2D extends R2DType {
	
	public Vector2 data;

	public R2DTypeVec2D(String id) {
		super(id);
		this.data = new Vector2(0,0);
	}
	
	public R2DTypeVec2D(String id, Vector2 data) {
		this(id);
		this.data = data;
	}

	@Override
	public void read(DataInput d) throws IOException {
		data.x = d.readFloat();
		data.y = d.readFloat();
	}

	@Override
	public void write(DataOutput d) throws IOException {
		d.writeFloat(data.x);
		d.writeFloat(data.y);
	}

	@Override
	public byte getId() {
		return 11;
	}
	
	@Override
	public String toString()
	{
		return data.toString();
	}

}

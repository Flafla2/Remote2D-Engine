package com.remote.remote2d.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class R2DTypeInt extends R2DType {
	
	public int data;

	public R2DTypeInt(String id) {
		super(id);
	}
	
	public R2DTypeInt(String id, int data) {
		this(id);
		this.data = data;
	}

	@Override
	public void read(DataInput d) throws IOException {
		data = d.readInt();
	}

	@Override
	public void write(DataOutput d) throws IOException {
		d.writeInt(data);
	}

	@Override
	public byte getId() {
		return 3;
	}
	
	@Override
	public String toString()
	{
		return data+"";
	}

}

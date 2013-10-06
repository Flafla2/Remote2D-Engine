package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class R2DTypeShort extends R2DType {
	
	public short data;

	public R2DTypeShort(String id) {
		super(id);
	}
	
	public R2DTypeShort(String id, short data) {
		this(id);
		this.data = data;
	}

	@Override
	public void read(DataInput d) throws IOException {
		data = d.readShort();
	}

	@Override
	public void write(DataOutput d) throws IOException {
		d.writeShort(data);
	}

	@Override
	public byte getId() {
		return 2;
	}
	
	@Override
	public String toString()
	{
		return data+"";
	}

}

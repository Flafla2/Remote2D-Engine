package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class R2DTypeLong extends R2DType {
	
	public long data;

	public R2DTypeLong(String id) {
		super(id);
	}
	
	public R2DTypeLong(String id, long data) {
		this(id);
		this.data = data;
	}

	@Override
	public void read(DataInput d) throws IOException {
		data = d.readLong();
	}

	@Override
	public void write(DataOutput d) throws IOException {
		d.writeLong(data);
	}

	@Override
	public byte getId() {
		return 4;
	}
	
	@Override
	public String toString()
	{
		return data+"";
	}

}

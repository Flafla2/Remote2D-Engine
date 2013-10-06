package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class R2DTypeByte extends R2DType {
	
	public byte data;
	
	public R2DTypeByte(String id)
	{
		super(id);
	}
	
	public R2DTypeByte(String id, byte data)
	{
		this(id);
		this.data = data;
	}

	@Override
	public void read(DataInput d) throws IOException {
		data = d.readByte();
	}

	@Override
	public void write(DataOutput d) throws IOException {
		d.writeByte((int)data);
	}

	@Override
	public byte getId() {
		return 1;
	}
	
	@Override
	public String toString()
	{
		return data+"";
	}

}

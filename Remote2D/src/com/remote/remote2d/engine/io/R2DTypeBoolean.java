package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class R2DTypeBoolean extends R2DType {
	
	public boolean data;
	
	public R2DTypeBoolean(String id)
	{
		super(id);
	}
	
	public R2DTypeBoolean(String id, boolean data)
	{
		this(id);
		this.data = data;
	}

	@Override
	public void read(DataInput d) throws IOException {
		data = d.readBoolean();
	}

	@Override
	public void write(DataOutput d) throws IOException {
		d.writeBoolean(data);
	}

	@Override
	public byte getId() {
		return 9;
	}
	
	@Override
	public String toString()
	{
		return data+"";
	}

}

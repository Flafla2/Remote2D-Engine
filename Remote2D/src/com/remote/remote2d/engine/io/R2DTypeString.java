package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class R2DTypeString extends R2DType {
	
	public String data;

	public R2DTypeString(String id) {
		super(id);
	}
	
	public R2DTypeString(String id, String data) {
		this(id);
		this.data = data;
	}

	@Override
	public void read(DataInput d) throws IOException {
		data = d.readUTF();
	}

	@Override
	public void write(DataOutput d) throws IOException {
		d.writeUTF(data);
	}

	@Override
	public byte getId() {
		return 8;
	}
	
	@Override
	public String toString()
	{
		return data;
	}

}

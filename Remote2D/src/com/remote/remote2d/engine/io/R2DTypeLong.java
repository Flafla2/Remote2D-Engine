package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import nu.xom.Element;

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
	public void read(Element e) {
		data = Long.parseLong(e.getValue());
	}

	@Override
	public void write(Element e) {
		e.appendChild(Long.toString(data));
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
	
	@Override
	public boolean equals(R2DType type)
	{
		if(!type.name.equals(name) || !(type instanceof R2DTypeLong))
			return false;
		return ((R2DTypeLong)type).data == data;
	}

}

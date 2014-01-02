package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import nu.xom.Element;

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
		d.writeByte(data);
	}
	
	@Override
	public void read(Element e) {
		data = Byte.parseByte(e.getValue());
	}

	@Override
	public void write(Element e) {
		e.appendChild(Byte.toString(data));
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
	
	@Override
	public boolean equals(R2DType type)
	{
		if(!type.name.equals(name) || !(type instanceof R2DTypeByte))
			return false;
		return ((R2DTypeByte)type).data == data;
	}
	
	@Override
	public R2DTypeByte clone()
	{
		return new R2DTypeByte(name,data);
	}

}

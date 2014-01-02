package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import nu.xom.Element;

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
	public void read(Element e) {
		data = Short.parseShort(e.getValue());
	}

	@Override
	public void write(Element e) {
		e.appendChild(Short.toString(data));
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
	
	@Override
	public boolean equals(R2DType type)
	{
		if(!type.name.equals(name) || !(type instanceof R2DTypeShort))
			return false;
		return ((R2DTypeShort)type).data == data;
	}
	
	@Override
	public R2DTypeShort clone()
	{
		return new R2DTypeShort(name,data);
	}

}

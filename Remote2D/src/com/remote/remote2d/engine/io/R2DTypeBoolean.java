package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import nu.xom.Element;

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
	public void read(Element e) {
		data = Boolean.parseBoolean(e.getValue());
	}

	@Override
	public void write(Element e) {
		e.appendChild(Boolean.toString(data));
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
	
	@Override
	public boolean equals(R2DType type)
	{
		if(!type.name.equals(name) || !(type instanceof R2DTypeBoolean))
			return false;
		return ((R2DTypeBoolean)type).data == data;
	}

}

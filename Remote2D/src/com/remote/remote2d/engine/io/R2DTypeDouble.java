package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import nu.xom.Element;

public class R2DTypeDouble extends R2DType {
	
	public double data;

	public R2DTypeDouble(String id) {
		super(id);
	}
	
	public R2DTypeDouble(String id, double data) {
		this(id);
		this.data = data;
	}

	@Override
	public void read(DataInput d) throws IOException {
		data = d.readDouble();
	}

	@Override
	public void write(DataOutput d) throws IOException {
		d.writeDouble(data);
	}
	
	@Override
	public void read(Element e) {
		data = Double.parseDouble(e.getValue());
	}

	@Override
	public void write(Element e) {
		e.appendChild(Double.toString(data));
	}

	@Override
	public byte getId() {
		return 6;
	}
	
	@Override
	public String toString()
	{
		return data+"";
	}
	
	@Override
	public boolean equals(R2DType type)
	{
		if(!(type instanceof R2DTypeDouble))
			return false;
		return ((R2DTypeDouble)type).data == data;
	}

}

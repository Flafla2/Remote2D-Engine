package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import nu.xom.Element;

public class R2DTypeChar extends R2DType {
	
	public char data;

	public R2DTypeChar(String id) {
		super(id);
	}
	
	public R2DTypeChar(String id, char data) {
		this(id);
		this.data = data;
	}

	@Override
	public void read(DataInput d) throws IOException {
		data = d.readChar();
	}

	@Override
	public void write(DataOutput d) throws IOException {
		d.writeChar(data);
	}
	
	@Override
	public void read(Element e) {
		data = e.getValue().charAt(0);
	}

	@Override
	public void write(Element e) {
		e.appendChild(Character.toString(data));
	}

	@Override
	public byte getId() {
		return 7;
	}
	
	@Override
	public String toString()
	{
		return data+"";
	}
	
	@Override
	public boolean equals(R2DType type)
	{
		if(!type.name.equals(name) || !(type instanceof R2DTypeChar))
			return false;
		return ((R2DTypeChar)type).data == data;
	}
	
	@Override
	public R2DTypeChar clone()
	{
		return new R2DTypeChar(name,data);
	}

}

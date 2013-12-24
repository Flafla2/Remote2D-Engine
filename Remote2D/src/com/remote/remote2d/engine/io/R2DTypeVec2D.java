package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import nu.xom.Element;

import com.remote.remote2d.engine.logic.Vector2;

public class R2DTypeVec2D extends R2DType {
	
	public Vector2 data;

	public R2DTypeVec2D(String id) {
		super(id);
		this.data = new Vector2(0,0);
	}
	
	public R2DTypeVec2D(String id, Vector2 data) {
		this(id);
		this.data = data;
	}

	@Override
	public void read(DataInput d) throws IOException {
		data.x = d.readFloat();
		data.y = d.readFloat();
	}

	@Override
	public void write(DataOutput d) throws IOException {
		d.writeFloat(data.x);
		d.writeFloat(data.y);
	}
	
	@Override
	public void read(Element e) {
		data.x = Float.parseFloat(e.getChildElements("x").get(0).getValue());
		data.y = Float.parseFloat(e.getChildElements("y").get(0).getValue());
	}

	@Override
	public void write(Element e) {
		Element xElement = new Element("x");
		xElement.appendChild(data.x+"");
		Element yElement = new Element("y");
		yElement.appendChild(data.y+"");
		
		e.appendChild(xElement);
		e.appendChild(yElement);
	}

	@Override
	public byte getId() {
		return 11;
	}
	
	@Override
	public String toString()
	{
		return data.toString();
	}
	
	@Override
	public boolean equals(R2DType type)
	{	
		if(type == null)
			return false;
		if(!type.name.equals(name) || !(type instanceof R2DTypeVec2D))
			return false;
		Vector2 otherData = ((R2DTypeVec2D)type).data;
		if((data == null && otherData != null) || (otherData == null && data != null))
			return false;
		return data.equals(otherData);
	}

}

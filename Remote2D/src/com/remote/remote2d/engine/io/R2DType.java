package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Element;

public abstract class R2DType {
	
	protected String name;
	
	public R2DType(String id)
	{
		this.name = id;
	}
	
	public abstract void read(DataInput d) throws IOException;
	public abstract void write(DataOutput d) throws IOException;
	public abstract void read(Element e);
	public abstract void write(Element e);
	public abstract boolean equals(R2DType type);
	public abstract R2DType clone();
	
	public abstract byte getId();
	
	public String getName()
	{
		if(name == null)
			return "";
		return name;
	}
	
	public R2DType setName(String name)
	{
		if(name == null)
			this.name = "";
		else
			this.name = name;
		
		return this; //For convenience
	}
	
	public static R2DType readNamedType(DataInput d) throws IOException
	{
		byte startByte = d.readByte();
		
		if(startByte == 0)
			return new R2DTypeEnd();
		else
		{
			String name = d.readUTF();
			R2DType type = createType(startByte,name);
			type.read(d);
			return type;
		}
	}
	
	public static R2DType readNamedType(Element e)
	{
		byte startByte = Byte.parseByte(e.getAttributeValue("id"));
		
		if(startByte == 0)
			return new R2DTypeEnd();
		else
		{
			String name = e.getAttributeValue("name");
			R2DType type = createType(startByte,name);
			type.read(e);
			return type;
		}
	}
	
	public static void writeNamedType(R2DType type, DataOutput d) throws IOException
	{
		if(type.getId() == 0)
			return;
		d.writeByte(type.getId());
		d.writeUTF(type.getName());
		type.write(d);
	}
	
	public static void writeNamedType(R2DType type, Element e)
	{
		if(type.getId() == 0)
			return;
		e.addAttribute(new Attribute("id",type.getId()+""));
		e.addAttribute(new Attribute("name",type.name));
		type.write(e);
	}
	
	public static R2DType createType(byte id, String name)
	{
		switch(id)
		{
			case 0:
				return new R2DTypeEnd();
			case 1:
				return new R2DTypeByte(name);
			case 2:
				return new R2DTypeShort(name);
			case 3:
				return new R2DTypeInt(name);
			case 4:
				return new R2DTypeLong(name);
			case 5:
				return new R2DTypeFloat(name);
			case 6:
				return new R2DTypeDouble(name);
			case 7:
				return new R2DTypeChar(name);
			case 8:
				return new R2DTypeString(name);
			case 9:
				return new R2DTypeBoolean(name);
			case 10:
				return new R2DTypeCollection(name);
			case 11:
				return new R2DTypeVec2D(name);
//			case 12:
//				return new R2DTypeTexture(name);
//			case 13:
//				return new R2DTypeAnimation(name);
//			case 14:
//				return new R2DTypeColor(name);
			default:
				return null;
		}
	}
	
	public static String getTypeName(byte id)
	{
		switch(id)
		{
			case 0:
				return "TYPE_END";
			case 1:
				return "TYPE_BYTE";
			case 2:
				return "TYPE_SHORT";
			case 3:
				return "TYPE_INT";
			case 4:
				return "TYPE_LONG";
			case 5:
				return "TYPE_FLOAT";
			case 6:
				return "TYPE_DOUBLE";
			case 7:
				return "TYPE_CHAR";
			case 8:
				return "TYPE_STRING";
			case 9:
				return "TYPE_BOOLEAN";
			case 10:
				return "TYPE_COLLECTION";
			case 11:
				return "TYPE_VEC2D";
//			case 12:
//				return "TYPE_TEXTURE";
//			case 13:
//				return "TYPE_ANIMATION";
//			case 14:
//				return "TYPE_COLOR";
			default:
				return "TYPE_NULL";
		}
	}
	
}

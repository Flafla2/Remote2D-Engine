package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import com.remote.remote2d.engine.Remote2DException;
import com.remote.remote2d.engine.logic.Vector2;

public class R2DTypeCollection extends R2DType {
	
	private Map<String,R2DType> data;
	
	public R2DTypeCollection(String id)
	{
		super(id);
		data = new HashMap<String,R2DType>();
	}

	@Override
	public void read(DataInput d) throws IOException {
		if(!(d.readByte() == getId()))
			throw new Remote2DException(null,".r2d file doesn't begin with a Collection!");
		
		name = d.readUTF();
		
		data.clear();
		boolean isNext = true;
		while(isNext)
		{
			R2DType type = readNamedType(d);
			if(type.getId() == 0)
				isNext = false;
			else
			{
				data.put(type.getName(), type);
			}
		}
	}
	
	@Override
	public void read(Element e) {
		if(!e.getAttribute("id").getValue().equals(getId()+""))
			throw new Remote2DException(null,"XML file doesn't begin with a Collection!"+e.getAttribute("id").getValue());
		
		name = e.getAttributeValue("name");
		
		data.clear();
		Elements elements = e.getChildElements("type");
		for(int x=0; x<elements.size(); x++)
		{
			R2DType type = readNamedType(elements.get(x));
			data.put(type.getName(), type);
		}
	}

	@Override
	public void write(DataOutput d) throws IOException {
		d.writeByte(getId());
		d.writeUTF(name);
		
		Iterator<Entry<String, R2DType>> dataIterator = data.entrySet().iterator();
		while(dataIterator.hasNext())
		{
			Map.Entry<String, R2DType> pairs = dataIterator.next();
			writeNamedType(pairs.getValue(),d);
		}
		d.writeByte(0);
	}

	@Override
	public void write(Element e) {
		e.addAttribute(new Attribute("id",getId()+""));
		e.addAttribute(new Attribute("name",name));
		
		Iterator<Entry<String, R2DType>> dataIterator = data.entrySet().iterator();
		while(dataIterator.hasNext())
		{
			Map.Entry<String, R2DType> pairs = dataIterator.next();
			Element typeElement = new Element("type");
			writeNamedType(pairs.getValue(),typeElement);
			e.appendChild(typeElement);
		}
	}
	
	public Iterator<Entry<String, R2DType>> getDataIterator()
	{
		return data.entrySet().iterator();
	}
	
	public void printContents()
	{
		String infoString = getInfoString(1);
		System.out.println(infoString);
	}
	
	public String getInfoString(int indents)
	{
		Iterator<Entry<String, R2DType>> dataIterator = data.entrySet().iterator();
		String indentString = "";
		for(int x=0;x<indents-1;x++)
			indentString += "    ";
		String s = indentString+getTypeName(getId())+": \""+getName()+"\":\n";
		indentString+="    ";
		while(dataIterator.hasNext())
		{
			Map.Entry<String, R2DType> pairs = dataIterator.next();
			
			if(pairs.getValue().getId() == getId())
			{
				s += ((R2DTypeCollection)pairs.getValue()).getInfoString(indents+1);
				continue;
			}
			
			String spaces = "";
			String typeName = getTypeName(pairs.getValue().getId());
			for(int x=0;x<12-typeName.length();x++)
				spaces += " ";
			s += indentString+typeName+": "+spaces+"\""+pairs.getKey()+"\" : "+pairs.getValue().toString()+"\n";
		}
		return s;
	}
	
	public boolean hasKey(String key)
	{
		return data.containsKey(key);
	}
	
	public void setType(String key, R2DType value)
	{
		data.put(key, value);
	}
	
	public void setByte(String key, byte value)
	{
		setType(key,new R2DTypeByte(key,value));
	}
	
	public void setShort(String key, short value)
	{
		setType(key,new R2DTypeShort(key,value));
	}
	
	public void setInteger(String key, int value)
	{
		setType(key,new R2DTypeInt(key,value));
	}
	
	public void setLong(String key, long value)
	{
		setType(key,new R2DTypeLong(key,value));
	}
	
	public void setFloat(String key, float value)
	{
		setType(key,new R2DTypeFloat(key,value));
	}
	
	public void setDouble(String key, double value)
	{
		setType(key,new R2DTypeDouble(key,value));
	}
	
	public void setChar(String key, char value)
	{
		setType(key,new R2DTypeChar(key,value));
	}
	
	public void setVector2D(String key, Vector2 value)
	{
		setType(key,new R2DTypeVec2D(key,value));
	}
//	
//	public void setTexture(String key, Texture value)
//	{
//		setType(key,new R2DTypeTexture(key,value));
//	}
//	
//	public void setAnimation(String key, Animation value)
//	{
//		setType(key,new R2DTypeAnimation(key,value));
//	}
	
	public void setString(String key, String value)
	{
		setType(key,new R2DTypeString(key,value));
	}
	
	public void setBoolean(String key, boolean value)
	{
		setType(key,new R2DTypeBoolean(key,value));
	}
	
	public void setCollection(R2DTypeCollection collection)
	{
		setType(collection.name, collection);
	}
	
//	public void setColor(String key, Color value) {
//		setType(key,new R2DTypeColor(key,value));
//	}
	
	public R2DType getType(String key)
	{
		return data.get(key);
	}
	
	public byte getByte(String key)
	{
		if(!data.containsKey(key))
			return 0;
		return ((R2DTypeByte) data.get(key)).data;
	}
	
	public int getInteger(String key)
	{
		if(!data.containsKey(key))
			return 0;
		return ((R2DTypeInt) data.get(key)).data;
	}
	
	public short getShort(String key)
	{
		if(!data.containsKey(key))
			return 0;
		return ((R2DTypeShort) data.get(key)).data;
	}
	
	public Vector2 getVector2D(String key)
	{
		if(!data.containsKey(key))
			return null;
		return ((R2DTypeVec2D) data.get(key)).data.copy();
	}
//	
//	public Texture getTexture(String key)
//	{
//		if(!data.containsKey(key))
//			return null;
//		return new Texture(((R2DTypeTexture) data.get(key)).data.textureLocation);
//	}
//	
//	public Animation getAnimation(String key)
//	{
//		if(!data.containsKey(key))
//			return null;
//		return Remote2D.artLoader.getAnimation(((R2DTypeAnimation) data.get(key)).data.getPath());
//	}
	
	public long getLong(String key)
	{
		if(!data.containsKey(key))
			return 0;
		return ((R2DTypeLong) data.get(key)).data;
	}
	
	public float getFloat(String key)
	{
		if(!data.containsKey(key))
			return 0;
		return ((R2DTypeFloat) data.get(key)).data;
	}
	
	public double getDouble(String key)
	{
		if(!data.containsKey(key))
			return 0;
		return ((R2DTypeDouble) data.get(key)).data;
	}
	
	public char getChar(String key)
	{
		if(!data.containsKey(key))
			return '\u0000';
		return ((R2DTypeChar) data.get(key)).data;
	}
	
	public String getString(String key)
	{
		if(!data.containsKey(key))
			return "";
		return ((R2DTypeString) data.get(key)).data;
	}
	
	public boolean getBoolean(String key)
	{
		if(!data.containsKey(key))
			return false;
		return ((R2DTypeBoolean) data.get(key)).data;
	}
	
	public R2DTypeCollection getCollection(String key)
	{
		if(!data.containsKey(key))
			return null;
		return (R2DTypeCollection) data.get(key);
	}
	
//	public Color getColor(String key)
//	{
//		if(!data.containsKey(key))
//			return null;
//		return ((R2DTypeColor)data.get(key)).data;
//	}

	@Override
	public byte getId() {
		return 10;
	}
	
	@Override
	public String toString()
	{
		return data.toString();
	}
	
	@Override
	public boolean equals(R2DType type)
	{
		if(!(type instanceof R2DTypeCollection))
			return false;
		Iterator<Entry<String, R2DType>> iterator = ((R2DTypeCollection)type).getDataIterator();
		while(iterator.hasNext())
		{
			Entry<String, R2DType> entry = iterator.next();
			if(!entry.getValue().equals(getType(entry.getKey())))
				return false;
		}
		
		Iterator<Entry<String, R2DType>> thisIterator = getDataIterator();
		while(thisIterator.hasNext())
		{
			Entry<String, R2DType> entry = thisIterator.next();
			if(!entry.getValue().equals(((R2DTypeCollection)type).getType(entry.getKey())))
					return false;
		}
		return true;
	}

}

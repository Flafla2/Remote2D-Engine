package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import nu.xom.Element;

public class R2DTypeEnd extends R2DType {

	public R2DTypeEnd() {
		super("");
	}

	@Override
	public void read(DataInput d) throws IOException {
		
	}

	@Override
	public void write(DataOutput d) throws IOException {
		
	}
	
	@Override
	public void read(Element e) {
		
	}

	@Override
	public void write(Element e) {
		
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean equals(R2DType type)
	{
		if(!(type instanceof R2DTypeBoolean))
			return false;
		return true;
	}

}

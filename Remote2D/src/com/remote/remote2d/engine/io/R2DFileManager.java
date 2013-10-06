package com.remote.remote2d.engine.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.Remote2DException;

public class R2DFileManager {
	
	private String path;
	private File file;
	private R2DTypeCollection collection;
	private R2DFileSaver saverClass;
	
	public R2DFileManager(String path, R2DFileSaver saverClass)
	{
		this.path = path;
		file = new File(Remote2D.getJarPath()+path);
		collection = new R2DTypeCollection(file.getName());
		this.saverClass = saverClass;
	}
	
	public void read()
	{
		boolean read = file.exists();
		
		if(read)
		{
			Log.info("File Manager Reading file -- file exists!");
			try {
				FileInputStream fis = new FileInputStream(file);
				DataInputStream dis = new DataInputStream(fis);
				read(dis);
				dis.close();
				fis.close();
			} catch (IOException e) {
				throw new Remote2DException(e,"Error reading R2D file!");
			}
		} else
			collection = new R2DTypeCollection(collection.getName());
	}
	
	public void write()
	{
		try {
			doWriteOperation(file);
			if("true".equalsIgnoreCase(System.getProperty("runInEclipse")))
			{
				Log.debug("We are running in eclipse, saving file in src!");
				File file2 = new File("src"+path);
				doWriteOperation(file2);
			}
		} catch (IOException e) {
			throw new Remote2DException(e,"Error writing R2D file!");
		}
	}
	
	private void doWriteOperation(File file) throws IOException
	{
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream dos = new DataOutputStream(fos);
		write(dos);
		dos.close();
		fos.close();
	}
	
	private void read(DataInput d) throws IOException
	{
		collection.read(d);
		collection.printContents();
		if(saverClass != null)
			saverClass.loadR2DFile(collection);
	}
	
	private void write(DataOutput d) throws IOException
	{
		/*collection.setByte("Test Byte", (byte)25);
		collection.setBoolean("Test Boolean", true);
		collection.setString("Test String", "Testicular cancer");
		collection.setInteger("Test Integer", 12345);
		collection.setShort("Test Short", (short)300);
		collection.setFloat("Test Float", 123.45f);
		collection.setDouble("Test Double", 123.4567d);
		collection.setChar("Test Character", 'x');
		collection.setLong("Test Long", (long)12341234);*/
		if(saverClass != null)
			saverClass.saveR2DFile(collection);
		
		collection.write(d);
		if(Log.DEBUG)
			collection.printContents();
	}
	
	public File getFile()
	{
		return new File(file.getPath());
	}
	
	public String getPath()
	{
		return path;
	}
	
}

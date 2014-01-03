package com.remote.remote2d.engine.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.Remote2DException;
import com.remote.remote2d.engine.art.ResourceLoader;

public class R2DFileManager {
	
	private String path;
	private File file;
	private R2DTypeCollection collection;
	private R2DFileSaver saverClass;
	
	public R2DFileManager(String path, R2DFileSaver saverClass)
	{
		this.path = path;
		file = R2DFileUtility.getResource(path);
		collection = new R2DTypeCollection(file.getName());
		this.saverClass = saverClass;
	}
	
	public void read() throws IOException
	{
		boolean read = file.exists();
		boolean xml = true;
		
		if(file.getName().toLowerCase().endsWith(".xml"))
			xml = true;
		else if(file.getName().toLowerCase().endsWith(".bin"))
			xml = false;
		
		if(read)
			doReadOperation(file,xml);
		else
			collection = new R2DTypeCollection(collection.getName());
	}
	
	public void write(boolean xml) throws IOException
	{
		if(!file.getName().toLowerCase().endsWith(".xml") && !file.getName().toLowerCase().endsWith(".bin"))
			throw new IOException("INVALID FILE EXTENSION! Only write to a file that ends with .xml or .bin.");
		
		try {
			File file = R2DFileUtility.getResource(this.path);
						
			doWriteOperation(file,xml);
			
			ResourceLoader.refreshFile(file);
		} catch (IOException e) {
			throw new Remote2DException(e,"Error writing R2D file!");
		}
	}
	
	public void write() throws IOException
	{
		if(file.getName().toLowerCase().endsWith(".xml"))
			write(true);
		else if(file.getName().toLowerCase().endsWith(".bin"))
			write(false);
		else
			throw new IOException("INVALID FILE EXTENSION! Only write to a file that ends with .xml or .bin.");
	}
	
	private void doWriteOperation(File file, boolean xml) throws IOException
	{
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream dos = new DataOutputStream(fos);
		write(file,dos,xml);
		dos.close();
		fos.close();
	}
	
	private void doReadOperation(File file, boolean xml) throws IOException
	{
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		read(file,dis,xml);
		dis.close();
		fis.close();
	}
	
	private void read(File file, DataInputStream d, boolean xml) throws IOException
	{
		if(xml)
		{
			Builder parser = new Builder();
			try {
				Document doc = parser.build(d);
				Element root = doc.getRootElement();
				for(int x = 0;x<root.getChildCount();x++)
				{
					Node node = root.getChild(x);
					if(!(node instanceof Element))
						continue;
					collection.read((Element)node);
					
				}
			} catch (ValidityException e) {
				Log.warn("Invalid XML: "+file.getName());
			} catch (ParsingException e) {
				throw new Remote2DException(e,"Failed to parse XML for file: "+file.getName()+"!");
			}
		} else collection.read(d);
		
		if(saverClass != null)
			saverClass.loadR2DFile(collection);
	}
	
	private void write(File file, DataOutputStream d, boolean xml) throws IOException
	{
		if(saverClass != null)
			saverClass.saveR2DFile(collection);
		
		if(xml)
		{
			Element root = new Element("r2dxml");
			Element coll = new Element("type");
			collection.write(coll);
			root.appendChild(coll);
			
			Document doc = new Document(root);
			Serializer serializer = new Serializer(d, "ISO-8859-1");
			serializer.setIndent(4);
			serializer.write(doc);  
		} else
			collection.write(d);
	}
	
	public File getFile()
	{
		return new File(file.getPath());
	}
	
	public String getPath()
	{
		return path;
	}
	
	public R2DTypeCollection getCollection()
	{
		return collection;
	}
	
	public void setCollection(R2DTypeCollection coll)
	{
		this.collection = coll;
	}
	
}

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
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.Remote2DException;

public class R2DFileManager {
	
	private String path;
	private File file;
	private R2DTypeCollection collection;
	private R2DFileSaver saverClass;
	
	/**
	 * Whether or not to use XML for reading or writing files, unless otherwise
	 * specified.
	 */
	public static boolean USE_XML = false;
	
	public R2DFileManager(String path, R2DFileSaver saverClass)
	{
		this.path = path;
		file = new File(Remote2D.getJarPath()+path);
		collection = new R2DTypeCollection(file.getName());
		this.saverClass = saverClass;
	}
	
	public void read(boolean xml)
	{
		boolean read = file.exists();
		
		if(read)
		{
			Log.debug("File Manager","Reading file "+file.getName()+" file exists!");
			try {
				File file = this.file;
				if(xml && !file.getName().endsWith(".xml"))
				{
					File newFile = new File(file.getPath()+".xml");
					file = newFile.exists() ? newFile : file;
				} else if(!xml && file.getName().endsWith(".xml"))
				{
					File newFile = new File(file.getPath().substring(0,file.getPath().length()-4));
					file = newFile.exists() ? newFile : file;
				}
				
				doReadOperation(file,xml);
			} catch (IOException e) {
				throw new Remote2DException(e,"Error reading R2D file!");
			}
		} else
		{
			collection = new R2DTypeCollection(collection.getName());
			Log.warn("File "+file.getName()+" does not exist!");
		}
	}
	
	public void read()
	{
		read(USE_XML);
	}
	
	public void write(boolean xml)
	{
		try {
			String path = this.path;
			if(xml && !file.getName().endsWith(".xml"))
				path = path+".xml";
			else if(!xml && file.getName().endsWith(".xml"))
				path = path.substring(0,file.getPath().length()-4);
			
			Log.debug("File Manager","Writing to file "+path);
			
			doWriteOperation(new File(Remote2D.getJarPath()+path),xml);
			if("true".equalsIgnoreCase(System.getProperty("runInEclipse")))
			{
				Log.debug("File Manager","We are running in eclipse, saving file in src!");
				File file2 = new File("src"+path);
				doWriteOperation(file2,xml);
			}
		} catch (IOException e) {
			throw new Remote2DException(e,"Error writing R2D file!");
		}
	}
	
	public void write()
	{
		write(USE_XML);
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
		if(file.getName().endsWith(".xml"))
		{
			if(!xml)
				Log.warn("Couldn't find binary file "+this.file.getName()+", using "+file.getName()+" instead!");
			
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
			
		} else
		{
			if(xml)
				Log.warn("File Manager","Couldn't find xml file "+this.file.getName()+", using "+file.getName()+" instead!");
			collection.read(d);
		}
		
//		if(Log.DEBUG)
//			collection.printContents();
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
			//root.addAttribute(new Attribute("version","1.0"));
			Element coll = new Element("type");
			collection.write(coll);
			root.appendChild(coll);
			
			Document doc = new Document(root);
			Serializer serializer = new Serializer(d, "ISO-8859-1");
			serializer.setIndent(4);
			serializer.write(doc);  
		} else
			collection.write(d);
		
//		if(Log.DEBUG)
//			collection.printContents();
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

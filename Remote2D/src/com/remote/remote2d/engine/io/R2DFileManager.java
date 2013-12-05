package com.remote.remote2d.engine.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.Remote2DException;

public class R2DFileManager {
	
	private String path;
	private File file;
	private R2DTypeCollection collection;
	private R2DFileSaver saverClass;
	
	/**
	 * Whether or not to use XML for writing files, unless otherwise specified.
	 */
	public static boolean USE_XML = false;
	
	public R2DFileManager(String path, R2DFileSaver saverClass)
	{
		this.path = path;
		file = R2DFileUtility.getResource(path);
		collection = new R2DTypeCollection(file.getName());
		this.saverClass = saverClass;
	}
	
	public void read()
	{
		boolean read = file.exists();
		boolean xml = USE_XML;
		try
		{
			Path path = Paths.get(file.getAbsolutePath());
			UserDefinedFileAttributeView view = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);
			String name = "user.xml";
			ByteBuffer buf = ByteBuffer.allocate(view.size(name));
			view.read(name, buf);
			buf.flip();
			String value = Charset.defaultCharset().decode(buf).toString();
			xml = Boolean.parseBoolean(value);
			buf.clear();
			buf = null;
		} catch(Exception e)
		{
			Log.warn("Unable to read R2D file metadata: "+file.getName());
		}
		
		if(read)
		{
			Log.debug("File Manager","Reading file "+file.getName()+"; file exists!");
			try {
				doReadOperation(file,xml);
			} catch (IOException e) {
				throw new Remote2DException(e,"Error reading R2D file!");
			}
		} else
		{
			collection = new R2DTypeCollection(collection.getName());
		}
	}
	
	public void write(boolean xml)
	{
		try {
			File file = R2DFileUtility.getResource(this.path);
			
			Log.debug("File Manager","Writing to file "+file.getName());
			
			doWriteOperation(file,xml);
			
			Path path = Paths.get(file.getAbsolutePath());
			UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
			//TODO: Change xml check from using attributes to something else
			if(view != null)
			{
				view.write("user.xml", Charset.defaultCharset().encode(Boolean.toString(xml)));
				view.write("user.mimetype", Charset.defaultCharset().encode("application/xml"));
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

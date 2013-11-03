package com.remote.remote2d.engine.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * A filter used to denote what files are loadable by Remote2D.
 * @author Flafla2
 *
 */
public class R2DFileFilter implements FilenameFilter {
	
	/**
	 * A list of all extensions supported by the engine.  You can add to this list
	 * at startup if desired.  To add to the list, use the format <code>.xxx</code>
	 * if you want to support the ".xxx" file format.  Do NOT include XML extensions
	 * such as <code>.xxx.xml</code>.  These are handled by the engine.<br />
	 * 
	 * Also, do not add the following extensions as they are already used in the
	 * engine (you probably shouldn't save to these formats as well):
	 * <ul>
	 * <li>.xml</li>
	 * <li>.anim</li>
	 * <li>.r2d</li>
	 * </ul>
	 */
	public static ArrayList<String> extensions = new ArrayList<String>();

	@Override
	public boolean accept(File dir, String name) {
		for(String s : extensions)
			if(name.endsWith(s) || name.endsWith(s+".xml"))
				return true;
		
		return false;
	}

}

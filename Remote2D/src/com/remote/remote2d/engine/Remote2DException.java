package com.remote.remote2d.engine;

import com.esotericsoftware.minlog.Log;

public class Remote2DException extends RuntimeException {
	private static final long serialVersionUID = 6027016756902321101L;

	public Remote2DException(Exception e, String s) {
		Log.error("REMOTE2D HANDLED ERROR: " + s);
		Log.error("Here is the stack trace:\n");
		if (e != null)
			e.printStackTrace();
		else
			System.err.println("-----No stack trace provided-----");
	}

	public Remote2DException(Exception e) {
		this(e, "No description provided");
	}

}
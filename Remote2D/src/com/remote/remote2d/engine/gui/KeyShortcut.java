package com.remote.remote2d.engine.gui;

import java.util.ArrayList;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Keyboard;

import com.esotericsoftware.minlog.Log;

public class KeyShortcut {
	
	public boolean useControl = false;
	public boolean useMeta = false;
	public boolean useShift = false;
	public int[] shortcuts;
	
	private boolean pressed = false;
	
	public KeyShortcut(int[] shortcuts)
	{
		this.shortcuts = shortcuts;
		setUseControl(true);
	}
	
	@Override
	public String toString()
	{
		String result = "";
		if(useShift)
			result+='\u21e7';
		if(useControl)
			result+="Ctrl";
		if(useMeta)
			result += "\u2318";
		for(int x : shortcuts)
		{
			result += getStringFromID(x);
		}
		return result;
	}
	
	public boolean getShortcutActivated()
	{
		boolean oldPressed = pressed;
		pressed = true;
		boolean control = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
		if(useControl && !control)
			pressed = false;
		boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
		if(useShift && !shift)
			pressed = false;
		boolean meta = Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA);
		if(useMeta && !meta)
			pressed = false;
		for(int x : shortcuts)
		{
			if(!Keyboard.isKeyDown(x))
				pressed = false;
		}
		
		if(!oldPressed && pressed)
			return true;
		return false;
	}
	
	public KeyShortcut setUseControl(boolean useControl)
	{
		this.useControl = useControl;
		return this;
	}
	
	public KeyShortcut setUseShift(boolean useShift)
	{
		this.useShift = useShift;
		return this;
	}
	
	public KeyShortcut setUseMeta(boolean useMeta)
	{
		this.useMeta = useMeta;
		return this;
	}
	
	public KeyShortcut setMetaOrControl(boolean use)
	{
		useMeta = false;
		useControl = false;
		if(use)
		{
			if(LWJGLUtil.getPlatformName().equalsIgnoreCase("macosx"))
				useMeta = true;
			else
				useControl = true;
		}
			
		return this;
	}
	
	public String getStringFromID(int x)
	{
		switch(x)
		{
		case Keyboard.KEY_DELETE:
			return "\u2326";
		case Keyboard.KEY_BACK:
			return "\u232b";
		case Keyboard.KEY_LEFT:
			return "\u21e0";
		case Keyboard.KEY_UP:
			return "\u21e1";
		case Keyboard.KEY_RIGHT:
			return "\u21e2";
		case Keyboard.KEY_DOWN:
			return "\u21e3";
		case Keyboard.KEY_LBRACKET:
			return "[";
		case Keyboard.KEY_RBRACKET:
			return "]";
		case Keyboard.KEY_LSHIFT:
			return "\u21e7";
		case Keyboard.KEY_RSHIFT:
			return "\u21e7";
		case Keyboard.KEY_LCONTROL:
			return "Ctrl";
		case Keyboard.KEY_RCONTROL:
			return "Ctrl";
		case Keyboard.KEY_LMETA:
			return "\u2318";
		case Keyboard.KEY_RMETA:
			return "\u2318";
		case Keyboard.KEY_APOSTROPHE:
			return "\'";
		case Keyboard.KEY_COLON:
			return ";";
		case Keyboard.KEY_BACKSLASH:
			return "\\";
		case Keyboard.KEY_COMMA:
			return ",";
		case Keyboard.KEY_PERIOD:
			return ".";
		case Keyboard.KEY_SLASH:
			return "/";
		case Keyboard.KEY_EQUALS:
			return "=";
		case Keyboard.KEY_MINUS:
			return "-";
		case Keyboard.KEY_RETURN:
			return "\u23ce";
		case Keyboard.KEY_TAB:
			return "\u21b9";
		default:
			return Keyboard.getKeyName(x);
		}
	}
}

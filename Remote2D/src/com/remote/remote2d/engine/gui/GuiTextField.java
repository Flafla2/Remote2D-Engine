package com.remote.remote2d.engine.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiTextField extends Gui {
	
	public Vector2 pos;
	public Vector2 dim;
	public int fontsize;
	public int maxLength = -1;
	public String text = "";
	public String prefix = "";
	public String defaultText = "";
	
	public TextLimiter limitToDigits = TextLimiter.FULL;
	private String hexList = "0123456789abcdef";
	
	private boolean isSelected = false;
	private boolean hasTyped = false;
	private boolean blink = true;
	private int blinkTimer = 50;
	private int backTimer = 20;
	private KeyShortcut shortcut;
	
	
	public GuiTextField(Vector2 pos, Vector2 dim, int fontsize)
	{
		this.pos = pos;
		this.dim = dim;
		this.fontsize = fontsize;
		shortcut = new KeyShortcut(new int[]{Keyboard.KEY_V});
	}

	@Override
	public void tick(int i, int j, int k) {
		hasTyped = false;
		if(pos.getColliderWithDim(dim).isPointInside(new Vector2(i,j)) && Remote2D.getInstance().hasMouseBeenPressed())
		{
			isSelected = true;
			blink = true;
			blinkTimer = 50;
		} else if(Remote2D.getInstance().hasMouseBeenPressed())
		{
			isSelected = false;
		}
		if(blinkTimer == 0)
		{
			blinkTimer = 50;
			blink = !blink;
		}
		else
			blinkTimer--;
		
		String paste = "";
		if(shortcut.getShortcutActivated())
			paste = getClipboardContents();
		
		if(isSelected)
		{
			ArrayList<Character> typedChars = (ArrayList<Character>) Remote2D.getInstance().getLimitedKeyboardList().clone();
			for(char c : paste.toCharArray())
				typedChars.add(c);
			for(int x=0;x<typedChars.size();x++)
			{
				char key = typedChars.get(x);
				
				boolean intLimit = Character.isDigit(key) || (key == '-' && !text.contains("-")) || limitToDigits != TextLimiter.LIMIT_TO_INTEGER;
				boolean floatLimit = (key == '.' || key == '-' || Character.isDigit(key)) || limitToDigits != TextLimiter.LIMIT_TO_FLOAT;
				boolean hexLimit = hexList.contains(""+key) || limitToDigits != TextLimiter.LIMIT_TO_HEX;
				boolean maxLimit = text.length() < maxLength || maxLength == -1;

				if((intLimit && floatLimit && hexLimit && maxLimit) || key == '\b')
				{
					hasTyped = true;
					if(key == '\b' && text.length() != 0)
					{
						text = text.substring(0, text.length() - 1);
					}
					else if(key != '\b')
						text += key;
				}
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_BACK))
			{
				if(backTimer == 0)
				{
					if(text.length() != 0)
						text = text.substring(0, text.length() - 1);
				} else
					backTimer--;
			}else
				backTimer = 10;
			
		}
	}

	@Override
	public void render(float interpolation) {
		
		Renderer.drawRect(pos, dim, 0x000000, 1.0f);
		Renderer.drawLineRect(pos, dim, 0xffffff, 1.0f);
		
		String s = prefix+text;
		if(!s.equals(""))
		{
			int[] size = Fonts.get("Arial").getStringDim(s, fontsize);
			while(size[0] > dim.x-20)
			{
				s = s.substring(1, s.length());
				size = Fonts.get("Arial").getStringDim(s, fontsize);
			}
		}
		
		float yPos = pos.y+dim.y/2-Fonts.get("Arial").getStringDim(text, fontsize)[1]/2;
		Fonts.get("Arial").drawString(s+((isSelected && blink) ? "|" : "")+" ", pos.x+10, yPos, fontsize, 0xffffff);
		if(text.equals("") && !isSelected)
			Fonts.get("Arial").drawString(defaultText, pos.x+10, yPos, fontsize, 0x777777);
	}
	
	public boolean hasText()
	{
		return !text.equals("");
	}
	
	public void deselect()
	{
		isSelected = false;
	}
	
	public boolean hasTyped()
	{
		return hasTyped;
	}
	
	public boolean isSelected()
	{
		return isSelected;
	}
	
	public String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		//odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if ( hasTransferableText ) {
			try {
				result = (String)contents.getTransferData(DataFlavor.stringFlavor);
			}
			catch(Exception e)
			{
				return null;
			}
		}
		return result;
	}
}

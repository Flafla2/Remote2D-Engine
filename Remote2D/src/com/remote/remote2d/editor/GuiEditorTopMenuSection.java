package com.remote.remote2d.editor;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.gui.Gui;
import com.remote.remote2d.engine.gui.KeyShortcut;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorTopMenuSection extends Gui {
	
	public static final int subheight = 20;
	
	GuiEditorTopMenu menu;
	
	String[] values;
	String title;
	int x;
	int y;
	int width;
	int subWidth;
	int height;
	int[] titledim;
	private boolean isEnabled = true;
	
	boolean isHovered = false;
	boolean isSelected = false;
	private int hoveredBox = -1;	//the drop down subsection that is currently hovered over
	private int selectedBox = -1;  	//when a box is physically clicked on, this changes.
									//It is used to communicate between this class and the menu class
									//via popSelectedBox()
	
	public KeyShortcut[] keyCombos;
	
	public GuiEditorTopMenuSection(int x, int y, int h, String[] values, String title, GuiEditorTopMenu menu)
	{
		super();
		
		this.x = x;
		this.y = y;
		this.menu = menu;
		height = h;
		this.values = values;
		this.title = title;
		titledim = Fonts.get("Arial").getStringDim(title, 20);
		width = titledim[0]+20;
		
		keyCombos = new KeyShortcut[values.length];
		
		reloadSubWidth();
	}
	
	public void reloadSubWidth()
	{
		subWidth = width;
		for(int i=0;i<values.length;i++)
		{
			int thisWidth = Fonts.get("Arial").getStringDim(values[i], 20)[0];
			if(keyCombos[i] != null)
			{
				int keyWidth = Fonts.get("Arial").getStringDim(keyCombos[i].toString(), 20)[0];
				thisWidth += keyWidth+20;
			}
			if(thisWidth > subWidth)
				subWidth = thisWidth;
		}
		subWidth += 20;
	}
	
	public void setEnabled(boolean enabled)
	{
		if(isEnabled != enabled)
		{
			//Log.debug("Setting enabled of: "+title+" : "+isEnabled + " : "+enabled);
			isEnabled = enabled;
			menu.initSections();
			
			//Log.debug("Setting enabled of: "+title+" : "+isEnabled + " : "+enabled);
		}
	}
	
	public boolean getEnabled()
	{
		return isEnabled;
	}

	@Override
	public void render(float interpolation) {
		if(!isEnabled)
			return;
		int xPos = x+width/2-titledim[0]/2;
		int yPos = y+height/2-titledim[1]/2;
		
		if(isSelected)
			Renderer.drawRect(new Vector2(x,y), new Vector2(width,height), 1,  0.6f, 0.6f, 1);
		
		Fonts.get("Arial").drawString(title, xPos, yPos, 20, 0xffffff);
		
		if(!isSelected)
			return;
		int currentY = height;
		
		for(int i=0;i<values.length;i++)
		{
			boolean isBoxHovered = hoveredBox == i;
			
			Renderer.drawRect(new Vector2(x,currentY), new Vector2(subWidth, height), 1, isBoxHovered?0.6f:0.2f, isBoxHovered?0.6f:0.2f, 1);
			Renderer.drawLineRect(new Vector2(x,currentY), new Vector2(subWidth, height), 0, 0, 0, 1);
			
			Fonts.get("Arial").drawString(values[i],x+10,currentY, 20, 0xffffff);
			
			if(keyCombos[i] != null)
			{
				int keyWidth = Fonts.get("Arial").getStringDim(keyCombos[i].toString(), 20)[0];
				Fonts.get("Arial").drawString(keyCombos[i].toString(), x+subWidth-10-keyWidth, currentY, 20, 0xffffff);
			}
			
			currentY += subheight;
		}
	}
	
	@Override
	public void tick(int i, int j, int k) {
		for(int x = 0; x<keyCombos.length; x++)
		{
			if(keyCombos[x] == null)
				continue;
			boolean hasComboBeenPressed = keyCombos[x].getShortcutActivated();
			if(hasComboBeenPressed && !menu.editor.getInspector().isTyping())
			{
				selectedBox = x;
			}
		}
		if(!isEnabled)
			return;
		isHovered = i > x && j > y && i < x+width && j < y+height;
		if (Remote2D.getInstance().hasMouseBeenPressed() && isHovered)
		{
			isSelected = !isSelected;
		}
		
		if(isSelected && !isHovered)
		{
			for(int x=0;x<menu.sections.size();x++)
			{
				if(menu.sections.get(x).isHovered)
				{
					menu.sections.get(x).isSelected = true;
					isSelected = false;
				}
			}
		}
		
		if(isSelected)
		{
			int currentY = height;
			hoveredBox = -1;
			for(int a=0;a<values.length;a++)
			{
				boolean isBoxHovered = i > x && j > currentY && i < x+subWidth && j < currentY+subheight;
				if(isBoxHovered)
				{
					hoveredBox = a;
					a=values.length;//stop the loop
				}
				
				currentY += subheight;
			}
			if(!isHovered && Remote2D.getInstance().hasMouseBeenPressed())
			{
				isSelected = false;
				if(hoveredBox != -1)
				{
					selectedBox = hoveredBox;
				}
			}
		}
	}
	
	public int popSelectedBox()
	{
		int box = selectedBox;
		selectedBox = -1;
		return box;
	}

}

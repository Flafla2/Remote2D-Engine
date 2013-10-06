package com.remote.remote2d.editor;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.gui.GuiWindow;
import com.remote.remote2d.engine.gui.WindowHolder;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Interpolator;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.world.Console;
import com.remote.remote2d.engine.world.Message;

public class GuiWindowConsole extends GuiWindow {
	
	public ArrayList<ConsoleMessage> messages;
	private long lastUpdateTime = -1;
	private float oldOffset = 0;
	private float offset = 0;

	public GuiWindowConsole(WindowHolder holder, Vector2 pos, Vector2 dim, ColliderBox allowedBounds) {
		super(holder, pos, dim, allowedBounds, "Console");
		messages = new ArrayList<ConsoleMessage>();
		updateEntries();
	}

	@Override
	public void renderContents(float interpolation) {
		float offset = (float) Interpolator.linearInterpolate(oldOffset, this.offset, interpolation);
		GL11.glPushMatrix();
		GL11.glTranslatef(0, -offset, 0);
		int yPos = 0;
		for(ConsoleMessage message : messages)
		{
			if((yPos > offset && yPos < offset+dim.y) || (yPos+message.dim.y > offset && yPos+message.dim.y < offset+dim.y))
				message.render(new Vector2(0,yPos),interpolation);
			yPos += message.dim.y;
		}
		GL11.glPopMatrix();
	}
	
	@Override
	public void tick(int i, int j, int k)
	{
		super.tick(i,j,k);
		oldOffset = offset;
		if(pos.getColliderWithDim(dim).isPointInside(new Vector2(i,j)))
		{
			if(Remote2D.getInstance().getDeltaWheel() < 0)
				offset += 20;
			if(Remote2D.getInstance().getDeltaWheel() > 0)
				offset -= 20;
			
			if(offset > getTotalSectionHeight()-dim.y+20)
				offset = getTotalSectionHeight()-dim.y+20;
			if(offset < 0)
				offset = 0;
		}
		if(lastUpdateTime < Console.getLastMessageTime())
		{
			updateEntries();
			lastUpdateTime = System.currentTimeMillis();
		}
	}
	
	public void updateEntries()
	{
		messages.clear();
		int ypos = 0;
		for(int x=0;x<Console.size();x++)
		{
			ConsoleMessage mess = new ConsoleMessage(this,Console.getMessage(x));
			messages.add(mess);
			ypos += mess.getDim().y;
		}
	}
	
	public float getTotalSectionHeight()
	{
		float value = 0;
		for(ConsoleMessage message : messages)
			value += message.getDim().y;
		return value;
	}
	
	private class ConsoleMessage
	{
		private Message message;
		private Vector2 dim;
		private GuiWindowConsole console;
		
		public ConsoleMessage(GuiWindowConsole console, Message message)
		{
			this.message = message;
			this.dim = new Vector2(console.dim.x,message.getRenderHeight((int) console.dim.x,20));
			this.console = console;
		}
		
		public void render(Vector2 pos, float interpolation)
		{
			if(dim.x != console.dim.x)
				this.dim = new Vector2(console.dim.x,message.getRenderHeight((int) console.dim.x,20));
			message.render(pos,20,(int)dim.x);
			Renderer.drawLine(new Vector2(pos.x,pos.y+dim.y), pos.add(dim), 0xffffff, 1.0f);
		}
		
		public Vector2 getDim()
		{
			return dim;
		}
	}

}

package com.remote.remote2d.engine.world;

import java.util.ArrayList;
import java.util.Stack;

import com.esotericsoftware.minlog.Log;

public class Console {
	
	private static ArrayList<Message> messageStack = new ArrayList<Message>();
	private static int maxMessages = 100;
	private static long lastMessageTime = System.currentTimeMillis();
	
	public static long getLastMessageTime()
	{
		return lastMessageTime;
	}
	
	public static void pushError(String message)
	{
		pushMessage(null,message,0xff0000,Log.LEVEL_ERROR);
	}
	
	public static void pushMessage(String message)
	{
		pushMessage(null,message,0xffffff,Log.LEVEL_INFO);
	}
	
	public static void pushMessage(String prefix, String message, int color, int minlogType)
	{
		pushMessage(new Message(prefix,message,color,minlogType));
	}
	
	public static void pushMessage(Message message)
	{
		messageStack.add(message);
		updateMessageCount();
		lastMessageTime = System.currentTimeMillis();
	}
	
	public static void pushMessage(Message message, int index)
	{
		messageStack.add(index, message);
		updateMessageCount();
		lastMessageTime = System.currentTimeMillis();
	}
	
	public static Message getMessage(int index)
	{
		return messageStack.get(index);
	}
	
	public static void setMaxMessages(int maxMessages)
	{
		Console.maxMessages = maxMessages;
		updateMessageCount();
	}
	
	public static int size()
	{
		return messageStack.size();
	}
	
	private static void updateMessageCount()
	{
		while(messageStack.size() > maxMessages)
		{
			messageStack.remove(messageStack.size()-1);
		}
	}
	
	public static void clear()
	{
		messageStack.clear();
	}

}

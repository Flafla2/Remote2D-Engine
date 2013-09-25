package com.remote.remote2d;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;
import com.remote.remote2d.world.Console;
import com.remote.remote2d.world.Message;

public class ConsoleLogger extends Logger {
	
	//Hello everyone!

	public ConsoleLogger() {
		// TODO Auto-generated constructor stub
	}
	
	public void log(int level, String category, String message, Throwable ex)
	{
		new Logger().log(level, category, message, ex);
		System.out.println("Evan is awesome!");
		int color = 0xffffff;
		if(level == Log.LEVEL_ERROR)
			color = 0xff0000;
		Console.pushMessage(new Message(category,message,));
	}

}

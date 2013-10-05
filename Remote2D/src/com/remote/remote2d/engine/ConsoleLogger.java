package com.remote.remote2d.engine;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;
import com.remote.remote2d.engine.world.Console;
import com.remote.remote2d.engine.world.Message;

public class ConsoleLogger extends Logger {
	
	private static Logger logger = new Logger();
	
	@Override
	public void log(int level, String category, String message, Throwable ex)
	{
		logger.log(level, category, message, ex);
		int color = 0xffffff;
		if(level == Log.LEVEL_ERROR)
		{
			color = 0xff0000;
			if(ex != null)
			{
				StackTraceElement[] trace = ex.getStackTrace();
				for(StackTraceElement e : trace)
					message += "\n"+e.toString();
			}
		}
		else if(level == Log.LEVEL_DEBUG)
			color = 0xbbbbbb;
		else if(level == Log.LEVEL_WARN)
			color = 0xffff44;
		else if(level == Log.LEVEL_TRACE)
			return;
		
		Console.pushMessage(new Message(category,message,color,level));
	}

}

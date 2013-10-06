package com.remote.remote2d.engine.gui;

public interface WindowHolder {
	
	public GuiWindow getTopWindow();
	public void pushWindow(GuiWindow window);
	public void closeWindow(GuiWindow window);
	
}

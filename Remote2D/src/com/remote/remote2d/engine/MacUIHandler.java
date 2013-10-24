package com.remote.remote2d.engine;

import javax.swing.JOptionPane;

import org.simplericity.macify.eawt.ApplicationEvent;
import org.simplericity.macify.eawt.ApplicationListener;

/**
 * Class that adds elements to agree with Apple's Human Interface Guidelines (HIG)
 * such as a dock icon and menu bar on OSX.  Uses Simplericity's Macify library
 * for platform independence.
 * 
 * @author Flafla2
 */
public class MacUIHandler implements ApplicationListener {

	@Override
	public void handleAbout(ApplicationEvent event) {
		JOptionPane.showMessageDialog(null, "Remote2D Version "+Remote2D.getVersion(), "About", JOptionPane.INFORMATION_MESSAGE);
		event.setHandled(true);
	}

	@Override
	public void handleOpenApplication(ApplicationEvent event) {
		//Not much we can do here
		//Application launching is already handled by Remote2D
	}

	@Override
	public void handleOpenFile(ApplicationEvent event) {
		//Maybe we can do something with this...?
	}

	@Override
	public void handlePreferences(ApplicationEvent event) {
		//Can't really do anything here
	}

	@Override
	public void handlePrintFile(ApplicationEvent event) {
		JOptionPane.showMessageDialog(null, "What do you want to print...?");
	}

	@Override
	public void handleQuit(ApplicationEvent event) {
		Remote2D.running = false;
		event.setHandled(true);
	}

	@Override
	public void handleReOpenApplication(ApplicationEvent event) {
		//Yep
	}

}

package com.remote.remote2d.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

public class ErrorHandler {
	public static void crash(final Exception e) {
		File log = new File("log." + System.currentTimeMillis() + ".txt");

		try {
			log.createNewFile();
			new BufferedWriter(new FileWriter(log)) {
				{
					write("Remote2D Crash Log\n\n");
					newLine();
					newLine();
					for (StackTraceElement ste : e.getStackTrace())
						write(ste.toString());
					close();
				}
			};
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JOptionPane.showMessageDialog(
				null,
				"Sorry, the engine crashed. Log written to\n"
						+ log.getAbsolutePath());
		System.exit(1);
	}
}
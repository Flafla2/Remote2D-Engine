package com.remote.remote2d.engine.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.remote.remote2d.engine.Remote2D;

public class GuiMenu extends Gui {

	protected ArrayList<GuiButton> buttonList;
	public int backgroundColor = 0xffffff;

	public GuiMenu() {
		super();
		buttonList = new ArrayList<GuiButton>();
		initGui();
	}

	@Override
	public void render(float interpolation) {
		renderBackground(interpolation);
		for (int x = 0; x < buttonList.size(); x++)
			buttonList.get(x).render(interpolation);
	}

	public void initGui() {

	}

	public void renderBackground(float interpolation) {

	}

	public void drawBlueprintBackground() {
		final int bgcolor = 0x7f9ddf;
		final int medcolor = 0x6b8fdf;
		final int smallcolor = 0x5e87df;
		final int largecolor = 0x98aedf;

		backgroundColor = bgcolor;

		bindRGB(smallcolor);
		drawGrid(25);
		bindRGB(medcolor);
		GL11.glLineWidth(3);
		drawGrid(100);
		bindRGB(largecolor);
		GL11.glLineWidth(5);
		drawGrid(200);
		GL11.glLineWidth(1);
		GL11.glColor3f(1, 1, 1);

	}

	private void drawGrid(int distance) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		for (int x = 0; x <= screenWidth() / distance; x++) {
			GL11.glVertex2f(x * distance, 0);
			GL11.glVertex2f(x * distance, screenHeight());
		}

		for (int x = 0; x <= screenHeight() / distance; x++) {
			GL11.glVertex2f(0, x * distance);
			GL11.glVertex2f(screenWidth(), x * distance);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	public void tick(int i, int j, int k) {
		for (int x = 0; x < buttonList.size(); x++) {
			buttonList.get(x).tick(i, j, k);
			if (buttonList.get(x).selectState == 2
					|| buttonList.get(x).selectState == 3) {
				if (Remote2D.getInstance().hasMouseBeenPressed()) {
					// if(buttonList.get(x).sound != 0)
					// DefenseStep.getInstance().soundManager.quickPlay((buttonList.get(x).sound==1
					// ? "gui.select" : "gui.back"), 0, 0, false, false, false);
					actionPerformed(buttonList.get(x));
				}
			}
		}
	}

	public void actionPerformed(GuiButton button) {

	}

	/**
	 * Overrides the default stretch type, if needed.
	 * 
	 * @return Overridden stretch type, or null if there is no override. Default
	 *         is null.
	 */
	// public StretchType getOverrideStretchType()
	// {
	// return null;
	// }

}

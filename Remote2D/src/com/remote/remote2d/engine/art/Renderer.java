package com.remote.remote2d.engine.art;

import org.lwjgl.opengl.GL11;

import com.remote.remote2d.engine.DisplayHandler;
import com.remote.remote2d.engine.gui.Gui;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;

/**
 * A universal renderer class. This is used to abstract out all of the annoying
 * GL11 calls, and consolidates them into one line. This is also useful to do
 * any widespread changes to the render system (switching core libraries, or
 * OpenGL versions).
 * 
 * TODO: Update from old OpenGL to new OpenGL.
 * 
 * @author Adrian
 * 
 */
public class Renderer {

	private static boolean wireframe = false;

	public static void drawPoly(Vector2[] vectors, Vector2[] uv, Texture tex,
			float red, float green, float blue, float alpha) {
		if (isWireframe()) {
			drawLinePoly(vectors, red, green, blue, alpha);
			return;
		}
		tex.bind();
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_POLYGON);

		for (int x = 0; x < vectors.length; x++) {
			GL11.glTexCoord2f(uv[x].x, uv[x].y);
			GL11.glVertex2f(vectors[x].x, vectors[x].y);
		}

		GL11.glEnd();

		GL11.glColor3f(1, 1, 1);
	}

	public static void drawPoly(Vector2[] vectors, float red, float green,
			float blue, float alpha) {
		if (isWireframe()) {
			drawLinePoly(vectors, red, green, blue, alpha);
			return;
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_POLYGON);

		for (int x = 0; x < vectors.length; x++)
			GL11.glVertex2f(vectors[x].x, vectors[x].y);

		GL11.glEnd();

		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawLinePoly(Vector2[] vectors, float red, float green,
			float blue, float alpha) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_LINE_STRIP);

		for (int x = 0; x < vectors.length; x++)
			GL11.glVertex2f(vectors[x].x, vectors[x].y);

		GL11.glVertex2f(vectors[0].x, vectors[0].y);

		GL11.glEnd();

		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawLinePoly(Vector2[] verts, int color, float alpha) {
		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		drawLinePoly(verts, r, g, b, alpha);
	}

	public static void drawRect(Vector2 pos, Vector2 dim, Vector2 uvPos,
			Vector2 uvDim, Texture tex, int color, float alpha) {
		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		drawRect(pos, dim, uvPos, uvDim, tex, r, g, b, alpha);
	}

	public static void drawRect(Vector2 pos, Vector2 dim, int color, float alpha) {
		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		drawRect(pos, dim, r, g, b, alpha);
	}

	public static void drawRect(Vector2 pos, Vector2 dim, Vector2 uvPos,
			Vector2 uvDim, Texture tex, float red, float green, float blue,
			float alpha) {
		if (isWireframe()) {
			drawCrossRect(pos, dim, red, green, blue, alpha);
			return;
		}
		tex.bind();
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glTexCoord2f(uvPos.x, uvPos.y);
		GL11.glVertex2f(pos.x, pos.y);
		GL11.glTexCoord2f(uvPos.x + uvDim.x, uvPos.y);
		GL11.glVertex2f(pos.x + dim.x, pos.y);
		GL11.glTexCoord2f(uvPos.x + uvDim.x, uvPos.y + uvDim.y);
		GL11.glVertex2f(pos.x + dim.x, pos.y + dim.y);
		GL11.glTexCoord2f(uvPos.x, uvPos.y + uvDim.y);
		GL11.glVertex2f(pos.x, pos.y + dim.y);

		GL11.glEnd();

		GL11.glColor3f(1, 1, 1);
	}

	public static void drawRect(Vector2 pos, Vector2 dim, Texture tex,
			float red, float green, float blue, float alpha) {
		drawRect(pos, dim, new Vector2(0, 0), new Vector2(1, 1), tex, red,
				green, blue, alpha);
	}

	public static void drawRect(Vector2 pos, Vector2 dim, Texture tex,
			int color, float alpha) {
		drawRect(pos, dim, new Vector2(0, 0), new Vector2(1, 1), tex, color,
				alpha);
	}

	public static void drawRect(Vector2 pos, Vector2 dim, float red,
			float green, float blue, float alpha) {
		if (isWireframe()) {
			drawCrossRect(pos, dim, red, green, blue, alpha);
			return;
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glVertex2f(pos.x, pos.y);
		GL11.glVertex2f(pos.x + dim.x, pos.y);
		GL11.glVertex2f(pos.x + dim.x, pos.y + dim.y);
		GL11.glVertex2f(pos.x, pos.y + dim.y);

		GL11.glEnd();

		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawLineRect(Vector2 pos, Vector2 dim, float red,
			float green, float blue, float alpha) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_LINE_STRIP);

		GL11.glVertex2f(pos.x, pos.y);
		GL11.glVertex2f(pos.x + dim.x, pos.y);
		GL11.glVertex2f(pos.x + dim.x, pos.y + dim.y);
		GL11.glVertex2f(pos.x, pos.y + dim.y);
		GL11.glVertex2f(pos.x, pos.y);

		GL11.glEnd();

		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawCrossRect(Vector2 pos, Vector2 dim, float red,
			float green, float blue, float alpha) {
		drawLineRect(pos, dim, red, green, blue, alpha);
		drawLine(pos, pos.add(dim), red, green, blue, alpha);
		drawLine(new Vector2(pos.x + dim.x, pos.y), new Vector2(pos.x, pos.y
				+ dim.y), red, green, blue, alpha);
	}

	public static void drawCrossRect(Vector2 pos, Vector2 dim, int color,
			float alpha) {
		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		drawCrossRect(pos, dim, r, g, b, alpha);
	}

	public static void drawLineRect(Vector2 pos, Vector2 dim, int color,
			float alpha) {
		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		drawLineRect(pos, dim, r, g, b, alpha);
	}

	public static void drawLine(Vector2 vec1, Vector2 vec2, float red,
			float green, float blue, float alpha) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);

		GL11.glBegin(GL11.GL_LINES);

		GL11.glVertex2f(vec1.x, vec1.y);
		GL11.glVertex2f(vec2.x, vec2.y);

		GL11.glEnd();

		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawLine(Vector2 vec1, Vector2 vec2, int color,
			float alpha) {
		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		drawLine(vec1, vec2, r, g, b, alpha);
	}

	public static void drawCircleHollow(Vector2 center, float radius,
			int sides, float red, float green, float blue, float alpha) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_LINE_STRIP);

		float degree = 0;
		for (int i = 0; i < sides + 1; i++) {
			float degInRad = degree * (3.14159f / 180f);
			double x = Math.cos(degInRad) * radius + center.x;
			double y = Math.sin(degInRad) * radius + center.y;
			GL11.glVertex2d(x, y);
			degree += 360f / sides;
			if (degree >= 360)// this finishes the circle
				degree = 0;
		}

		GL11.glEnd();
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawCircleHollow(Vector2 center, float radius,
			int sides, int color, float alpha) {
		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		drawCircleHollow(center, radius, sides, r, g, b, alpha);
	}

	public static void drawCircleOpaque(Vector2 center, float radius,
			int sides, float red, float green, float blue, float alpha) {
		if (isWireframe()) {
			drawCircleHollow(center, radius, sides, red, green, blue, alpha);
			return;
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);

		GL11.glVertex2f(center.x, center.y);
		float degree = 0;
		for (int i = 0; i < sides + 1; i++) {
			float degInRad = degree * (3.14159f / 180f);
			double x = Math.cos(degInRad) * radius + center.x;
			double y = Math.sin(degInRad) * radius + center.y;
			GL11.glVertex2d(x, y);

			degree += 360f / sides;
			if (degree >= 360)// this finishes the circle
				degree = 0;
		}

		GL11.glEnd();
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawCircleOpaque(Vector2 center, float radius,
			int sides, int color, float alpha) {
		float r = ((color >> 16) & 0xff) / 255f;
		float g = ((color >> 8) & 0xff) / 255f;
		float b = (color & 0xff) / 255f;
		drawCircleOpaque(center, radius, sides, r, g, b, alpha);
	}

	public static void startScissor(Vector2 pos, Vector2 dim) {
		Vector2 newPos = pos.copy();
		ColliderBox screenPos = DisplayHandler.getScreenRenderArea();
		newPos.y += dim.y;// bottom left
		newPos.y = Gui.screenHeight() - newPos.y;// translate into GL
													// coordinates
		newPos = newPos.add(screenPos.pos);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int) newPos.x, (int) newPos.y, (int) dim.x, (int) dim.y);
	}

	public static void endScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public static void setWireframe(boolean wireframe) {
		Renderer.wireframe = wireframe;
	}

	public static boolean isWireframe() {
		return wireframe;
	}
}

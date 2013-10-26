package com.remote.remote2d.engine.art;

import java.nio.FloatBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.gui.Gui;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;

/**
 * A universal renderer class.  This is used to abstract out all of the annoying GL11 calls, and consolidates them into one line.
 * This is also useful to do any widespread changes to the render system (switching core libraries, or OpenGL versions).
 * 
 * TODO: Update from old OpenGL to new OpenGL.
 * @author Flafla2
 *
 */
public class Renderer {
	
	private static boolean wireframe = false;
	private static Stack<Matrix4f> matrixStack = new Stack<Matrix4f>();
	
	static
	{
		matrixStack.push(new Matrix4f());
	}
	
	/**
	 * Draws an approximation of a circle out of lines (Wireframe) with the given amount of sides.
	 * @param center Center of the circle
	 * @param radius Radius of the circle
	 * @param sides How many sides the circle has (infinite sides are obviously not possible)
	 * @param red Red value, (0.0-1.0)
	 * @param green Green value, (0.0-1.0)
	 * @param blue Blue value, (0.0-1.0)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawCircleHollow(Vector2, float, int, int, float)
	 */
	public static void drawCircleHollow(Vector2 center, float radius, int sides, float red, float green, float blue, float alpha)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		
		float degree = 0;
		for (int i=0; i < sides+1; i++)
		{
			float degInRad = degree*(3.14159f/180f);
			double x = Math.cos(degInRad)*radius+center.x;
			double y = Math.sin(degInRad)*radius+center.y;
			GL11.glVertex2d(x, y);
			degree += 360f/sides;
			if(degree >= 360)//this finishes the circle
				degree = 0;
		}
		
		GL11.glEnd();
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	/**
	 * Draws an approximation of a circle out of lines (Wireframe) with the given amount of sides.
	 * @param center Center of the circle
	 * @param radius Radius of the circle
	 * @param sides How many sides the circle has (infinite sides are obviously not possible)
	 * @param color Color in hex (0x000000-0xffffff)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawCircleHollow(Vector2, float, int, float, float, float, float)
	 */
	public static void drawCircleHollow(Vector2 center, float radius, int sides, int color, float alpha) {
		float r = ((color >> 16) & 0xff)/255f;
		float g = ((color >> 8) & 0xff)/255f;
		float b = (color & 0xff)/255f;
		drawCircleHollow(center,radius,sides,r,g,b,alpha);
	}
	
	/**
	 * Draws an approximation of a circle with the given amount of sides.
	 * @param center Center of the circle
	 * @param radius Radius of the circle
	 * @param sides How many sides the circle has (infinite sides are obviously not possible)
	 * @param red Red value, (0.0-1.0)
	 * @param green Green value, (0.0-1.0)
	 * @param blue Blue value, (0.0-1.0)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawCircleOpaque(Vector2, float, int, int, float)
	 */
	public static void drawCircleOpaque(Vector2 center, float radius, int sides, float red, float green, float blue, float alpha)
	{
		if(isWireframe())
		{
			drawCircleHollow(center,radius,sides,red,green,blue,alpha);
			return;
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		
		GL11.glVertex2f(center.x,center.y);
		float degree = 0;
		for (int i=0; i < sides+1; i++)
		{
			float degInRad = degree*(3.14159f/180f);
			double x = Math.cos(degInRad)*radius+center.x;
			double y = Math.sin(degInRad)*radius+center.y;
			GL11.glVertex2d(x, y);
			
			degree += 360f/sides;
			if(degree >= 360)//this finishes the circle
				degree = 0;
		}
		
		GL11.glEnd();
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	/**
	 * Draws an approximation of a circle with the given amount of sides.
	 * @param center Center of the circle
	 * @param radius Radius of the circle
	 * @param sides How many sides the circle has (infinite sides are obviously not possible)
	 * @param color Color in hex (0x000000-0xffffff)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawCircleOpaque(Vector2, float, int, float, float, float, float)
	 */
	public static void drawCircleOpaque(Vector2 center, float radius, int sides, int color, float alpha) {
		float r = ((color >> 16) & 0xff)/255f;
		float g = ((color >> 8) & 0xff)/255f;
		float b = (color & 0xff)/255f;
		drawCircleOpaque(center,radius,sides,r,g,b,alpha);
	}
	
	/**
	 * Draws a rectangle with an X inside of it.  Mostly Used for debugging
	 * purposes.
	 * @param pos Top left corner of the rectangle
	 * @param dim Dimensions of the rectangle
	 * @param red Red value, (0.0-1.0)
	 * @param green Green value, (0.0-1.0)
	 * @param blue Blue value, (0.0-1.0)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawCrossRect(Vector2, Vector2, int, float)
	 */
	public static void drawCrossRect(Vector2 pos, Vector2 dim, float red, float green, float blue, float alpha)
	{
		drawLineRect(pos,dim,red,green,blue,alpha);
		drawLine(pos,pos.add(dim),red,green,blue,alpha);
		drawLine(new Vector2(pos.x+dim.x,pos.y),new Vector2(pos.x,pos.y+dim.y),red,green,blue,alpha);
	}
	
	/**
	 * Draws a rectangle with an X inside of it.  Mostly Used for debugging
	 * purposes.
	 * @param pos Top left corner of the rectangle
	 * @param dim Dimensions of the rectangle
	 * @param color Color in hex (0x000000-0xffffff)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawCrossRect(Vector2, Vector2, float, float, float, float)
	 */
	public static void drawCrossRect(Vector2 pos, Vector2 dim, int color, float alpha) {
		float r = ((color >> 16) & 0xff)/255f;
		float g = ((color >> 8) & 0xff)/255f;
		float b = (color & 0xff)/255f;
		drawCrossRect(pos,dim,r,g,b,alpha);
	}
	
	/**
	 * Draws a simple line between two points.
	 * @param vec1 First point of the line
	 * @param vec2 Second point of the line
	 * @param red Red value, (0.0-1.0)
	 * @param green Green value, (0.0-1.0)
	 * @param blue Blue value, (0.0-1.0)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawLine(Vector2, Vector2, int, float)
	 */
	public static void drawLine(Vector2 vec1, Vector2 vec2, float red, float green, float blue, float alpha)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		
		GL11.glBegin(GL11.GL_LINES);
		
		GL11.glVertex2f(vec1.x, vec1.y);
		GL11.glVertex2f(vec2.x, vec2.y);
		
		GL11.glEnd();
		
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	/**
	 * Draws a simple line between two points.
	 * @param vec1 First point of the line
	 * @param vec2 Second point of the line
	 * @param color Color in hex (0x000000-0xffffff)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawLine(Vector2, Vector2, float, float, float, float)
	 */
	public static void drawLine(Vector2 vec1, Vector2 vec2, int color, float alpha) {
		float r = ((color >> 16) & 0xff)/255f;
		float g = ((color >> 8) & 0xff)/255f;
		float b = (color & 0xff)/255f;
		drawLine(vec1,vec2,r,g,b,alpha);
	}
	
	/**
	 * Draws a polygon out of lines (wireframe) using the given coordinates.
	 * The polygon must be convex.  Note that this is relatively very slow
	 * compared to quads/triangles due to the fact that it uses
	 * {@link org.lwjgl.opengl.GL11#GL_POLYGON}.
	 * 
	 * @param vectors Each vertex of the polygon, in a <b>clockwise</b> direction.
	 * @param red Red value, (0.0-1.0)
	 * @param green Green value, (0.0-1.0)
	 * @param blue Blue value, (0.0-1.0)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawPoly(Vector2[], Vector2[], Texture, float, float, float, float)
	 * @see #drawPoly(Vector2[], float, float, float, float)
	 * @see #drawLinePoly(Vector2[], int, float)
	 */
	public static void drawLinePoly(Vector2[] vectors, float red, float green, float blue, float alpha)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		
		for(int x=0;x<vectors.length;x++)
			GL11.glVertex2f(vectors[x].x, vectors[x].y);
		
		GL11.glVertex2f(vectors[0].x, vectors[0].y);
		
		GL11.glEnd();
		
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	/**
	 * Draws a polygon out of lines (wireframe) using the given coordinates.
	 * The polygon must be convex.  Note that this is relatively very slow
	 * compared to quads/triangles due to the fact that it uses
	 * {@link org.lwjgl.opengl.GL11#GL_POLYGON}.
	 * 
	 * @param verts Each vertex of the polygon, in a <b>clockwise</b> direction.
	 * @param color Color in hex (0x000000-0xffffff)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawPoly(Vector2[], Vector2[], Texture, float, float, float, float)
	 * @see #drawPoly(Vector2[], float, float, float, float)
	 * @see #drawLinePoly(Vector2[], int, float)
	 */
	public static void drawLinePoly(Vector2[] verts, int color, float alpha) {
		float r = ((color >> 16) & 0xff)/255f;
		float g = ((color >> 8) & 0xff)/255f;
		float b = (color & 0xff)/255f;
		drawLinePoly(verts,r,g,b,alpha);
	}
	
	/**
	 * Draws a rectangle out of lines (wireframe).
	 * @param pos Top left corner of the rectangle
	 * @param dim Dimensions of the rectangle
	 * @param red Red value, (0.0-1.0)
	 * @param green Green value, (0.0-1.0)
	 * @param blue Blue value, (0.0-1.0)
	 * @param alpha Alpha (opaque) value, (0.0-1.0).
	 * @see #drawLineRect(Vector2, Vector2, int, float)
	 */
	public static void drawLineRect(Vector2 pos, Vector2 dim, float red, float green, float blue, float alpha)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		
		GL11.glVertex2f(pos.x, pos.y);
		GL11.glVertex2f(pos.x+dim.x, pos.y);
		GL11.glVertex2f(pos.x+dim.x, pos.y+dim.y);
		GL11.glVertex2f(pos.x, pos.y+dim.y);
		GL11.glVertex2f(pos.x, pos.y);
		
		GL11.glEnd();
		
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	/**
	 * Draws a rectangle out of lines (wireframe).
	 * @param pos Top left corner of the rectangle
	 * @param dim Dimensions of the rectangle
	 * @param color Color in hex (0x000000-0xffffff)
	 * @param alpha Alpha (opaque) value, (0.0-1.0).
	 * @see #drawLineRect(Vector2, Vector2, float, float, float, float)
	 */
	public static void drawLineRect(Vector2 pos, Vector2 dim, int color, float alpha) {
		float r = ((color >> 16) & 0xff)/255f;
		float g = ((color >> 8) & 0xff)/255f;
		float b = (color & 0xff)/255f;
		drawLineRect(pos,dim,r,g,b,alpha);
	}
	
	/**
	 * Draws a polygon using the given coordinates.  The polygon must be convex.
	 * Note that this is relatively very slow compared to quads/triangles due to
	 * the fact that it uses {@link org.lwjgl.opengl.GL11#GL_POLYGON}.
	 * 
	 * @param vectors Each vertex of the polygon, in a <b>clockwise</b> direction.
	 * @param red Red value, (0.0-1.0)
	 * @param green Green value, (0.0-1.0)
	 * @param blue Blue value, (0.0-1.0)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawPoly(Vector2[], Vector2[], Texture, float, float, float, float)
	 * @see #drawLinePoly(Vector2[], int, float)
	 * @see #drawLinePoly(Vector2[], float, float, float, float)
	 */
	public static void drawPoly(Vector2[] vectors, float red, float green, float blue, float alpha)
	{
		if(isWireframe())
		{
			drawLinePoly(vectors,red,green,blue,alpha);
			return;
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_POLYGON);
		
		for(int x=0;x<vectors.length;x++)
			GL11.glVertex2f(vectors[x].x, vectors[x].y);
		
		GL11.glEnd();
		
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	/**
	 * Draws a polygon using the given coordinates.  The polygon must be convex.
	 * Note that this is relatively very slow compared to quads/triangles due to
	 * the fact that it uses {@link org.lwjgl.opengl.GL11#GL_POLYGON}.
	 * 
	 * @param vectors Each vertex of the polygon, in a <b>clockwise</b> direction.
	 * @param uv UV coordinates of each vertex
	 * @param tex Texture to use
	 * @param red Red value, (0.0-1.0)
	 * @param green Green value, (0.0-1.0)
	 * @param blue Blue value, (0.0-1.0)
	 * @param alpha Alpha (opaque) value, (0.0-1.0)
	 * @see #drawPoly(Vector2[], float, float, float, float)
	 * @see #drawLinePoly(Vector2[], int, float)
	 * @see #drawLinePoly(Vector2[], float, float, float, float)
	 */
	public static void drawPoly(Vector2[] vectors, Vector2[] uv, Texture tex, float red, float green, float blue, float alpha)
	{
		if(isWireframe())
		{
			drawLinePoly(vectors,red,green,blue,alpha);
			return;
		}
		tex.bind();
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_POLYGON);
		
		for(int x=0;x<vectors.length;x++)
		{
			GL11.glTexCoord2f(uv[x].x,uv[x].y);
			GL11.glVertex2f(vectors[x].x, vectors[x].y);
		}
		
		GL11.glEnd();
		
		GL11.glColor3f(1, 1, 1);
	}
	
	/**
	 * Draws a rectangle.
	 * @param pos Top left corner of the rectangle
	 * @param dim Dimensions of the rectangle
	 * @param red Red value, (0.0-1.0)
	 * @param green Green value, (0.0-1.0)
	 * @param blue Blue value, (0.0-1.0)
	 * @param alpha Alpha (opaque) value, (0.0-1.0).
	 * @see #drawRect(Vector2, Vector2, int, float)
	 * @see #drawRect(Vector2, Vector2, Texture, int, float)
	 * @see #drawRect(Vector2, Vector2, Texture, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Vector2, Vector2, Texture, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Vector2, Vector2, Texture, int, float)
	 */
	public static void drawRect(Vector2 pos, Vector2 dim, float red, float green, float blue, float alpha)
	{
		if(isWireframe())
		{
			drawCrossRect(pos,dim,red,green,blue,alpha);
			return;
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glVertex2f(pos.x, pos.y);
		GL11.glVertex2f(pos.x+dim.x, pos.y);
		GL11.glVertex2f(pos.x+dim.x, pos.y+dim.y);
		GL11.glVertex2f(pos.x, pos.y+dim.y);
		
		GL11.glEnd();
		
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	/**
	 * Draws a rectangle.
	 * @param pos Top left corner of the rectangle
	 * @param dim Dimensions of the rectangle
	 * @param color Color in hex (0x000000-0xffffff)
	 * @param alpha Alpha (opaque) value, (0.0-1.0).
	 * @see #drawRect(Vector2, Vector2, Texture, int, float)
	 * @see #drawRect(Vector2, Vector2, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Texture, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Vector2, Vector2, Texture, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Vector2, Vector2, Texture, int, float)
	 */
	public static void drawRect(Vector2 pos, Vector2 dim, int color, float alpha)
	{
		float r = ((color >> 16) & 0xff)/255f;
		float g = ((color >> 8) & 0xff)/255f;
		float b = (color & 0xff)/255f;
		drawRect(pos,dim,r,g,b,alpha);
	}
	
	/**
	 * Draws a rectangle.
	 * @param pos Top left corner of the rectangle
	 * @param dim Dimensions of the rectangle
	 * @param tex Texture to use
	 * @param red Red value, (0.0-1.0)
	 * @param green Green value, (0.0-1.0)
	 * @param blue Blue value, (0.0-1.0)
	 * @param alpha Alpha (opaque) value, (0.0-1.0).
	 * @see #drawRect(Vector2, Vector2, int, float)
	 * @see #drawRect(Vector2, Vector2, Texture, int, float)
	 * @see #drawRect(Vector2, Vector2, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Vector2, Vector2, Texture, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Vector2, Vector2, Texture, int, float)
	 */
	public static void drawRect(Vector2 pos, Vector2 dim, Texture tex, float red, float green, float blue, float alpha)
	{
		drawRect(pos,dim,new Vector2(0,0),new Vector2(1,1),tex,red,green,blue,alpha);
	}
	
	/**
	 * Draws a rectangle.
	 * @param pos Top left corner of the rectangle
	 * @param dim Dimensions of the rectangle
	 * @param color Color in hex
	 * @param alpha Alpha (opaque) value, (0.0-1.0).
	 * @see #drawRect(Vector2, Vector2, int, float)
	 * @see #drawRect(Vector2, Vector2, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Texture, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Vector2, Vector2, Texture, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Vector2, Vector2, Texture, int, float)
	 */
	public static void drawRect(Vector2 pos, Vector2 dim, Texture tex, int color, float alpha)
	{
		drawRect(pos,dim,new Vector2(0,0),new Vector2(1,1),tex,color,alpha);
	}
	
	/**
	 * Draws a rectangle.
	 * @param pos Top left corner of the rectangle
	 * @param dim Dimensions of the rectangle
	 * @param uvPos Top left corner of the UV coordinates (0.0-1.0) on X and Y axis
	 * @param uvDim Dimensions of the UV coordinates (0.0-1.0)
	 * @param tex Texture to use
	 * @param red Red value, (0.0-1.0)
	 * @param green Green value, (0.0-1.0)
	 * @param blue Blue value, (0.0-1.0)
	 * @param alpha Alpha (opaque) value, (0.0-1.0).
	 * @see #drawRect(Vector2, Vector2, int, float)
	 * @see #drawRect(Vector2, Vector2, Texture, int, float)
	 * @see #drawRect(Vector2, Vector2, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Texture, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Vector2, Vector2, Texture, int, float)
	 */
	public static void drawRect(Vector2 pos, Vector2 dim, Vector2 uvPos, Vector2 uvDim, Texture tex, float red, float green, float blue, float alpha)
	{
		if(isWireframe())
		{
			drawCrossRect(pos,dim,red,green,blue,alpha);
			return;
		}
		tex.bind();
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glTexCoord2f(uvPos.x, uvPos.y);
		GL11.glVertex2f(pos.x, pos.y);
		GL11.glTexCoord2f(uvPos.x+uvDim.x, uvPos.y);
		GL11.glVertex2f(pos.x+dim.x, pos.y);
		GL11.glTexCoord2f(uvPos.x+uvDim.x, uvPos.y+uvDim.y);
		GL11.glVertex2f(pos.x+dim.x, pos.y+dim.y);
		GL11.glTexCoord2f(uvPos.x, uvPos.y+uvDim.y);
		GL11.glVertex2f(pos.x, pos.y+dim.y);
		
		GL11.glEnd();
		
		GL11.glColor3f(1, 1, 1);
	}
	
	/**
	 * Draws a rectangle.
	 * @param pos Top left corner of the rectangle
	 * @param dim Dimensions of the rectangle
	 * @param uvPos Top left corner of the UV coordinates (0.0-1.0) on X and Y axis
	 * @param uvDim Dimensions of the UV coordinates (0.0-1.0)
	 * @param tex Texture to use
	 * @param color Color in hex (0x000000-0xffffff)
	 * @param alpha Alpha (opaque) value, (0.0-1.0).
	 * @see #drawRect(Vector2, Vector2, int, float)
	 * @see #drawRect(Vector2, Vector2, Texture, int, float)
	 * @see #drawRect(Vector2, Vector2, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Texture, float, float, float, float)
	 * @see #drawRect(Vector2, Vector2, Vector2, Vector2, Texture, float, float, float, float)
	 */
	public static void drawRect(Vector2 pos, Vector2 dim, Vector2 uvPos, Vector2 uvDim, Texture tex, int color, float alpha)
	{
		float r = ((color >> 16) & 0xff)/255f;
		float g = ((color >> 8) & 0xff)/255f;
		float b = (color & 0xff)/255f;
		drawRect(pos,dim,uvPos,uvDim,tex,r,g,b,alpha);
	}
	
	/**
	 * Ends any current scissor.  In its current this is identical to
	 * <code>GL11.glDisable(GL11.GL_SCISSOR_TEST);</code>, however it is
	 * reccommended to use this instead for more control over rendering.
	 */
	public static void endScissor()
	{
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	/**
	 * If the Renderer is currently in wireframe mode.
	 */
	public static boolean isWireframe() {
		return wireframe;
	}
	
	/**
	 * Toggles wireframe mode.  Note that this only works when using a
	 * render call from {@link Renderer}.
	 */
	public static void setWireframe(boolean wireframe)
	{
		Renderer.wireframe = wireframe;
	}
	
	/**
	 * Handles the use of {@link GL11#glScissor(int, int, int, int)} while taking into
	 * account platform independence features which would otherwise mess up glScissor.
	 * 
	 * @param pos Top left corner of scissor box
	 * @param dim Dimensions of scissor box
	 * @see org.lwjgl.opengl.GL11#glScissor(int, int, int, int)
	 */
	public static void startScissor(Vector2 pos, Vector2 dim)
	{
		Vector2 newPos = pos.copy();
		ColliderBox screenPos = Remote2D.displayHandler.getScreenRenderArea();
		newPos.y += dim.y;//bottom left
		newPos.y = Gui.screenHeight()-newPos.y;//translate into GL coordinates
		newPos = newPos.add(screenPos.pos);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int)newPos.x, (int)newPos.y, (int)dim.x, (int)dim.y);
	}
	
	private static void pushMatrixToGL()
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		matrixStack.peek().store(buffer);
		GL11.glLoadMatrix(buffer);
		buffer.clear();
		buffer = null;
	}
	
	public static void pushMatrix()
	{
		matrixStack.push(matrixStack.peek());
	}
	
	public static void popMatrix()
	{
		if(matrixStack.size() > 1)
		{
			matrixStack.pop();
			pushMatrixToGL();
		}
	}
	
	public static void rotate(float angle, boolean radians)
	{
		if(!radians)
		{
			angle *= Math.PI;
			angle /= 180;
		}
		matrixStack.peek().rotate(angle, new Vector3f(0,0,1));
		pushMatrixToGL();
	}
	
	public static void rotate(float angle)
	{
		rotate(angle,false);
	}
	
	public static void translate(Vector2 trans)
	{
		matrixStack.peek().translate(new Vector3f(trans.x,trans.y,0));
		pushMatrixToGL();
	}
	
	public static void scale(Vector2 scale)
	{
		matrixStack.peek().scale(new Vector3f(scale.x, scale.y, 1));
		pushMatrixToGL();
	}
	
	public static void scale(float scale)
	{
		matrixStack.peek().scale(new Vector3f(scale, scale, 1));
		pushMatrixToGL();
	}
	
	public static void loadIdentity()
	{
		matrixStack.peek().setIdentity();
	}
	
	public static void clear()
	{
		matrixStack.clear();
		matrixStack.push(new Matrix4f());
	}

	public static Matrix4f peekMatrix() {
		return matrixStack.peek();
	}
	
	public static Vector2 matrixMultiply(Vector2 vec, Matrix4f matrix)
	{
		Vector4f lwjglVec = new Vector4f(vec.x,vec.y,0,0);
		Vector4f retVec = Matrix4f.transform(matrix, lwjglVec, null);
		return new Vector2(retVec.x, retVec.y);
	}
	
	public static Vector2 matrixMultiply(Vector2 vec)
	{
		return matrixMultiply(vec,matrixStack.peek());
	}
}

package com.remote.remote2d.editor.inspector;

import java.awt.Color;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.art.Animation;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Material;
import com.remote.remote2d.engine.art.Material.RenderType;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorInspectorSectionSetMaterial extends GuiEditorInspectorSectionSet {
	private Vector2[] renderTypePos;
	private Vector2 renderTypeDim;
	private RenderType renderType = RenderType.SOLID;
	private final int selectedColor = 0xcccccc;

	public GuiEditorInspectorSectionSetMaterial(String name, GuiEditor inspector, Vector2 pos, int width) {
		super(name, inspector, pos, width, new String[]{"color","alpha","UVPos","UVDim","texture","animation","linearScaling","repeat"}, new Class[]{Color.class,Float.class,Vector2.class,Vector2.class,Texture.class,Animation.class,Boolean.class,Boolean.class});
		renderTypePos = new Vector2[3];
		int typedimx = (width-20)/3;
		renderTypeDim = new Vector2(typedimx,20);
		renderTypePos[0] = new Vector2(pos.x+10,pos.y+sectionHeight()-20);
		renderTypePos[1] = new Vector2(pos.x+10+typedimx,pos.y+sectionHeight()-20);
		renderTypePos[2] = new Vector2(pos.x+10+typedimx*2,pos.y+sectionHeight()-20);
	}
	
	@Override
	public int sectionHeight()
	{
		return super.sectionHeight()+20;
	}

	@Override
	public Object getData() {
		Material mat = new Material(renderType,(Texture)getDataWithName("texture"),(Animation)getDataWithName("animation"),((Color)getDataWithName("color")).getRGB(),(Float)getDataWithName("alpha"));
		mat.linearScaling = (Boolean)getDataWithName("linearScaling");
		mat.repeat = (Boolean)getDataWithName("repeat");
		mat.setUVPos((Vector2)getDataWithName("UVPos"));
		mat.setUVDim((Vector2)getDataWithName("UVDim"));
		return mat;
	}
	
	@Override
	public void tick(int i, int j, int k)
	{
		super.tick(i, j, k);
		Vector2 mouse = new Vector2(i,j);
		boolean tex = renderTypePos[0].getColliderWithDim(renderTypeDim).isPointInside(mouse);
		boolean anim = renderTypePos[1].getColliderWithDim(renderTypeDim).isPointInside(mouse);
		boolean solid = renderTypePos[2].getColliderWithDim(renderTypeDim).isPointInside(mouse);
		if(k == 1 && tex)
			renderType = RenderType.TEX;
		else if(k == 1 && anim)
			renderType = RenderType.ANIM;
		else if(k == 1 && solid)
			renderType = RenderType.SOLID;
	}
	
	@Override
	public void render(float interpolation)
	{
		super.render(interpolation);
		if(renderType == RenderType.TEX)
		{
			Renderer.drawRect(renderTypePos[0], renderTypeDim, selectedColor, 1);
			int fontwidth = Fonts.get("Arial").getStringDim("TEX", 20)[0];
			Fonts.get("Arial").drawString("TEX", renderTypePos[0].x+renderTypeDim.x/2-fontwidth/2, renderTypePos[0].y, 20, 0x000000);
		}
		else
		{
			Renderer.drawLineRect(renderTypePos[0], renderTypeDim, selectedColor, 1);
			int fontwidth = Fonts.get("Arial").getStringDim("TEX", 20)[0];
			Fonts.get("Arial").drawString("TEX", renderTypePos[0].x+renderTypeDim.x/2-fontwidth/2, renderTypePos[0].y, 20, selectedColor);
		}
		
		if(renderType == RenderType.ANIM)
		{
			Renderer.drawRect(renderTypePos[1], renderTypeDim, selectedColor, 1);
			int fontwidth = Fonts.get("Arial").getStringDim("ANIM", 20)[0];
			Fonts.get("Arial").drawString("ANIM", renderTypePos[1].x+renderTypeDim.x/2-fontwidth/2, renderTypePos[1].y, 20, 0x000000);
		}
		else
		{
			Renderer.drawLineRect(renderTypePos[1], renderTypeDim, selectedColor, 1);
			int fontwidth = Fonts.get("Arial").getStringDim("ANIM", 20)[0];
			Fonts.get("Arial").drawString("ANIM", renderTypePos[1].x+renderTypeDim.x/2-fontwidth/2, renderTypePos[1].y, 20, selectedColor);
		}
		
		if(renderType == RenderType.SOLID)
		{
			Renderer.drawRect(renderTypePos[2], renderTypeDim, selectedColor, 1);
			int fontwidth = Fonts.get("Arial").getStringDim("SOLID", 20)[0];
			Fonts.get("Arial").drawString("SOLID", renderTypePos[2].x+renderTypeDim.x/2-fontwidth/2, renderTypePos[2].y, 20, 0x000000);
		}
		else
		{
			Renderer.drawLineRect(renderTypePos[2], renderTypeDim, selectedColor, 1);
			int fontwidth = Fonts.get("Arial").getStringDim("SOLID", 20)[0];
			Fonts.get("Arial").drawString("SOLID", renderTypePos[2].x+renderTypeDim.x/2-fontwidth/2, renderTypePos[2].y, 20, selectedColor);
		}
	}

	@Override
	public void setData(Object o) {
		
		if(!(o instanceof Material))
			return;
		
		Material mat = (Material)o;
		setDataWithName("color",new Color(mat.getColor()));
		setDataWithName("alpha",mat.getAlpha());
		setDataWithName("UVPos",mat.getUVPos());
		setDataWithName("UVDim",mat.getUVDim());
		setDataWithName("texture",mat.getTexture());
		setDataWithName("animation",mat.getAnimation());
		setDataWithName("repeat",mat.repeat);
		setDataWithName("linearScaling",mat.linearScaling);
		renderType = mat.getRenderType();
	}
	
	@Override
	public boolean isComplete() {
		return isComplete("color") && isComplete("alpha") && isComplete("UVPos") && isComplete("UVDim");
	}

}

package com.remote.remote2d.editor.inspector;

import org.lwjgl.input.Keyboard;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.gui.GuiTextField;
import com.remote.remote2d.engine.gui.TextLimiter;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorInspectorSectionVec2D extends GuiEditorInspectorSection {
	
	GuiTextField textField1;
	GuiTextField textField2;
	
	boolean link = false;
	float old1 = 0.0f;
	float old2 = 0.0f;
	

	public GuiEditorInspectorSectionVec2D(String name, GuiEditor inspector, Vector2 pos, int width) {
		super(name, inspector, pos, width);
		textField1 = new GuiTextField(pos.add(new Vector2(10,20)), new Vector2(width/2-10,20), 20);
		textField1.limitToDigits = TextLimiter.LIMIT_TO_FLOAT;
		
		textField2 = new GuiTextField(pos.add(new Vector2(width/2+10,20)), new Vector2(width/2-20,20), 20);
		textField2.limitToDigits = TextLimiter.LIMIT_TO_FLOAT;
	}

	@Override
	public int sectionHeight() {
		return 40;
	}

	@Override
	public Object getData() {
		return new Vector2(Float.parseFloat(textField1.text),Float.parseFloat(textField2.text));
	}

	@Override
	public void initSection() {
		
	}

	@Override
	public void tick(int i, int j, int k) {
		textField1.tick(i, j, k);
		textField2.tick(i, j, k);
		
		if(i > pos.x+width-20 && i < pos.x+width && j > pos.y && j < pos.y+20 && Remote2D.getInstance().hasMouseBeenPressed())
			link = !link;
		
		if(link)
		{
			if(textField1.hasTyped() && textField1.hasText() && textField2.hasText())
			{
				float new1 = Float.parseFloat(textField1.text);
				int x = (int)((new1*old2)/old1);
				textField2.text = ""+x;
				// old1 = new1
				// old2 = x
				// old1*x = new1*old2
				// x = (new1*old2)/old1
			}
			if(textField2.hasTyped() && textField1.hasText() && textField2.hasText())
			{
				float new2 = Float.parseFloat(textField2.text);
				int x = (int)((new2*old1)/old2);
				textField1.text = ""+x;
				// old1 = x
				// old2 = new2
				// old2*x = new2*old1
				// x = (new2*old1)/old2
			}
		}else
		{
			if(textField1.hasText() && !textField1.text.equals("-"))
				old1 = Float.parseFloat(textField1.text);
			if(textField2.hasText() && !textField2.text.equals("-"))
				old2 = Float.parseFloat(textField2.text);
		}
	}

	@Override
	public void render(float interpolation) {
		Fonts.get("Arial").drawString(renderName, pos.x, pos.y, 20, isComplete() ? 0xffffff : 0xff7777);
		textField1.render(interpolation);
		textField2.render(interpolation);
		
		Renderer.drawLineRect(new Vector2(pos.x+width-20,pos.y), new Vector2(20), 0xffffff, 1.0f);
		
		if(link)
		{
			Renderer.drawLine(new Vector2(pos.x+width-20,pos.y), new Vector2(pos.x+width,pos.y+20), 0xffffff, 1.0f);
			Renderer.drawLine(new Vector2(pos.x+width-20,pos.y+20), new Vector2(pos.x+width,pos.y), 0xffffff, 1.0f);
		}
	}
	
	@Override
	public void setData(Object o) {
		if(o instanceof Vector2)
		{
			textField1.text = ((Vector2)o).x+"";
			textField2.text = ((Vector2)o).y+"";
		}
	}
	
	@Override
	public void deselect() {
		textField1.deselect();
		textField2.deselect();
	}
	
	@Override
	public boolean isSelected() {
		return textField1.isSelected() || textField2.isSelected();
	}

	@Override
	public boolean isComplete() {
		return !textField1.text.trim().equals("") && !textField2.text.trim().equals("");
	}
	
	@Override
	public boolean hasFieldBeenChanged() {
		return (textField1.isSelected() || textField2.isSelected()) && isComplete() && Remote2D.getInstance().getIntegerKeyboardList().contains(Keyboard.KEY_RETURN);
	}

}

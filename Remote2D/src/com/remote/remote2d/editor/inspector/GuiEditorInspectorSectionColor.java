package com.remote.remote2d.editor.inspector;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.gui.GuiTextField;
import com.remote.remote2d.engine.gui.TextLimiter;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorInspectorSectionColor extends GuiEditorInspectorSection {
	
	GuiTextField textField;

	public GuiEditorInspectorSectionColor(String name, GuiEditor inspector, Vector2 pos, int width) {
		super(name, inspector, pos, width);
		textField = new GuiTextField(pos.add(new Vector2(10,20)), new Vector2(width-20,20), 20);
		textField.limitToDigits = TextLimiter.LIMIT_TO_HEX;
		textField.prefix = "0x";
		textField.text = "ffffff";
		textField.maxLength = 6;
	}

	@Override
	public int sectionHeight() {
		return 40;
	}

	@Override
	public Object getData() {
		return new Color(Integer.parseInt(textField.text, 16));
	}

	@Override
	public void initSection() {
		
	}

	@Override
	public void tick(int i, int j, int k) {
		textField.tick(i, j, k);
	}

	@Override
	public void render(float interpolation) {
		Fonts.get("Arial").drawString(renderName, pos.x, pos.y, 20, isComplete() ? 0xffffff : 0xff7777);
		textField.render(interpolation);
	}
	
	@Override
	public void setData(Object o) {
		if(o != null && (o instanceof Color))
		{
			textField.text = Integer.toHexString(((Color)o).getRGB()).substring(2);
		}
	}
	
	@Override
	public void deselect() {
		textField.deselect();
	}
	
	@Override
	public boolean isSelected() {
		return textField.isSelected();
	}

	@Override
	public boolean isComplete() {
		return textField.text.length() == 6;
	}
	
	@Override
	public boolean hasFieldBeenChanged() {
		return textField.isSelected() && isComplete() && Remote2D.getIntegerKeyboardList().contains(Keyboard.KEY_RETURN);
	}

}

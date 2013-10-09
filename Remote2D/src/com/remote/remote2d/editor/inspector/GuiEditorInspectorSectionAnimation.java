package com.remote.remote2d.editor.inspector;

import org.lwjgl.input.Keyboard;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.editor.DraggableObject;
import com.remote.remote2d.editor.DraggableObjectFile;
import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Animation;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.gui.GuiTextField;
import com.remote.remote2d.engine.gui.TextLimiter;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorInspectorSectionAnimation extends GuiEditorInspectorSection {
	
	GuiTextField textField;

	public GuiEditorInspectorSectionAnimation(String name, GuiEditor inspector, Vector2 pos, int width) {
		super(name, inspector, pos, width);
		textField = new GuiTextField(pos.add(new Vector2(10,20)), new Vector2(width-20,20), 20);
	}

	@Override
	public int sectionHeight() {
		return 40;
	}

	@Override
	public Object getData() {
		if(Remote2D.getInstance().artLoader.R2DExists(textField.text))
			return Remote2D.getInstance().artLoader.getAnimation(textField.text);
		return null;
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
		if(o instanceof Animation)
		{
			textField.text = ((Animation)o).getPath();
		} else if(o instanceof String)
			if(Remote2D.getInstance().artLoader.R2DExists((String)o))
				textField.text = (String)o;
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
		return Remote2D.getInstance().artLoader.R2DExists(textField.text);
	}

	@Override
	public boolean hasFieldBeenChanged() {
		return textField.isSelected() && isComplete() && Remote2D.getInstance().getIntegerKeyboardList().contains(Keyboard.KEY_RETURN);
	}
	
	public boolean acceptsDraggableObject(DraggableObject object)
	{
		if(object instanceof DraggableObjectFile)
		{
			DraggableObjectFile fileobj = ((DraggableObjectFile)object);
			if(fileobj.file != null)
			{
				if(fileobj.file.getName().endsWith(Animation.getExtension()))
					return true;
			}
		}
		return false;
	}
	
	public void acceptDraggableObject(DraggableObject object)
	{
		if(object instanceof DraggableObjectFile)
		{
			DraggableObjectFile fileobj = ((DraggableObjectFile)object);
			if(fileobj.file != null)
			{
				if(fileobj.file.getName().endsWith(Animation.getExtension()))
				{
					textField.text = fileobj.file.getPath();
					if(textField.text.startsWith(Remote2D.getJarPath().getAbsolutePath()))
						textField.text = textField.text.substring((int) Remote2D.getJarPath().getAbsolutePath().length());
					textField.text.replace('\\', '/');
				}
			}
		}
	}

}

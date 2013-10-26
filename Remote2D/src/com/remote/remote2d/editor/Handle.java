package com.remote.remote2d.editor;

public abstract class Handle {
	private String entityUUID;
	protected GuiEditor editor;
	
	public Handle(GuiEditor editor, String uuid)
	{
		this.editor = editor;
		setEntityUUID(uuid);
	}
	
	public abstract void tick(int i, int j, int k);
	public abstract void render(float interpolation);

	public String getEntityUUID() {
		return entityUUID;
	}

	public void setEntityUUID(String entityUUID) {
		this.entityUUID = entityUUID;
	}
	
	public abstract boolean isSelected();
}

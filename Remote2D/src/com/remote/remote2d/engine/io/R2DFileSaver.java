package com.remote.remote2d.engine.io;

public interface R2DFileSaver {
	
	/**
	 * Called when something attempts to save this R2DFileSaver to a file.  
	 * @param collection R2DTypeCollection to save to.
	 * @see com.remote.remote2d.engine.io.R2DType
	 * @see com.remote.remote2d.engine.io.R2DTypeCollection
	 */
	public void saveR2DFile(R2DTypeCollection collection);
	/**
	 * Called when something attempts to load this R2DFileSaver from a file.  
	 * @param collection R2DTypeCollection to load from.
	 * @see com.remote.remote2d.engine.io.R2DType
	 * @see com.remote.remote2d.engine.io.R2DTypeCollection
	 */
	public void loadR2DFile(R2DTypeCollection collection);
	
}

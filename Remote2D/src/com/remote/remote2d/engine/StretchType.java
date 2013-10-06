package com.remote.remote2d.engine;

/**
 * The stretch type for this game.  The different stretch types work as follows:
 * <ul>
 * <li>{@link StretchType#STRETCH}: Stretches the target resolution to the window size.</li>
 * <li>{@link StretchType#SCALE}: Scales the target resolution proportionally, with black borders on the sides.  Some artifacts may be seen due to scaling errors.</li>
 * <li>{@link StretchType#MULTIPLES}: Scales the target resolution proportionally by multiples of 2, with black borders on the sides - great for pixel art.</li>
 * <li>{@link StretchType#NONE}: Completely ignores the target Window Size.  No resolution independence at all.</li>
 * </ul>
 */
public enum StretchType {
	
	NONE, SCALE, STRETCH, MULTIPLES;
	
}

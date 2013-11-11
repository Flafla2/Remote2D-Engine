package com.remote.remote2d.engine;

import java.io.File;
import java.io.IOException;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.io.R2DFileUtility;
import com.remote.remote2d.engine.logic.Vector2;

import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class AudioHandler {
	
	private static SoundSystem soundSystem;
	
	public static void init()
	{		
		try { SoundSystemConfig.setCodec("ogg",CodecJOgg.class); }
		catch(SoundSystemException e) { Log.error("Unable to register JOgg codec.  .ogg sounds will not work on this machine!"); }
		
		try { SoundSystemConfig.setCodec("wav",CodecWav.class); }
		catch(SoundSystemException e) { Log.error("Unable to register wav codec.  .wav sounds will not work on this machine!"); }
		
		boolean openal_compatible = SoundSystemConfig.libraryCompatible(LibraryLWJGLOpenAL.class);
		boolean javasound_compatible = SoundSystemConfig.libraryCompatible(LibraryJavaSound.class);
		
		try
		{
			if(openal_compatible)
				soundSystem = new SoundSystem(LibraryLWJGLOpenAL.class);
			else if(javasound_compatible)
				soundSystem = new SoundSystem(LibraryJavaSound.class);
			else
				soundSystem = new SoundSystem(Library.class);
		} catch(SoundSystemException e)
		{
			throw new Remote2DException(e,"Error initializing the soundsystem!");
		}
		
	}
	
	/**
	 * Plays the sound at the given location with the given parameters.
	 * 
	 * @param localPath The local path of this sound.
	 * @param pos Position of the camera in world space
	 * @param priority If true, will not cut this sound off if too many sounds are trying to play at once (if possible).
	 * @param loop If true, loops this sound.
	 * @param attmodel The attenuation model of this sound. {@link SoundSystemConfig#ATTENUATION_ROLLOFF} for realistic fadeout due to distance, 
	 * {@link SoundSystemConfig#ATTENUATION_LINEAR} for less realistic linear fadeout, and {@link SoundSystemConfig#ATTENUATION_NONE} 
	 * for no fadeout - sound will always play (ambient noise)
	 * @param distOrRoll The fadeout distance of this sound if using linear attenuation, or the rolloff factor when using rolloff attenuation
	 * 
	 * @see SoundSystemConfig
	 * 
	 * @return A unique identifier used to refer to this sound later
	 */
	public static String playSound(String localPath, Vector2 pos, boolean priority, boolean loop, int attmodel, float distOrRoll)
	{
		File f = R2DFileUtility.getResource(localPath);
		try {
			return soundSystem.quickPlay(priority, f.getCanonicalFile().toURI().toURL(), f.getName(), loop, pos.x, pos.y, 0, attmodel, distOrRoll);
		} catch (IOException e) {
			throw new Remote2DException(e,"Unable to create new audio source!");
		}
	}
	
	/**
	 * Plays the sound at the given location with the given parameters.  Uses {@link SoundSystemConfig} to decide the default attenuation
	 * and fade distance/rolloff.
	 * 
	 * @param localPath The local path of this sound.
	 * @param pos Position of the camera in world space
	 * @param priority If true, will not cut this sound off if too many sounds are trying to play at once (if possible).
	 * @param loop If true, loops this sound.
	 * 
	 * @see SoundSystemConfig#getDefaultAttenuation()
	 * @see SoundSystemConfig#getDefaultFadeDistance()
	 * @see SoundSystemConfig#getDefaultRolloff()
	 * 
	 * @return A unique identifier used to refer to this sound later
	 */
	public static String playSound(String localPath, Vector2 pos, boolean priority, boolean loop)
	{
		float distOrRoll = 0;
		switch(SoundSystemConfig.getDefaultAttenuation())
		{
		case SoundSystemConfig.ATTENUATION_NONE:
			distOrRoll = 0;
			break;
		case SoundSystemConfig.ATTENUATION_LINEAR:
			distOrRoll = SoundSystemConfig.getDefaultFadeDistance();
			break;
		case SoundSystemConfig.ATTENUATION_ROLLOFF:
			distOrRoll = SoundSystemConfig.getDefaultRolloff();
			break;
		}
		return playSound(localPath, pos, priority, loop, SoundSystemConfig.getDefaultAttenuation(), distOrRoll);
	}
	
	/**
	 * Plays an ambient sound with the given parameters.  This means that the sound will not depend at all about the listener's
	 * position.  Useful for ambient sounds.
	 * 
	 * @param localPath The local path of this sound.
	 * @param priority If true, will not cut this sound off if too many sounds are trying to play at once (if possible).
	 * @param loop If true, loops this sound.
	 * 
	 * @return A unique identifier used to refer to this sound later
	 */
	public static String playSound(String localPath, boolean priority, boolean loop)
	{
		return playSound(localPath, new Vector2(0,0), priority, loop, SoundSystemConfig.ATTENUATION_NONE, 0);
	}
	
	/**
	 * Resumes a paused/stopped sound
	 * @param identifier Identifier passed by {@link #playSound(String, Vector2, boolean, boolean, int, float)} or similar playSound() method.
	 */
	public static void resume(String identifier)
	{
		soundSystem.play(identifier);
	}
	
	/**
	 * Pauses a sound
	 * @param identifier Identifier passed by {@link #playSound(String, Vector2, boolean, boolean, int, float)} or similar playSound() method.
	 */
	public static void pause(String identifier)
	{
		soundSystem.pause(identifier);
	}
	
	/**
	 * Stops a sound
	 * @param identifier Identifier passed by {@link #playSound(String, Vector2, boolean, boolean, int, float)} or similar playSound() method.
	 */
	public static void stop(String identifier)
	{
		soundSystem.stop(identifier);
	}
	
	/**
	 * Sets the volume of the specified sound
	 * @param identifier Identifier passed by {@link #playSound(String, Vector2, boolean, boolean, int, float)} or similar playSound() method.
	 * @param value How loud the sound should be (0.0-1.0)
	 */
	public static void setVolume(String identifier, float value)
	{
		soundSystem.setVolume(identifier, value);
	}
	
	/**
	 * Sets the master volume of the whole game
	 * @param value How loud the game should be (0.0-1.0)
	 */
	public static void setVolume(float value)
	{
		soundSystem.setMasterVolume(value);
	}
	
	/**
	 * The master volume of the game (0.0-1.0)
	 */
	public static float getVolume() {
		return soundSystem.getMasterVolume();
	}
	
	/**
	 * The volume of the specified sound
	 * @param identifier Identifier passed by {@link #playSound(String, Vector2, boolean, boolean, int, float)} or similar playSound() method.
	 */
	public static float getVolume(String identifier)
	{
		return soundSystem.getVolume(identifier);
	}
	
	/**
	 * Sets the position of the audio listener.  This obviously does not affect ambient sounds.
	 */
	public static void setListenerPos(Vector2 pos)
	{
		soundSystem.setListenerPosition(pos.x, pos.y, 0);
	}
	
	/**
	 * Sets the rotation of the audio listener.  
	 * @param pos
	 */
	public static void setListenerRot(Vector2 pos)
	{
		soundSystem.setListenerOrientation(pos.x, pos.y, 0, 0, 0, 1);
	}
	
	public static void shutDown()
	{
		soundSystem.cleanup();
	}
	
}

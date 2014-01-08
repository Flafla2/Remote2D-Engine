package com.remote.remote2d.extras.test.entity;

import org.lwjgl.input.Keyboard;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.art.Animation;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.entity.component.Component;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Vector2;
import com.remote.remote2d.engine.particles.ParticleSystem;

public class ComponentPlayer extends Component {
	
	//PUBLIC VARIABLES: Since this is a component these are editable within the editor
	public Animation idleAnimation;
	public Animation walkAnimation;
	public Animation jumpAnimation;
	public Animation fallAnimation;
	public Animation landAnimation;
	public Entity testEntity;
	public boolean spriteFacesRight = true;
	public boolean particleTest = false;
	
	//PRIVATE VARIABLES: These are NOT editable within the editor
	private PlayerState state = PlayerState.IDLE;
	private FacingState facing = FacingState.RIGHT;
	private long lastLand = 0;
	private long timerLength = -1;
	
	private ParticleSystem testParticles;
	
	private Vector2 velocity = new Vector2(0,0);		//Physics stuff - simple velocity and acceleration
	private Vector2 acceleration = new Vector2(0,2);
	private Vector2 maxVelocity = new Vector2(10,-1);	//Max Velocity is required because we aren't accounting for friction.

	@Override
	public void tick(int i, int j, int k) {
		PlayerState oldState = state;
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			if(state == PlayerState.IDLE)
				state = PlayerState.WALK;
			facing = FacingState.LEFT;
			acceleration.x = -2;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			if(state == PlayerState.IDLE)
				state = PlayerState.WALK;
			facing = FacingState.RIGHT;
			acceleration.x = 2;
		}
		
		if(!Keyboard.isKeyDown(Keyboard.KEY_A) && !Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			acceleration.x = 0;
			velocity.x = 0;
			if(state == PlayerState.WALK)
				state = PlayerState.IDLE;
		}
		velocity = velocity.add(acceleration);
		
		if(Math.abs(velocity.x) > maxVelocity.x && maxVelocity.x != -1)
		{
			if(velocity.x > 0)
				velocity.x = maxVelocity.x;
			else
				velocity.x = -maxVelocity.x;
		}
		if(Math.abs(velocity.y) > maxVelocity.y && maxVelocity.y != -1)
		{
			if(velocity.y > 0)
				velocity.y = maxVelocity.y;
			else
				velocity.y = -maxVelocity.y;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && (state == PlayerState.WALK || state == PlayerState.IDLE))
			velocity.y -= 20;
		
		Vector2 globalPos = entity.getPosGlobal();
		
		Vector2 correction = entity.getMap().getCorrection(globalPos.getColliderWithDim(entity.getDim()),new Vector2(velocity.getElements()));
		velocity = velocity.add(correction);
		
		//velocity = velocity.multiply(new Vector2DF(friction,friction));
		
		entity.pos = entity.pos.add(velocity);
		
		if(velocity.y > 0)
			state = PlayerState.FALL;
		else if(velocity.y < 0)
			state = PlayerState.JUMP;
		else if(state == PlayerState.FALL)
			state = PlayerState.LAND;
		//velocity.print();
		
		ColliderBox renderarea = entity.getMap().camera.getMapRenderArea();
		float right = renderarea.pos.x+renderarea.dim.x;
		float left = renderarea.pos.x;
		if(globalPos.x+entity.getDim().x > right)
			entity.getMap().camera.pos.x += (entity.pos.x+entity.getDim().x)-right;
		if(globalPos.x < left)
			entity.getMap().camera.pos.x -= left-entity.pos.x;
		
		float bottom = renderarea.pos.y+renderarea.dim.y;
		float top = renderarea.pos.y;
		if(globalPos.y+entity.getDim().y > bottom)
			entity.getMap().camera.pos.y += (globalPos.y+entity.getDim().y)-bottom;
		if(globalPos.y < top)
			entity.getMap().camera.pos.y -= top-globalPos.y;
		
		Animation anim = getAnim();
		
		if(state != oldState)
		{
			
			if(state == PlayerState.LAND && anim != null)
			{
				timerLength = (int) (anim.getFramelength()*anim.getFrames().x*anim.getFrames().y);
				lastLand = System.currentTimeMillis();
			}
				
		}
		
		testParticles.pos = entity.getMap().screenToWorldCoords(new Vector2(i,j));
		if(particleTest)
			testParticles.tick(false);
	}

	@Override
	public void renderBefore(boolean editor, float interpolation) {
		
	}

	@Override
	public void onEntitySpawn() {
		if(testEntity != null)
			Log.debug(testEntity.name);
		testParticles = new ParticleSystem(entity.getMap());
	}
	
	public Animation getAnim()
	{
		Animation anim = null;
		switch(state)
		{
		case IDLE:
			anim = idleAnimation;
			break;
		case WALK:
			anim = walkAnimation;
			break;
		case JUMP:
			anim = jumpAnimation;
			break;
		case FALL:
			anim = fallAnimation;
			break;
		case LAND:
			anim = landAnimation;
			break;
		}
		
		if(anim != null)
			anim.flippedX = spriteFacesRight ? (facing == FacingState.LEFT) : (facing == FacingState.RIGHT);
		return anim;
	}

	@Override
	public void renderAfter(boolean editor, float interpolation) {
		
		if(state == PlayerState.LAND && System.currentTimeMillis()-lastLand >= timerLength && timerLength != -1)
		{
			state = PlayerState.IDLE;
			timerLength = -1;
		}
		
		Animation anim = getAnim();
		if(anim != null)
		{
			Vector2 posVec = new Vector2(0,0);
			posVec.x = entity.getPosLocal(interpolation).x+entity.getDim().x/2-anim.getSpriteDim().x/2;
			posVec.y = entity.getPosLocal(interpolation).y+entity.getDim().y/2-anim.getSpriteDim().y/2;
			anim.render(posVec, new Vector2(anim.getSpriteDim().getElements()));
		}
		
		if(particleTest)
			testParticles.render();
	}

	@Override
	public void apply() {
		
	}
	
	enum PlayerState
	{
		IDLE, WALK, JUMP, FALL, LAND;
	}
	
	enum FacingState
	{
		LEFT, RIGHT;
	}

	@Override
	public void init() {
		testParticles = new ParticleSystem(entity.getMap());
	}

}

package gameObject.player;

import gameObject.GameObject;
import gameObject.enemy.Enemy;
import gameObject.player.InputHandler.Click;
import misc.GeometricObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

import core.ingame.Camera;
import core.ingame.GameProperties;

abstract class PlayerInteraction extends GameObject implements Detectable, RayCastCallback {

	private Enemy enemyGrab;
	private int shuriken = 10;
	private Click click;
	private Vector2 target;
	private InteractionState nextState, interruptedState;
	
	protected PlayerInteraction(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}
	
	protected void processInput() {
		
		boolean action = false;
		
		processHook();
		
//		CLICK 
		click = InputHandler.getInstance().getClick();
		if(actionTimer >= ACTION_TIMER_INITIAL && !isCrouching() && getInteractionState().isInterruptable()) {
			
			if(click != null) {
				normalizeClickPoint();
				if(InputHandler.getInstance().buttonDown(GameProperties.keyThrow))
					action = processThrow();
				else if(InputHandler.getInstance().buttonDown(GameProperties.keyHook))
					action = tryToHook();
				
				InputHandler.getInstance().resetClick();
			}
		} else
			actionTimer++;


		if(!action && isGrounded()) {
			
			if(InputHandler.getInstance().isKeyDown(GameProperties.keyAction))
				processAction();
			
//			JUMP
			else if(InputHandler.getInstance().isKeyDown(GameProperties.keyJump)) {
				nextState = InteractionState.JUMP;	
			} else if (isJumping() && isGrounded())
				nextState = getInteractionState().equals(InteractionState.JUMP) ? InteractionState.STAND : InteractionState.WALK;
			
//			CROUCH
			else if(InputHandler.getInstance().isKeyDown(GameProperties.keyCrouch)) {
				if(!isCrouching()) {
					if(getInteractionState().equals(InteractionState.STAND))
						nextState = InteractionState.CROUCH_DOWN;
					else 
						nextState = InteractionState.CROUCH_STAND;
				} else if(getInteractionState().equals(InteractionState.CROUCH_DOWN)
							&& isAnimationFinished())
					nextState = InteractionState.CROUCH_STAND;
			} else if(isCrouching())
				nextState = InteractionState.STAND;
		} 
		
		if(!action) {
			
			if (InputHandler.getInstance().isKeyDown(GameProperties.keyRight)
					|| InputHandler.getInstance().isKeyDown(GameProperties.keyLeft)) {
			
				setFlip(InputHandler.getInstance().isKeyDown(GameProperties.keyLeft));
				
				switch(getInteractionState()) {
				case CROUCH_DOWN : 
				case CROUCH_STAND :
					nextState = InteractionState.CROUCH_SNEAK;
					break;
				case GRAB :
					nextState = InteractionState.GRAB_PULL;
					break;
				case STAND :
					nextState = InteractionState.WALK; 
					break;
				case JUMP :
					nextState = InteractionState.JUMP_MOVE;
					break;
				default:
					break;
				}	
			} else {
				
				switch(getInteractionState()) {
				case RUN : 
				case WALK :
					nextState = InteractionState.STAND; 
					break;
				case CROUCH_SNEAK :
					nextState = InteractionState.CROUCH_STAND;
					break;
				case GRAB_PULL :
					nextState = InteractionState.GRAB;
					break;
				case JUMP_MOVE :
					nextState = InteractionState.JUMP;
					break;
				default:
					break;
				}		
			}
		}
		
		
		if(!action)
			processRun();
		
		if(interruptedState != null && isAnimationFinished()) {
			nextState = interruptedState;
			interruptedState = null;
		}
		
		applyState(nextState);
		
	}
	
	private void applyState(InteractionState state) {
		if(nextState == null)
			return;
		
		if(!nextState.equals(getInteractionState())) {
			setInteractionState(nextState);
			if(applyAnimation())
				nextState = null;
		} else
			nextState = null;
			
	}

	private final int TAP_TIMER_LIMIT_HIGH = 10,
			TAP_TIMER_LIMIT_LOW = 1;
	private int runTapTimer = 0;
	
	private boolean processRun() {
		
		switch(getInteractionState()) {
			case WALK:
				if(runTapTimer < TAP_TIMER_LIMIT_HIGH && runTapTimer > TAP_TIMER_LIMIT_LOW)
					nextState = InteractionState.RUN;
				else
					runTapTimer = 0;
				break;
			
			case STAND:
				if(runTapTimer < TAP_TIMER_LIMIT_HIGH)
					runTapTimer++;
				break;
				
			default:
				break;
		
		}
		
		return false;
	}
	

	private final int ACTION_TIMER_INITIAL = 50;
	private int actionTimer = ACTION_TIMER_INITIAL;
	
	private Vector2 startPoint, clickPoint;
	
	private void normalizeClickPoint() {
		startPoint = GameProperties.meterToPixel(this.getLocalCenterInWorld());
		clickPoint = new Vector2(click.screenX, click.screenY);
		Camera.getInstance().unproject(clickPoint);
		
		switch(GameProperties.debugMode) {
		case GEOMETRIC:
			new GeometricObject(new Circle(startPoint.x, startPoint.y, 5), Color.RED);
			new GeometricObject(new Circle(clickPoint.x, clickPoint.y, 10), Color.GREEN);
			break;
		case CONSOLE:
			System.out.println(startPoint.toString()+" -- "+clickPoint.toString());
			break;
		default:
			break;
		}
	}
	
	private boolean processThrow() {
		if(shuriken <= 0)
			return false;
	
		interruptedState = getInteractionState();
		applyState(nextState = InteractionState.THROW);
		
//		shuriken--;
		new Shuriken(this, clickPoint.sub(startPoint));
		actionTimer = 0;
		
		return true;
	}
	
	private final int HOOK_RADIUS = 400;
	
	private boolean tryToHook() {
		Vector2 endPoint = clickPoint.cpy();
		
		endPoint.sub(startPoint);
		endPoint.nor().scl(HOOK_RADIUS);
		endPoint.add(startPoint);
		
		body.getWorld().rayCast(this, getLocalCenterInWorld(), GameProperties.pixelToMeter(endPoint));
		
		actionTimer = 0;
		
		return true;
	}
	
	private void beginHook(Vector2 target) {
		nextState = InteractionState.HOOK;
		body.setGravityScale(0);
//		body.setTransform(target, body.getAngle());
		this.target = target;
	}
	
	private final int HOOK_TIME_LIMIT = 30;
	private int hookTime = 0;
	
	private void processHook() {
		if(target == null || !isHooking()) {
			return;
		}
		
		body.applyLinearImpulse(target.clamp(10, 15), getLocalCenterInWorld(), true);
		hookTime++;
		
		if(hookTime >= HOOK_TIME_LIMIT)
			resetHook();
	}
	
	private boolean processAction() {
		InputHandler.getInstance().keyUp(GameProperties.keyAction);
		
		return false;
	}

	
	
	@Override
	public boolean isDetectable(Enemy enemy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCaptured(Enemy enemy) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isCrouching() {
		return getInteractionState().equals(InteractionState.CROUCH_DOWN)
				|| getInteractionState().equals(InteractionState.CROUCH_STAND)
				|| getInteractionState().equals(InteractionState.CROUCH_SNEAK);
	}
	
	public boolean isHooking() 	{ 
		return getInteractionState().equals(InteractionState.HOOK);	
	}
	public boolean isRunning() 	{ 
		return getInteractionState().equals(InteractionState.RUN);	
	}
	
	public boolean isHiding() 	{ 
		return getInteractionState().equals(InteractionState.HIDE);	
	}
	
	public boolean isJumping() {
		return getInteractionState().equals(InteractionState.JUMP)
				|| getInteractionState().equals(InteractionState.JUMP_MOVE);
	}
	
	public boolean isGrabbing() { 
		return enemyGrab != null 
				&& (getInteractionState().equals(InteractionState.GRAB) 
						|| getInteractionState().equals(InteractionState.GRAB_PULL));	
	}
	
	public boolean setGrab(Enemy enemy) {
		if(!enemy.isStunned() && enemyGrab == null)
			return false;
		
		enemy.isCarriable(body.getPosition());
		this.enemyGrab = enemy;
		nextState = InteractionState.GRAB;
		return true;
	}
	
	public void addShuriken() {
		actionTimer = ACTION_TIMER_INITIAL;
		shuriken++;
	}
	
	public void setShuriken(int shurikens) {
		actionTimer = ACTION_TIMER_INITIAL;
		this.shuriken = shurikens;
	}
	
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
		System.out.println("---"+point.toString());
		if(((GameObject) fixture.getBody().getUserData()).getGameObjectType() == GameObjectTypes.GROUND) {
			
//			visualize target
			Vector2 p = GameProperties.meterToPixel(point);
			new GeometricObject(new Circle(p.x-5, p.y-5, 5), Color.BLUE);
		
			beginHook(point);
			
			return 0;
		} else
			return 1;
	}
	
	protected void resetHook() {
		target = null;
		hookTime = 0;
		body.setGravityScale(1);
		currentState = InteractionState.STAND;
		applyAnimation();
	}

}

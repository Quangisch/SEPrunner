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

abstract class PlayerInteraction extends PlayerCollision implements Detectable, RayCastCallback {

	private Enemy enemyGrab;
	private int shuriken = 10;
	private Click click;
	
	protected PlayerInteraction(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}
	
	protected void processInput() {
		
//		CLICK 
		click = InputHandler.getInstance().getClick();
		if(actionTimer >= ACTION_TIMER_INITIAL) {
			
			if(click != null) {
				normalizeClickPoint();
				if(InputHandler.getInstance().buttonDown(GameProperties.keyThrow))
						processThrow();
					
					if(InputHandler.getInstance().buttonDown(GameProperties.keyHook))
						processHook();
				
				InputHandler.getInstance().resetClick();
			}
		} else
			actionTimer++;


		if(isGrounded()) {
			
			if(InputHandler.getInstance().isKeyDown(GameProperties.keyAction))
				processAction();
			else if(InputHandler.getInstance().isKeyDown(GameProperties.keyJump))
				setInteractionState(InteractionState.JUMP);
			else if(InputHandler.getInstance().isKeyDown(GameProperties.keyCrouch))
				setInteractionState(InteractionState.CROUCH_STAND);
		}
		
		
		if (InputHandler.getInstance().isKeyDown(GameProperties.keyRight)
				|| InputHandler.getInstance().isKeyDown(GameProperties.keyLeft)) {
		

			setFlip(InputHandler.getInstance().isKeyDown(GameProperties.keyLeft));
			
			switch(getInteractionState()) {
			case CROUCH_STAND :
				setInteractionState(InteractionState.CROUCH_SNEAK);
				break;
			case GRAB :
				setInteractionState(InteractionState.GRAB_PULL);
				break;
			case STAND :
				setInteractionState(InteractionState.WALK); 
				break;
			case JUMP :
				setInteractionState(InteractionState.JUMP_MOVE);
				break;
			default:
				break;
			}	
			
		} else {
			
			switch(getInteractionState()) {
			case RUN : 
			case WALK :
				setInteractionState(InteractionState.STAND); 
				break;
			case CROUCH_SNEAK :
				setInteractionState(InteractionState.CROUCH_STAND);
				break;
			case GRAB_PULL :
				setInteractionState(InteractionState.GRAB);
				break;
			case JUMP_MOVE :
				setInteractionState(InteractionState.JUMP);
				break;
			default:
				break;
			}		
		}
		
		processRun();
		
	}
	


	private final int TAP_TIMER_LIMIT_HIGH = 10,
			TAP_TIMER_LIMIT_LOW = 3;
	private int runTapTimer = 0;
	
	private boolean processRun() {
		
		
		if(GameProperties.debugMode.equals(GameProperties.Debug.CONSOLE))
			System.out.println(runTapTimer);
		
		switch(getInteractionState()) {
			case WALK:
				if(runTapTimer < TAP_TIMER_LIMIT_HIGH && runTapTimer > TAP_TIMER_LIMIT_LOW)
					setInteractionState(InteractionState.RUN);
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
		
		setInteractionState(InteractionState.THROW);
		shuriken--;
		new Shuriken(this, clickPoint.sub(startPoint));
		actionTimer = 0;
		
		return true;
	}
	
	private final int HOOK_RADIUS = 300;
	
	private boolean processHook() {
		Vector2 endPoint = clickPoint.cpy();
		
		endPoint.sub(startPoint);
		endPoint.nor().scl(HOOK_RADIUS);
		endPoint.add(startPoint);
		
		body.getWorld().rayCast(this, getLocalCenterInWorld(), GameProperties.pixelToMeter(endPoint));
		
		actionTimer = 0;
		
		return true;
	}
	
	private void hook(Vector2 target) {
		
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
	
	public boolean isHooking() 	{ 
		return getInteractionState().equals(InteractionState.HOOK_FLY)
				|| getInteractionState().equals(InteractionState.HOOK_START);	
	}
	public boolean isRunning() 	{ 
		return getInteractionState().equals(InteractionState.RUN);	
	}
	
	public boolean isHiding() 	{ 
		return getInteractionState().equals(InteractionState.HIDE);	
	}
	
	public boolean isGrabbing() { 
		return enemyGrab != null 
				&& (getInteractionState().equals(InteractionState.GRAB) 
						|| getInteractionState().equals(InteractionState.GRAB_PULL));	
	}
	
	public void setHook(boolean hook) {
//		TODO
	}

	public void setRun(boolean run) {
//		TODO
	}

	public void setHide(boolean hide) {
//		TODO
	}
	
	public boolean setGrab(Enemy enemy) {
		if(!enemy.isStunned() && enemyGrab == null)
			return false;
		
		enemy.isCarriable(body.getPosition());
		this.enemyGrab = enemy;
		setInteractionState(InteractionState.GRAB);
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
		
			hook(point);
			
			return 0;
		} else
			return 1;
	}

}

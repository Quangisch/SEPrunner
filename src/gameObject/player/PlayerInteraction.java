package gameObject.player;

import gameObject.IGameObjectStates.GameObjectStates.InteractionState;
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
	private int shuriken = 0;
	
	protected PlayerInteraction(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}
	
	protected void processInput() {
//		basic movement
		
		processHook();
//		
//		if(isGrounded()) {
//			if(InputHandler.getInstance().isKeyDown(GameProperties.keyJump)
//					&& !isHooking() && !isHiding() && !isGrabbing())
//				interactionState = InteractionState.JUMP;
//			
//			if(InputHandler.getInstance().isKeyDown(GameProperties.keyCrouch))
//				interactionState = InteractionState.CROUCH_STAND;
//			
//			if(InputHandler.getInstance().isKeyDown(GameProperties.keyThrow))
//				processThrow();
//			
//			if(InputHandler.getInstance().isKeyDown(GameProperties.keyHook))
//				processHook();
//			
//			if(InputHandler.getInstance().isKeyDown(GameProperties.keyAction))
//				processAction();
//		}
//		
//		if (InputHandler.getInstance().isKeyDown(GameProperties.keyRight)
//				|| InputHandler.getInstance().isKeyDown(GameProperties.keyLeft)) {
//		
//			switch(interactionState) {
//			case STAND : 
//				interactionState = InteractionState.WALK; 
//				break;
//			case CROUCH_STAND :
//				interactionState = InteractionState.CROUCH_SNEAK;
//				break;
//			case GRAB :
//				interactionState = InteractionState.GRAB_PULL;
//				break;
//
//			default:
//				break;
//			}		
//		}
//		
//		processRun();

	}
	


	private final int TAP_TIMER_INITIAL = 10;
	private int runTapTimer = TAP_TIMER_INITIAL;
	
	private boolean processRun() {
		
		if(GameProperties.debugMode)
			System.out.println(runTapTimer);
		
		switch(interactionState) {
			case WALK:
				if(runTapTimer > 0)
					interactionState = InteractionState.RUN;
				else
					runTapTimer = TAP_TIMER_INITIAL;
				break;
			
			case STAND:
				if(runTapTimer > 0)
					runTapTimer--;
				break;
				
			default:
				break;
		
		}
		
		return false;
	}
	

	private final int ACTION_TIMER_INITIAL = 10;
	private int actionTimer = ACTION_TIMER_INITIAL;
	
	private boolean processThrow() {
		
		Click click = InputHandler.getInstance().getClick();
		if(click == null)
			return false;
		InputHandler.getInstance().keyUp(GameProperties.keyHook);
		
//		only can throw, if throwTimer >= 10, to prevent spamming
		if(actionTimer < ACTION_TIMER_INITIAL) {
			actionTimer++;
			return false;
		}
		
		if(shuriken <= 0)
			return false;
		
		shuriken--;
		Vector2 direction = new Vector2();
		new Shuriken(this, direction);
		actionTimer = 0;

		return true;
	}
	
	private final int HOOK_RADIUS = 150;
	
	private boolean processHook() {
		
		
		if(actionTimer < ACTION_TIMER_INITIAL) {
			actionTimer++;
			return false;
		}
		
		Click click = InputHandler.getInstance().getClick();
		if(click == null)
			return false;
		InputHandler.getInstance().keyUp(GameProperties.keyHook);
		
		Vector2 startPoint = GameProperties.meterToPixel(body.getWorldPoint(body.getLocalCenter()));
		Vector2 clickPoint = new Vector2(click.screenX, click.screenY);
		Camera.getInstance().unproject(clickPoint);
		
		Vector2 endPoint = startPoint.cpy();
		endPoint.add(clickPoint);
		endPoint.limit(startPoint.len() + HOOK_RADIUS);
		System.out.println(startPoint.toString()+" -- "+endPoint.toString());
		
		
		
		new GeometricObject(new Circle(startPoint.x, startPoint.y, 5), Color.RED);
		new GeometricObject(new Circle(clickPoint.x, clickPoint.y, 10), Color.RED);
		new GeometricObject(new Circle(endPoint.x, endPoint.y, 5), Color.GREEN);
//		endPoint.limit(HOOK_RADIUS);
		
		
		body.getWorld().rayCast(this, getWorldPosition(), clickPoint);
		
		actionTimer = 0;
		
		return true;
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
		return interactionState.equals(InteractionState.HOOK_FLY)
				|| interactionState.equals(InteractionState.HOOK_START);	
	}
	public boolean isRunning() 	{ 
		return interactionState.equals(InteractionState.RUN);	
	}
	
	public boolean isHiding() 	{ 
		return interactionState.equals(InteractionState.HIDE);	
	}
	
	public boolean isGrabbing() { 
		return enemyGrab != null 
				&& (interactionState.equals(InteractionState.GRAB) 
						|| interactionState.equals(InteractionState.GRAB_PULL));	
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
		interactionState = InteractionState.GRAB;
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
		return 0;
	}

}

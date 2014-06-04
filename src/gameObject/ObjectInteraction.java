package gameObject;

import gameObject.enemy.Enemy;
import gameObject.player.Detectable;
import gameObject.player.Shuriken;
import misc.Debug;
import misc.GeometricObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

import core.ingame.Camera;
import core.ingame.GameProperties;
import core.ingame.IInputHandler;
import core.ingame.InputHandler.Click;
import core.ingame.KeyMap.ActionKey;

public abstract class ObjectInteraction extends GameObject implements
		Detectable, RayCastCallback {

	private Enemy enemyGrab;
	private int shuriken = 10;

	private Vector2 target;
	private InteractionState nextState, interruptedState;
	private volatile int bodyBlocked = 0;
	private volatile int grounded = 0;

//	private Set<Integer> keyPressed;
	protected IInputHandler iHandler;
	private Click click;

	protected ObjectInteraction(World world, Vector2 position) {
		super(world, position);
//		keyPressed = new HashSet<Integer>();
	}
	
	protected void processInput() {

		boolean action = false;
		processHook();

		// CLICK
		if (actionTimer >= ACTION_TIMER_INITIAL && !isCrouching()
				&& getInteractionState().isInterruptable()) {
			click = iHandler.getClick();
			if (click != null) {
				normalizeClickPoint();
				if (iHandler.isButtonDown(ActionKey.THROW))
					action = processThrow();
				else if (iHandler.isButtonDown(ActionKey.HOOK))
					action = tryToHook();

				click = null;
				iHandler.popClick();
			} 
			
		} else
			actionTimer++;

		if (!action && isGrounded()) {

			if (iHandler.isKeyDown(ActionKey.ACTION))
				processAction();

			// JUMP
			else if (iHandler.isKeyDown(ActionKey.JUMP)) {
				nextState = InteractionState.JUMP;
			} else if (isJumping() && isGrounded())
				nextState = getInteractionState().equals(InteractionState.JUMP) ? InteractionState.STAND
						: InteractionState.WALK;

			// CROUCH
			else if (iHandler.isKeyDown(ActionKey.CROUCH)) {
				if (!isCrouching()) {
					if (getInteractionState().equals(InteractionState.STAND))
						nextState = InteractionState.CROUCH_DOWN;
					else
						nextState = InteractionState.CROUCH_STAND;
				} else if (getInteractionState().equals(
						InteractionState.CROUCH_DOWN)
						&& isAnimationFinished())
					nextState = InteractionState.CROUCH_STAND;
			} else if (isCrouching() && !isBodyBlocked())
				nextState = InteractionState.STAND;
		}

		// BASIC MOVEMENT
		if (!action) {
			if (iHandler.isKeyDown(ActionKey.RIGHT) || iHandler.isKeyDown(ActionKey.LEFT)) {

				setFlip(iHandler.isKeyDown(ActionKey.LEFT));

				switch (getInteractionState()) {
				case CROUCH_DOWN:
				case CROUCH_STAND:
					nextState = InteractionState.CROUCH_SNEAK;
					break;
				case GRAB:
					nextState = InteractionState.GRAB_PULL;
					break;
				case STAND:
					nextState = InteractionState.WALK;
					break;
				case JUMP:
					nextState = InteractionState.JUMP_MOVE;
					break;
				default:
					break;
				}
			} else {

				switch (getInteractionState()) {
				case RUN:
				case WALK:
					nextState = InteractionState.STAND;
					break;
				case CROUCH_SNEAK:
					nextState = InteractionState.CROUCH_STAND;
					break;
				case GRAB_PULL:
					nextState = InteractionState.GRAB;
					break;
				case JUMP_MOVE:
					nextState = InteractionState.JUMP;
					break;
				default:
					break;
				}
			}
		}

		if (!action)
			processRun();

		if (interruptedState != null && isAnimationFinished()) {
			nextState = interruptedState;
			interruptedState = null;
		}

		applyState(nextState);

	}

	private void applyState(InteractionState state) {
		if (nextState == null)
			return;

		if (!nextState.equals(getInteractionState())) {
			setInteractionState(nextState);
			if (applyAnimation())
				nextState = null;
		} else
			nextState = null;

	}

	private final int TAP_TIMER_LIMIT_HIGH = 10, TAP_TIMER_LIMIT_LOW = 1;
	private int runTapTimer = 0;

	private boolean processRun() {

		switch (getInteractionState()) {
		case WALK:
			if (runTapTimer < TAP_TIMER_LIMIT_HIGH
					&& runTapTimer > TAP_TIMER_LIMIT_LOW)
				nextState = InteractionState.RUN;
			else
				runTapTimer = 0;
			break;

		case STAND:
			if (runTapTimer < TAP_TIMER_LIMIT_HIGH)
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

	// CLICKPOINT
	private void normalizeClickPoint() {
		startPoint = GameProperties.meterToPixel(this.getLocalCenterInWorld());
		clickPoint = new Vector2(click.screenX, click.screenY);
		Camera.getInstance().unproject(clickPoint);

		switch (Debug.getMode()) {
		case GEOMETRIC:
			new GeometricObject(new Circle(startPoint.x, startPoint.y, 5),
					Color.RED);
			break;
		case CONSOLE:
			Debug.println(startPoint.toString() + " -- "
					+ clickPoint.toString());
			break;
		default:
			break;
		}
	}

	// THROW (SHURIKEN)
	private boolean processThrow() {
		if (shuriken <= 0)
			return false;

		if (clickPoint.x < startPoint.x && tryToFlip())
			return false;

		interruptedState = getInteractionState();
		applyState(nextState = InteractionState.THROW);

		shuriken--;
		new Shuriken(this, clickPoint);
		actionTimer = 0;

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

	// HOOK
	private final int HOOK_RADIUS = 400;

	private boolean tryToHook() {
		Vector2 endPoint = clickPoint.cpy();

		endPoint.sub(startPoint);
		endPoint.nor().scl(HOOK_RADIUS);
		endPoint.add(startPoint);

		body.getWorld().rayCast(this, getLocalCenterInWorld(),
				GameProperties.pixelToMeter(endPoint));

		actionTimer = 0;

		return true;
	}

	private boolean beginHook(Vector2 target) {

		if (target.x < getLocalCenterInWorld().x && !tryToFlip())
			return false;

		setInteractionState(InteractionState.HOOK, true);
		applyAnimation();
		// body.setGravityScale(scale);
		// TODO
		// body.setTransform(target, body.getAngle());
		this.target = target.sub(getLocalCenterInWorld());
		return true;
	}

	private final int HOOK_TIME_LIMIT = 30;
	private int hookTime = 0;

	private boolean processHook() {
		if (target == null || !isHooking())
			return false;

		if (getInteractionState().equals(InteractionState.HOOK)
				&& isAnimationFinished()) {
			setInteractionState(InteractionState.HOOK_FLY, true);
			applyAnimation();
		}

		if (!getInteractionState().equals(InteractionState.HOOK_FLY))
			return true;

		body.applyLinearImpulse(target.clamp(10, 15), getLocalCenterInWorld(), true);
		hookTime++;

		if (hookTime >= HOOK_TIME_LIMIT)
			resetHook();

		return true;
	}

	protected void resetHook() {
		target = null;
		hookTime = 0;
		body.setGravityScale(1);
		setInteractionState(InteractionState.STAND, true);
		applyAnimation();
	}

	// TODO
	// ACTION
	private boolean processAction() {
		iHandler.keyUp(ActionKey.ACTION);
		return false;
	}

	// INTERACTION WITH ENEMY
	@Override
	public boolean isDetectable(Enemy enemy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCaptured(Enemy enemy) {
		// TODO Auto-generated method stub

	}

	// STATES
	public boolean isRunning() {
		return getInteractionState().equals(InteractionState.RUN);
	}

	public boolean isHiding() {
		return getInteractionState().equals(InteractionState.HIDE);
	}

	public boolean isCrouching() {
		return getInteractionState().equals(InteractionState.CROUCH_DOWN)
				|| getInteractionState().equals(InteractionState.CROUCH_STAND)
				|| getInteractionState().equals(InteractionState.CROUCH_SNEAK);
	}

	public boolean isHooking() {
		return getInteractionState().equals(InteractionState.HOOK)
				|| getInteractionState().equals(InteractionState.HOOK_FLY);
	}

	public boolean isJumping() {
		return getInteractionState().equals(InteractionState.JUMP)
				|| getInteractionState().equals(InteractionState.JUMP_MOVE);
	}

	public boolean isGrabbing() {
		return enemyGrab != null
				&& (getInteractionState().equals(InteractionState.GRAB) || getInteractionState()
						.equals(InteractionState.GRAB_PULL));
	}

	public boolean setGrab(Enemy enemy) {
		if (!enemy.isStunned() && enemyGrab == null)
			return false;

		enemy.isCarriable(body.getPosition());
		this.enemyGrab = enemy;
		nextState = InteractionState.GRAB;
		return true;
	}

	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		if (((GameObject) fixture.getBody().getUserData()).getGameObjectType() == GameObjectTypes.GROUND) {

			if (Debug.isMode(Debug.Mode.GEOMETRIC)) {
				Vector2 p = GameProperties.meterToPixel(point);
				new GeometricObject(new Circle(p.x - 5, p.y - 5, 5), Color.BLUE);
			}

			beginHook(point);
			return 0;
		} else
			return 1;
	}

	public boolean isBodyBlocked() {
		return bodyBlocked > 0;
	}

	public void calcBodyBlockedContact(boolean start) {
		bodyBlocked += start ? 1 : -1;
	}

	public boolean isGrounded() {
		return grounded > 0;
	}

	public void calcGroundedContact(boolean start) {
		grounded += start ? 1 : -1;
	}
	
	public void setClick(Click click) {
		this.click = click;
	}
	
	
	// HELPER
	private boolean tryToFlip() {
		if (iHandler.isKeyDown(ActionKey.RIGHT))
			return false;
		setFlip(true);
		return true;
	}

	// TEMP Methods for debugging
	protected int getGrounded() {
		return grounded;
	}

	protected int getBodyBlocked() {
		return bodyBlocked;
	}

	// COLLISION DETECTION
	public boolean handleCollision(boolean start, Sensor mySensor,
			GameObject other, Sensor otherSensor) {
		boolean handled = super.handleCollision(start, mySensor, other,
				otherSensor);

		if (handled)
			return handled;

		if (mySensor != null) {
			// CHECK GROUNDED
			if (mySensor.getSensorType() == SensorTypes.FOOT
					&& other.getGameObjectType() == GameObjectTypes.GROUND) {
				calcGroundedContact(start);
				return true;
			}

			// CHECK BODY
			if (mySensor.getSensorType() == SensorTypes.BODY
					&& other.getGameObjectType() == GameObjectTypes.GROUND) {
				calcBodyBlockedContact(start);
				return true;
			}

		} else {
			// CHECK WHILE HOOKING
			if (isHooking()
					&& other.getGameObjectType() == GameObjectTypes.GROUND) {
				resetHook();
				return true;
			}
		}

		return false;
	}

}

package gameObject.interaction;

import gameObject.BodyObject;
import gameObject.GameObject;
import gameObject.Sensor;
import gameObject.enemy.Enemy;
import gameObject.player.Detectable;
import gameObject.player.Shuriken;
import gameWorld.GameWorld;
import misc.Debug;
import misc.GeometricObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import core.ingame.GameProperties;
import core.ingame.IInputHandler;
import core.ingame.InputHandler.Click;
import core.ingame.KeyMap.ActionKey;

public abstract class InteractionObject extends GameObject implements
		Detectable, RayCastCallback, Runnable {

	private Enemy enemyGrab;
	private int shuriken = 10;

	private Vector2 target;
	private InteractionState nextState, interruptedState;
	private volatile int bodyBlocked = 0;
	private volatile int grounded = 0;
	private volatile boolean hideable = false;

	private IInputHandler iHandler;
	private Click click;

	protected InteractionObject(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
	}

	public void run() {
		if (iHandler != null) {
			processInput();
			if (!isHooking())
				processStates();
		}
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

			// ACTION (HIDE OR GRAB)
			if (iHandler.isKeyDown(ActionKey.ACTION)) {
				processAction();

			} else if (!iHandler.isKeyDown(ActionKey.ACTION)) {
				endAction();

				//
				// } else if(isGrabbing()) {
				// nextState = InteractionState.STAND;
				// break action;

			}

			// JUMP
			if (iHandler.isKeyDown(ActionKey.JUMP)) {
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
			if (iHandler.isKeyDown(ActionKey.RIGHT)
					|| iHandler.isKeyDown(ActionKey.LEFT)) {

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

	protected void processStates() {

		Vector2 baseForce;
		switch (getInteractionState()) {
		// case STAND: case CROUCH_STAND: case THROW: case HIDE: case GRAB:
		// case GRAB_DISPOSE: case STUNNED: case HOOK_START: case HOOK_FLY:
		// break;
		case WALK:
			baseForce = new Vector2(1, 0);
			break;
		case RUN:
			baseForce = new Vector2(1.7f, 0);
			break;
		case CROUCH_SNEAK:
			baseForce = new Vector2(0.7f, 0);
			break;
		case GRAB_PULL:
			baseForce = new Vector2(0.6f, 0);
			break;
		case JUMP:
			baseForce = new Vector2(0, 20);
			break;
		case JUMP_MOVE:
			baseForce = new Vector2(1, 20);
			break;
		default:
			baseForce = new Vector2(0, 0);
			break;
		}

		if (!isGrounded())
			baseForce.y = 0;

		if (isFlipped())
			baseForce.x *= -1;

		// tweak gravity
		if (baseForce.len() != 0)
			setGravityScale(0.7f);
		else
			setGravityScale(1);

		// apply impulse
		applyImpulse(baseForce.scl(isGrounded() ? 2 : 1.5f));
	}

	private void applyState(InteractionState state) {
		if (nextState == null)
			return;

		if (!nextState.equals(getInteractionState())) {
			boolean set = setInteractionState(nextState);

			if (set) {
				applyAnimation();
				nextState = null;
			}
		} else
			nextState = null;
	}

	// TODO Move To Preferences
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
		getGameWorld().getCamera().unproject(clickPoint);

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

		rayCast(this, getLocalCenterInWorld(),
				GameProperties.pixelToMeter(endPoint));

		actionTimer = 0;

		return true;
	}

	private boolean beginHook(Vector2 target) {

		if (target.x < getLocalCenterInWorld().x && !tryToFlip())
			return false;

		setGravityScale(0);
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

		applyImpulse(target.nor().scl(10));
		hookTime++;

//		System.out.println(hookTime);
		if (hookTime >= HOOK_TIME_LIMIT || (hookTime >= 5 && isGrounded()))
			resetHook();

		return true;
	}

	protected void resetHook() {
		target = null;
		hookTime = 0;
		setGravityScale(1);
		setInteractionState(InteractionState.STAND, true);
		applyAnimation();
	}

	// TODO Move finals to Preferences/Drawing class
	private int oriLayer;
	private final int HIDE_LAYER = -1;
	private float oriAlpha;
	private final float HIDE_ALPHA = 0.5f;

	// ACTION
	private boolean processAction() {
		if (hideable)
			processHiding(true);
		return false;
	}

	private void endAction() {
		if (isHiding())
			processHiding(false);
	}

	private void processHiding(boolean start) {
		if (!isGrounded())
			return;

		// start hiding
		if (start) {
			switch (getInteractionState()) {
			case STAND:
			case WALK:
			case CROUCH_STAND:
			case CROUCH_SNEAK:
				nextState = InteractionState.HIDE_START;
				oriAlpha = getAlpha();
				oriLayer = getLayer();
				setLayer(HIDE_LAYER);
				setAlpha(HIDE_ALPHA);
				break;
			case HIDE_START:
				nextState = InteractionState.HIDE;
			default:
				break;
			}

			// end hiding
		} else {
			if (getInteractionState().equals(InteractionState.HIDE))
				nextState = InteractionState.HIDE_END;
			if (getInteractionState().equals(InteractionState.HIDE_END))
				nextState = InteractionState.STAND;
			setLayer(oriLayer);
			setAlpha(oriAlpha);
		}
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
		return getInteractionState() == InteractionState.RUN;
	}

	public boolean isThrowing() {
		return getInteractionState() == InteractionState.THROW;
	}

	// TODO Replace equals with ==
	public boolean isHiding() {
		return getInteractionState().equals(InteractionState.HIDE_START)
				|| getInteractionState().equals(InteractionState.HIDE)
				|| getInteractionState().equals(InteractionState.HIDE_END);
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

		enemy.isCarriable(getPosition());
		this.enemyGrab = enemy;
		nextState = InteractionState.GRAB;
		return true;
	}

	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		if (((BodyObject) fixture.getBody().getUserData()).getGameObjectType() == GameObjectTypes.GROUND) {

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

	protected void setInputHandler(IInputHandler iHandler) {
		this.iHandler = iHandler;
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
			BodyObject other, Sensor otherSensor) {

		boolean handled = super.handleCollision(start, mySensor, other,
				otherSensor);
		if (handled)
			return handled;

		if (mySensor != null) {
			// CHECK GROUNDED
			if (mySensor.getSensorType() == SensorTypes.FOOT) {
				switch (other.getGameObjectType()) {
				case GameObjectTypes.GROUND:
					calcGroundedContact(start);
					return true;
				default:
					return false;
				}
			}

			// CHECK BODY
			if (mySensor.getSensorType() == SensorTypes.BODY) {
				switch (other.getGameObjectType()) {
				case GameObjectTypes.GROUND:
					calcBodyBlockedContact(start);
					return true;
				case GameObjectTypes.HIDEABLE:
					hideable = start;
					return true;
				default:
					return false;
				}
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

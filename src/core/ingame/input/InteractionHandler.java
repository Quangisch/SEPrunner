package core.ingame.input;

import gameObject.body.BodyObject;
import gameObject.body.GameObjectType;
import gameObject.interaction.GameObject;
import gameObject.interaction.InteractionState;
import gameObject.interaction.enemy.Enemy;
import gameObject.interaction.player.Detectable;
import gameObject.interaction.player.Shuriken;
import misc.Debug;
import misc.GeometricObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import core.ingame.GameProperties;
import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap.ActionKey;

public class InteractionHandler implements
		Detectable, Runnable, RayCastCallback {

	private IInputHandler iHandler;
	private GameObject gameObject;
	
	private Enemy enemyGrab;

	private Vector2 target;
	private InteractionState nextState, interruptedState;
	
	private volatile boolean hideable = false;

	private Click click;

	public InteractionHandler(IInputHandler iHandler, GameObject gameObject) {
		this.iHandler = iHandler;
		this.gameObject = gameObject;
	}

	public void run() {
		if (iHandler != null) {
			processInput();
			if (!gameObject.isHooking())
				processStates();
		}
	}

	protected void processInput() {

		boolean action = false;
		processHook();

		// CLICK
		if (actionTimer >= ACTION_TIMER_INITIAL && !gameObject.isCrouching()
				&& gameObject.isInteractionFinished()) {
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

		if (!action && gameObject.isGrounded()) {

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
			} else if (gameObject.isJumping() && gameObject.isGrounded())
				nextState = gameObject.getInteractionState().equals(InteractionState.JUMP) ? InteractionState.STAND
						: InteractionState.WALK;

			// CROUCH
			else if (iHandler.isKeyDown(ActionKey.CROUCH)) {
				if (!gameObject.isCrouching()) {
					if (gameObject.getInteractionState().equals(InteractionState.STAND))
						nextState = InteractionState.CROUCH_DOWN;
					else
						nextState = InteractionState.CROUCH_STAND;
				} else if (gameObject.getInteractionState().equals(
						InteractionState.CROUCH_DOWN)
						&& gameObject.isInteractionFinished())
					nextState = InteractionState.CROUCH_STAND;
			} else if (gameObject.isCrouching() && !gameObject.isBodyBlocked())
				nextState = InteractionState.STAND;
		}

		// BASIC MOVEMENT
		if (!action) {
			if (iHandler.isKeyDown(ActionKey.RIGHT)
					|| iHandler.isKeyDown(ActionKey.LEFT)) {

				gameObject.setFlip(iHandler.isKeyDown(ActionKey.LEFT));

				switch (gameObject.getInteractionState()) {
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

				switch (gameObject.getInteractionState()) {
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

		if (interruptedState != null && gameObject.isInteractionFinished()) {
			nextState = interruptedState;
			interruptedState = null;
		}

		applyState(nextState);

	}

	protected void processStates() {

		Vector2 baseForce;
		switch (gameObject.getInteractionState()) {
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

		if (!gameObject.isGrounded())
			baseForce.y = 0;

		if (gameObject.isFlipped())
			baseForce.x *= -1;

		// tweak gravity
		if (baseForce.len() != 0)
			gameObject.setGravityScale(0.7f);
		else
			gameObject.setGravityScale(1);

		// apply impulse
		gameObject.applyImpulse(baseForce.scl(gameObject.isGrounded() ? 2 : 1.5f));
	}

	private void applyState(InteractionState state) {
		if (nextState == null)
			return;

		if (!nextState.equals(gameObject.getInteractionState())) {
			boolean set = gameObject.tryToSetInteractionState(nextState);

			if (set) {
				gameObject.applyInteraction();
				nextState = null;
			}
		} else
			nextState = null;
	}

	private final int TAP_TIMER_LIMIT_HIGH = 10, TAP_TIMER_LIMIT_LOW = 1;
	private int runTapTimer = 0;

	private boolean processRun() {

		switch (gameObject.getInteractionState()) {
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
		startPoint = GameProperties.meterToPixel(gameObject.getLocalCenterInWorld());
		clickPoint = new Vector2(click.screenX, click.screenY);
		gameObject.getGameWorld().getCamera().unproject(clickPoint);

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
		if (!gameObject.decShuriken())
			return false;

		if (clickPoint.x < startPoint.x && tryToFlip())
			return false;

		interruptedState = gameObject.getInteractionState();
		applyState(nextState = InteractionState.THROW);

		new Shuriken(gameObject, clickPoint);
		actionTimer = 0;

		return true;
	}

	// HOOK
	private final int HOOK_RADIUS = 400;

	private boolean tryToHook() {
		Vector2 endPoint = clickPoint.cpy();

		endPoint.sub(startPoint);
		endPoint.nor().scl(HOOK_RADIUS);
		endPoint.add(startPoint);

		gameObject.getGameWorld().getWorld().rayCast(this, gameObject.getLocalCenterInWorld(),
				GameProperties.pixelToMeter(endPoint));

		actionTimer = 0;

		return true;
	}

	private boolean beginHook(Vector2 target) {

		if (target.x < gameObject.getLocalCenterInWorld().x && !tryToFlip())
			return false;

		gameObject.setGravityScale(0);
		gameObject.setInteractionState(InteractionState.HOOK, true);
		gameObject.applyInteraction();
		// body.setGravityScale(scale);
		// TODO
		// body.setTransform(target, body.getAngle());
		this.target = target.sub(gameObject.getLocalCenterInWorld());
		return true;
	}

	private final int HOOK_TIME_LIMIT = 30;
	private int hookTime = 0;

	private boolean processHook() {
		if (target == null || !gameObject.isHooking())
			return false;

		if (gameObject.getInteractionState().equals(InteractionState.HOOK)
				&& gameObject.isInteractionFinished()) {
			gameObject.setInteractionState(InteractionState.HOOK_FLY, true);
			gameObject.applyInteraction();
		}

		if (!gameObject.getInteractionState().equals(InteractionState.HOOK_FLY))
			return true;

		gameObject.applyImpulse(target.nor().scl(10));
		hookTime++;

//		System.out.println(hookTime);
		if (hookTime >= HOOK_TIME_LIMIT || (hookTime >= 5 && gameObject.isGrounded()))
			resetHook();

		return true;
	}

	protected void resetHook() {
		target = null;
		hookTime = 0;
		gameObject.setGravityScale(1);
		gameObject.setInteractionState(InteractionState.STAND, true);
		gameObject.applyInteraction();
	}

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
		if (gameObject.isHiding())
			processHiding(false);
	}

	private void processHiding(boolean start) {
		if (!gameObject.isGrounded())
			return;

		// start hiding
		if (start) {
			switch (gameObject.getInteractionState()) {
			case STAND:
			case WALK:
			case CROUCH_STAND:
			case CROUCH_SNEAK:
				nextState = InteractionState.HIDE_START;
				oriAlpha = gameObject.getAlpha();
				oriLayer = gameObject.getLayer();
				gameObject.setLayer(HIDE_LAYER);
				gameObject.setAlpha(HIDE_ALPHA);
				break;
			case HIDE_START:
				nextState = InteractionState.HIDE;
			default:
				break;
			}

			// end hiding
		} else {
			if (gameObject.getInteractionState().equals(InteractionState.HIDE))
				nextState = InteractionState.HIDE_END;
			if (gameObject.getInteractionState().equals(InteractionState.HIDE_END))
				nextState = InteractionState.STAND;
			gameObject.setLayer(oriLayer);
			gameObject.setAlpha(oriAlpha);
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
	

	public boolean setGrab(Enemy enemy) {
		if (!enemy.isStunned() && enemyGrab == null)
			return false;

		enemy.isCarriable(gameObject.getPosition());
		this.enemyGrab = enemy;
		nextState = InteractionState.GRAB;
		return true;
	}


	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		
		if (((BodyObject) fixture.getBody().getUserData()).getGameObjectType().equals(GameObjectType.Ground)) {

			if (Debug.isMode(Debug.Mode.GEOMETRIC)) {
				Vector2 p = GameProperties.meterToPixel(point);
				new GeometricObject(new Circle(p.x - 5, p.y - 5, 5), Color.BLUE);
			}

			beginHook(point);
			return 0;
		} else
			return 1;
	}
	
	public boolean equals(Object object) {
		if(object == null || !(object instanceof InteractionHandler))
			return false;
		
//		TODO equals for GameObject and IIHandler
		InteractionHandler other = (InteractionHandler) object;
		return this.gameObject.equals(other.gameObject) && this.iHandler.equals(other.iHandler);
	}
	

	// HELPER
	private boolean tryToFlip() {
		if (iHandler.isKeyDown(ActionKey.RIGHT))
			return false;
		gameObject.setFlip(true);
		return true;
	}

}
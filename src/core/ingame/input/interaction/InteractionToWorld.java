package core.ingame.input.interaction;

import gameObject.interaction.GameObject;
import misc.Debug;

import com.badlogic.gdx.math.Vector2;

import core.ingame.input.IInputHandler;
import core.ingame.input.KeyMap.ActionKey;

public class InteractionToWorld {

	private GameObject gameObject;
	private IInputHandler iHandler;

	protected InteractionToWorld(InteractionHandler interactionHandler, IInputHandler iHandler) {
		this.gameObject = interactionHandler.getGameObject();
		this.iHandler = iHandler;

	}

	protected void process() {

		applyForce(applyForceMultiplier(processBaseForce()));
	}

	private Vector2 processBaseForce() {
		Vector2 baseForce = new Vector2();

		switch (gameObject.getInteractionState()) {
		case CROUCH_DOWN:
		case CROUCH_STAND:
		case HIDE:
		case HIDE_END:
		case HIDE_START:
		case STUNNED:
		case STAND:
		case THROW:
		case GRAB:
		case GRAB_DISPOSE:
		case HOOK:
			return new Vector2();
		case HOOK_FLY:
			if (gameObject.getHookPoint() != null) {
				Vector2 hP = gameObject.getHookPoint().cpy();
				Vector2 sP = gameObject.getBodyObject().getLocalCenterInWorld().cpy();
				return hP.sub(sP).clamp(5, 5);
			}
			return new Vector2();

		case JUMP:
			baseForce.add(0, 1);
			break;
		case JUMP_MOVE:
			baseForce.add(1, 1);
			break;

		case GRAB_PULL:
		case RUN:
		case CROUCH_SNEAK:
		case WALK:
			baseForce.add(1, gameObject.isGrounded() ? 1 : 0);
			//TODO:Geschwindigkeit zu schnell für Enemys
			break;
		case WALK_ENEMY:
			baseForce.add(0.55f, gameObject.isGrounded() ? 1.35f : 0);
			break;
		default:
			break;

		}
		return baseForce;
	}

	private float runMul = 1.5f, sneakMul = 0.8f, pullMul = 1.5f;

	private Vector2 applyForceMultiplier(Vector2 baseForce) {
		Vector2 multipliedForce = baseForce.scl(2.5f);

		switch (gameObject.getInteractionState()) {
		case RUN:
			multipliedForce = baseForce.scl(runMul, 1);
			break;
		case CROUCH_SNEAK:
			multipliedForce = baseForce.scl(sneakMul, 0);
			break;
		case GRAB_PULL:
			multipliedForce = baseForce.scl(-pullMul, 0);
			break;
		case JUMP:
		case JUMP_MOVE:
			multipliedForce = processJumpMultiplier(baseForce);
			break;
		default:
			multipliedForce = baseForce;
		}

		if (gameObject.getAnimationObject().isFlipped() && !gameObject.isHooking()) multipliedForce.scl(-1, 1);

		return multipliedForce;
	}

	private void applyForce(Vector2 multipliedForce) {
		gameObject.getBodyObject().applyImpulse(multipliedForce);
	}

	private final int JUMP_TIMER_MAX = 15;
	private float jumpTimer = 1;

	private Vector2 processJumpMultiplier(Vector2 baseForce) {
		Debug.print("JumpTimer@" + jumpTimer, Debug.Mode.CONSOLE);

		if (gameObject.isJumping() && jumpTimer < JUMP_TIMER_MAX)
			jumpTimer = jumpTimer * 1.2f;
		else if (gameObject.isGrounded()) jumpTimer = 1;

		if (iHandler.isKeyDown(ActionKey.JUMP))
			baseForce.scl(0.7f, Math.max((JUMP_TIMER_MAX - jumpTimer) * 0.4f, 0));
		else
			baseForce.scl(0.7f, 0);

		return baseForce;
	}

}

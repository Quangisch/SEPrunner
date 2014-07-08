package gameObject;

import gameObject.body.BodyObject;
import gameObject.drawable.AnimationObject;
import gameObject.interaction.InteractionState;
import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

public interface IGameObject {

	void init(JsonValue resources);
	GameWorld getGameWorld();
	AnimationObject getAnimationObject();
	BodyObject getBodyObject();	
	
//	Interactable
	boolean isBodyBlocked();
	boolean isGrounded();
	boolean areBothFeetsGrounded();
	
	boolean decShuriken();
	int getShurikenQuantity();

	boolean canHide();
	boolean canGrab();
	boolean canDispose();

	boolean startGrab();
	boolean endGrab();
	boolean disposeGrab();

	int getHookRadius();
	void setHookRadius(int hookRadius);	
	Vector2 getHookPoint();
	boolean tryToHook(Vector2 clickPoint);
	
//	InteractionManageable
	InteractionState getInteractionState();
	InteractionState getDefaultInteractionState();
	boolean isInteractionFinished();
	void applyInteraction(InteractionState state);
	boolean tryToApplyInteraction(InteractionState state);
	
	boolean isRunning();
	boolean isThrowing();
	boolean isHiding();
	boolean isCrouching();
	boolean isHooking();
	boolean isJumping();
	boolean isGrabbing();
	boolean isStunned();	
	boolean isInAction();
	
}

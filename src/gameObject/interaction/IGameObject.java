package gameObject.interaction;

import gameObject.body.BodyObject;
import gameObject.drawable.AnimationObject;
import gameWorld.GameWorld;

public interface IGameObject {

	void init(String name);
	
	
	
	GameWorld getGameWorld();
	
	AnimationObject getAnimationObject();
	
	BodyObject getBodyObject();
	
	
	
	boolean isBodyBlocked();
	
	boolean isGrounded();
	
	boolean decShuriken();
	
	int getShurikenQuantity();
	
}

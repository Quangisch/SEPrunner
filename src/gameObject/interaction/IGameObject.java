package gameObject.interaction;

import gameObject.body.BodyObject;
import gameObject.drawable.AnimationObject;
import gameWorld.GameWorld;

import com.badlogic.gdx.utils.JsonValue;

public interface IGameObject {

	void init(JsonValue resources);

	GameWorld getGameWorld();
	
	AnimationObject getAnimationObject();
	
	BodyObject getBodyObject();	
	
}

package gameObject.statics;

import gameObject.body.BodyObjectType;
import gameObject.body.IBodyInitializer;
import gameObject.interaction.GameObject;
import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

public class Hideout extends GameObject implements Hideable {

	public Hideout(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
	}

	@Override
	public boolean canHide(IBodyInitializer moveableObject) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void init(JsonValue resources) {
		super.init(resources);
		setBodyObjectType(BodyObjectType.Hideable);
		getAnimationObject().setLayer(2);
	}

}

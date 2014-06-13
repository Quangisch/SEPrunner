package gameObject.statics;

import gameObject.body.BodyObjectType;
import gameObject.body.IBodyInitializer;
import gameObject.interaction.GameObject;
import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;

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
	public void init(String name) {
		super.init(name);
		setBodyObjectType(BodyObjectType.Hideable);
		getAnimationObject().setLayer(2);
	}

}

package gameObject.statics;

import gameObject.GameObject;
import gameObject.body.ICollisionable;
import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;

public class Hideout extends GameObject implements Hideable {

	public Hideout(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
	}

	@Override
	public boolean canHide(ICollisionable moveableObject) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void init(String name) {
		super.init(name);
		setGameObjectType(GameObjectType.HIDEABLE);
		setLayer(2);
	}

}

package gameObject.statics;

import gameObject.Collisionable;
import gameObject.GameObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Hideout extends GameObject implements Hideable {

	public Hideout(World world, Vector2 position) {
		super(world, position);
	}

	@Override
	public boolean canHide(Collisionable moveableObject) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void init(String name) {
		super.init(name);
		setGameObjectType(GameObjectTypes.HIDEABLE);
		setLayer(2);
		body.setFixedRotation(true);
	}

}

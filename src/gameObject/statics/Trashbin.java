package gameObject.statics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import gameObject.GameObject;
import gameObject.Moveable;

public class Trashbin extends GameObject implements Hideable {

	public Trashbin(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canHide(Moveable moveableObject) {
		// TODO Auto-generated method stub
		return false;
	}

}

package gameObject.player;

import gameObject.Collisionable;
import gameObject.GameObject;

import com.badlogic.gdx.math.Vector2;

public class Shuriken extends GameObject implements Collisionable {

	public Shuriken(Collisionable thrower, Vector2 direction) {
		super(thrower.getWorld(), thrower.getWorldPosition());
		// TODO Auto-generated constructor stub
	}
	

}

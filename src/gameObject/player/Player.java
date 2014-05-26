package gameObject.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import gameObject.GameObject;
import gameObject.enemy.Enemy;

public class Player extends GameObject implements Runnable, Detectable {

	public Player(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}

	public void run() {

	}

	@Override
	public boolean isDetectable(Enemy enemy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCaptured(Enemy enemy) {
		// TODO Auto-generated method stub

	}

}

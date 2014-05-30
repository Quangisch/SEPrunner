package gameObject.enemy;

import gameObject.player.Player;
import gameObject.statics.Hideable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Enemy extends EnemyAI implements Runnable, Stunnable {

	public Enemy(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}

	public void run() {
		
	}

	@Override
	public boolean isStunned() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCarriable(Vector2 position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setStun() {
		// TODO Auto-generated method stub
	}

	@Override
	public void attachToCarrier(Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void detachFromCarrier(Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean disposeAndHide(Hideable hideable) {
		// TODO Auto-generated method stub
		return false;
	}

	
}

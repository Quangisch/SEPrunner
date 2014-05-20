package gameObject.enemy;

import gameObject.player.Player;
import gameObject.statics.Hideable;

import com.badlogic.gdx.math.Vector2;

public class Enemy extends EnemyAI implements Runnable, Stunnable {

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

package gameObject.player;

import gameObject.GameObject;
import gameObject.enemy.Enemy;
import gameWorld.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import core.ingame.Camera;
import core.ingame.GameProperties;

public class Player extends GameObject implements Runnable, Detectable {

	private boolean grounded;

	public Player(World world, Vector2 position) {
		super(world, position);
		Camera.getInstance().setToFollowMoveable(this);
	}

	public static Player getInstance() {
		return Map.getInstance().getPlayer();
	}

	public void run() {
		processInput();
	}

	private void processInput() {
		Vector2 baseForce = new Vector2(0, 0);

		if (InputHandler.getInstance().isKeyDown(GameProperties.keyRight)) {
			baseForce.add(1, 0);
			setFlip(false);
			setCurrentState(1);
		} else if (InputHandler.getInstance().isKeyDown(GameProperties.keyLeft)) {
			baseForce.add(-1, 0);
			setFlip(true);
			setCurrentState(1);
		} else if (!grounded)
			setCurrentState(2);
		else
			setCurrentState(0);

		if ((getCurrentState() == 1 || getCurrentState() == 3)
				&& InputHandler.getInstance().isKeyDown(GameProperties.keyRun)) {
			setCurrentState(3);
			baseForce.scl(1.7f);
		}

		if (InputHandler.getInstance().isKeyDown(GameProperties.keyJump) && grounded) {
			grounded = false;
			body.applyLinearImpulse(new Vector2(body.getLocalCenter().x,
					body.getLocalCenter().y + 100), body.getWorldCenter(), true);
		}

		body.applyLinearImpulse(baseForce.scl(2f), body.getWorldCenter(), true);

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

	public void setGrounded(boolean grounded) {
		this.grounded = grounded;
	}

	public boolean isGrounded() {
		return grounded;
	}

}

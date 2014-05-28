package gameObject.player;

import gameObject.GameObject;
import gameObject.enemy.Enemy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import core.ingame.Camera;
import core.ingame.GameProperties;

public class Player extends GameObject implements Runnable, Detectable {
	
	private static Player player;
	
	private Player(World world, Vector2 position) {
		super(world, position);
		Camera.getInstance().setToFollowMoveable(this);
	}

	public static Player initInstance(World world, Vector2 position) {
		return player = new Player(world, position);
	}
	
	public static Player getInstance() {
		if(player == null)
			System.err.println("Player not initialized");
		return player;
	}
	
	public void run() {
		processInput();
	}
	
	private void processInput() {
		
		Vector2 baseForce = new Vector2(0,0);
		
//		basic movement
		if(InputHandler.getInstance().isKeyDown(GameProperties.keyRight)) {
			baseForce.add(1, 0);
			setFlip(false);
			setCurrentStatus(1);
		} else if(InputHandler.getInstance().isKeyDown(GameProperties.keyLeft)) {
			baseForce.add(-1, 0);
			setFlip(true);
			setCurrentStatus(1);
		} else if(!isGrounded())
			setCurrentStatus(1);
		else
			setCurrentStatus(0);
		
//		tweak gravity
		if(baseForce.len() != 0)
			body.setGravityScale(0.7f);
		else
			body.setGravityScale(1);
		
//		run
		if((getCurrentStatus() == 1 || getCurrentStatus() == 3) && InputHandler.getInstance().isKeyDown(GameProperties.keyRun) && isGrounded()) {
			setCurrentStatus(3);
			baseForce.scl(1.7f);
		}
		
//		jump
		if(InputHandler.getInstance().isKeyDown(GameProperties.keyJump) && isGrounded()) {
			setGrounded(false);
			body.applyLinearImpulse(new Vector2(body.getLocalCenter().x,body.getLocalCenter().y+100), body.getWorldCenter(), true);
		}
		
//		apply impulse
		body.applyLinearImpulse(baseForce.scl(isGrounded() ? 2 : 1.5f), body.getWorldCenter(), true);
		
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

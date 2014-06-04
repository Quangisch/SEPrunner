package core.ingame;

import gameObject.Collisionable;
import gameObject.player.InputHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import core.ingame.GameProperties.Debug;

public class Camera extends OrthographicCamera implements MoveableCamera {
	
	private static Camera camera;
	private Collisionable follow;
	
	private Camera() {
//		super.setToOrtho(false, GameProperties.pixelToMeter(Gdx.graphics.getWidth()), GameProperties.pixelToMeter(Gdx.graphics.getHeight()));
		super.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());	
	}
	
	public void update() {
		if(GameProperties.debugMode.equals(Debug.CAMERA)) {
			
			int dx = 0, dy = 0;
			
			if(InputHandler.getInstance().isKeyDown(Keys.D))
				dx = 1;
			else if(InputHandler.getInstance().isKeyDown(Keys.A))
				dx = -1;
			else if(InputHandler.getInstance().isKeyDown(Keys.W))
				dy = 1;
			else if(InputHandler.getInstance().isKeyDown(Keys.S))
				dy = -1;
			
			if(InputHandler.getInstance().isKeyDown(Keys.SPACE)) {
				dx *= 10;
				dy *= 10;
			}
			
			translate(dx, dy);
			super.update();
			return;
		}
		
		
		super.update();
		if(follow == null)
			return;
		
		float lerp = 0.1f;		
		
		float toX = follow.getX();//+GameProperties.width/3;
		float toY = follow.getY();//+GameProperties.height/5;
		
		if(position.x != toX || position.y != toY) {
			this.position.x += (toX - position.x) * lerp + 20;
			this.position.y += (toY - position.y) * lerp + 10;
//			if(GameProperties.debugMode)
//				System.out.println("Camera@"+position.x+"x"+position.y);
		}
			
	}
	
	public void updateOrtho() {
		super.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());	
	}
	
	public static Camera getInstance() {
		if(camera == null)
			camera = new Camera();
		return camera;
	}

	@Override
	public void setToFollowMoveable(Collisionable moveable) {
		this.follow = moveable;
	}

	@Override
	public void jumpTo(Vector2 position) {
		this.position.x = position.x;
		this.position.y = position.y;
	}
	
	/**
	 * Additional helper
	 * @param vector
	 * @return unprojected vector
	 */
	public Vector2 unproject(Vector2 vec) {
		Vector3 v = new Vector3(vec.x, vec.y, 0);
		unproject(v);
		vec.x = v.x;
		vec.y = v.y;
		return vec;
	}
	
	public Vector2 project(Vector2 vec) {
		Vector3 v = new Vector3(vec.x, vec.y, 0);
		project(v);
		vec.x = v.x;
		vec.y = v.y;
		return vec;
	}
	
}

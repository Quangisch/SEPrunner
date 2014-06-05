package core.ingame;

import gameObject.ICollisionable;
import misc.Debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Camera extends OrthographicCamera implements MoveableCamera {
	
	private ICollisionable follow;
	
	public Camera() {
		super.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());	
	}
	
	public void update() {

		super.update();
		
		if(Debug.isMode(Debug.Mode.CAMERA)) {
			Debug.processCamera(this);
			return;
		}
		
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
	
	@Override
	public void setToFollowMoveable(ICollisionable moveable) {
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

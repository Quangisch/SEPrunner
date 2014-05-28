package core.ingame;

import gameObject.Collisionable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class Camera extends OrthographicCamera implements MoveableCamera {
	
	private static Camera camera;
	private Collisionable follow;
	
	private Camera() {
//		super.setToOrtho(false, GameProperties.pixelToMeter(Gdx.graphics.getWidth()), GameProperties.pixelToMeter(Gdx.graphics.getHeight()));
		super.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		
	}
	
	public void update() {
		super.update();
		if(follow == null)
			return;
		
		
		float toX = follow.getX()+GameProperties.width/3;
		float toY = follow.getY()+GameProperties.height/5;
		
		//TODO
		if(this.position.x != toX) {
			this.position.x = toX;
		}
		
		if(this.position.y != toY) {
			this.position.y = toY;
		}
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
	
	
}

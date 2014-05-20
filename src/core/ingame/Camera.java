package core.ingame;

import gameObject.Moveable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class Camera extends OrthographicCamera implements MoveableCamera {
	
	private static Camera camera;
	
	private Camera() {
		super.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public void update() {
		super.update();
		//relativ zur Spielfigur bewegen
	}
	
	public static Camera getInstance() {
		if(camera == null)
			camera = new Camera();
		return camera;
	}

	@Override
	public void setToFollowMoveable(Moveable moveable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jumpTo(Vector2 position) {
		// TODO Auto-generated method stub
		
	}
	
	
}

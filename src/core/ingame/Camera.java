package core.ingame;

import gameObject.body.IBodyInitializer;
import gameObject.interaction.InteractionState;
import misc.Debug;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import core.GameProperties;
import core.GameProperties.GameState;

public class Camera extends OrthographicCamera implements MoveableCamera {
	
	private IBodyInitializer follow;
	
	public Camera() {
		super.setToOrtho(false, GameProperties.SCALE_WIDTH, GameProperties.SCALE_HEIGHT);	
	}
	
	public void update() {
		super.update();
		
		if(Debug.isMode(Debug.Mode.CAMERA)) {
			Debug.processCamera(this);
			return;
		}
		
		if(GameProperties.gameState.equals(GameState.WIN)) {
			if(zoom > 1.7f)
				zoom -= 0.01f;
			return;
		}
		
		if(follow == null)
			return;

		if(follow.getParent().getInteractionState().equals(InteractionState.STAND)){
			if(this.zoom >=1.2f){
				this.zoom = zoom - 0.005f;
			}
		}else{
			if(this.zoom <=1.5f){
				this.zoom = zoom + 0.01f;
			}
		}
		
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
	
	@Override
	public void setToFollowMoveable(IBodyInitializer moveable) {
		this.follow = moveable;
	}
	
	/**
	 * Additional helper
	 * @param vector
	 * @return unprojected vector
	 */
	public Vector2 unproject(Vector2 vec) {
		Vector3 v = new Vector3(vec.x, vec.y, 0);
		super.unproject(v);
		vec.x = v.x;
		vec.y = v.y;
		return vec;
	}
	
	public Vector2 project(Vector2 vec) {
		Vector3 v = new Vector3(vec.x, vec.y, 0);
		super.project(v);
		vec.x = v.x;
		vec.y = v.y;
		return vec;
	}
	
}

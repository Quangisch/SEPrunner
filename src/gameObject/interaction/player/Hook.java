package gameObject.interaction.player;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.Sensor;
import gameObject.interaction.GameObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import core.GameProperties;

public class Hook extends GameObject implements IHookable {

	private float ttl;
	private Vector2 direction;
	private Vector2 origin;
	private final float SCALE = 0.3f;
	private final float SPEED = 7;
	private final float INITIAL_TTL = 1;
	private GameObject parent;
	
	public Hook(GameObject parent) {
		super(parent.getGameWorld(), parent.getBodyObject().getPosition());
		init("res/objects/textures/hook.png", "res/objects/hook.json");

		this.parent = parent;
		
		getBodyObject().setGravityScale(0);
		getBodyObject().setBullet();
		getAnimationObject().setScale(SCALE);
		getAnimationObject().setVisible(false);
		getGameWorld().addGameObject(this);
	}
	

	public void run() {
		if(!getAnimationObject().isVisible())
			return;
		
		super.run();
		
		ttl -= Gdx.graphics.getDeltaTime();
		if(ttl <= 0 || getBodyObject().getPosition().dst(origin) > parent.getHookRadius())
			deactivate();
	}
	
	public void activate(Vector2 point) {
		origin = GameProperties.meterToPixel(parent.getBodyObject().getLocalCenterInWorld());
		direction = GameProperties.pixelToMeter(point.sub(origin)).nor().scl(SPEED);
		getBodyObject().setRotation((float)(Math.atan2(point.y,  point.x) / Math.PI * 180));
		getBodyObject().setPositionInMeter(parent.getBodyObject().getLocalCenterInWorld());
		getBodyObject().applyImpulse(direction);
		getAnimationObject().setVisible(true);
		parent.getAnimationObject().setFlip(direction.x < 0);
		
		ttl = INITIAL_TTL;
	}
	
	private void deactivate() {
		getAnimationObject().setVisible(false);
//		getBodyObject().applyImpulse(direction.scl(-1));
		getBodyObject().resetVelocity();
	}

	public void dispose() {
		super.dispose();
	}
	
	@Override
	public boolean handleCollision(boolean start, boolean postSolve, Sensor mySensor, BodyObject other, Sensor otherSensor) {
		if (!postSolve && start && other.getBodyObjectType().equals(BodyObjectType.Ground)) {
			deactivate();
			return true;
		}
		return false;
	}
	
}

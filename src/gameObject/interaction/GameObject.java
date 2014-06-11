package gameObject.interaction;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.ICollisionable;
import gameObject.body.IIdentifiable;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.body.Sensor;
import gameObject.drawable.AnimationObject;
import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class GameObject extends ObjectInitializer 
	implements ICollisionable, Runnable, Disposable, 
	Comparable<GameObject>, IIdentifiable, IGameObject{
	
	private int shuriken = 10;
	
//	TODO -> texture loading to ResourceManager
	private int grounded, bodyBlocked;
	private boolean canHide;
	private GameObject canPull, canDispose;
	private GameWorld gameWorld;

	public GameObject(GameWorld gameWorld, Vector2 position) {
		iniLink(new AnimationObject(position), 
				new BodyObject(gameWorld.getWorld(), position, this));
		this.gameWorld = gameWorld;
	}
	
	public void run() {
		// update position, where to draw depending on position of bodyObject
		getAnimationObject().setPosition(getBodyObject().getPosition());
	}

	@Override
	public void init(String name) {
		init(name, "res/sprites/" + name + ".json");
	}

	private void calcBodyBlockedContact(boolean start) {
		bodyBlocked += start ? 1 : -1;
	}
	
	private void calcGroundedContact(boolean start) {
		grounded += start ? 1 : -1;
	}
	
	@Override
	public boolean handleCollision(boolean start, Sensor mySensor,
			BodyObject other, Sensor otherSensor) {
		boolean handled = false;
		
		if(!handled && mySensor != null) {
			
			if(other.getBodyObjectType().equals(BodyObjectType.Ground)) {
				switch(mySensor.getSensorType()) {
				case SensorTypes.BODY :
					calcBodyBlockedContact(start);
					return true;
				case SensorTypes.FOOT :
					calcGroundedContact(start);
					return true;
					
				default:
					break;
				}
			
//				TODO testing
			} else if(other.getBodyObjectType().equals(BodyObjectType.Enemy)
					&& other.getParent().isStunned() && !isGrabbing()) {
				canPull = start ? other.getParent() : null;
				
//				TODO testing
			} else if(other.getBodyObjectType().equals(BodyObjectType.Hideable)
					&& canPull != null && isGrabbing()) {
				canDispose = start ? other.getParent() : null;
			}
				
			
		}
		
		
		return handled;
	}
	
	@Override
	public int compareTo(GameObject other) {
		return this.getAnimationObject().getLayer() - other.getAnimationObject().getLayer();
	}
	
	public void dispose() {
		getBodyObject().dispose();
//		disposeTextures();
	}
	
	@Override
	public boolean isBodyBlocked() {
		return bodyBlocked > 0;
	}

	@Override
	public boolean isGrounded() {
		return grounded > 0;
	}

	@Override
	public BodyObjectType getBodyObjectType() {
		return getBodyObject().getBodyObjectType();
	}

	@Override
	public void setBodyObjectType(BodyObjectType bodyObjectType) {
		getBodyObject().setBodyObjectType(bodyObjectType);
	}
	
	@Override
	public GameWorld getGameWorld() {
		return gameWorld;
	}
	
	@Override
	public boolean decShuriken() {
		if(shuriken <= 0)
			return false;
			
		shuriken--;
		return true;
	}
	
	@Override
	public int getShurikenQuantity() {
		return shuriken;
	}
	
	@Override
	public AnimationObject getAnimationObject() {
		return super.getAnimationObject();
	}
	
	@Override
	public BodyObject getBodyObject() {
		return super.getBodyObject();
	}
	
	@Override
	public boolean isInAction() {
		if(isGrabbing() || isHiding() || isHooking() || isThrowing())
			return true;
		return false;
	}
	
	@Override
	public boolean canHide() {
		return canHide;
	}
	
	@Override
	public boolean canPull() {
		return canPull != null;
	}
	
	@Override
	public boolean canDispose() {
		return canPull() && canDispose != null;
	}
	
}

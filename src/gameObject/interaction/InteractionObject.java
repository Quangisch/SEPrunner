package gameObject.interaction;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.ICollisionable;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.body.Sensor;
import gameWorld.GameWorld;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import misc.Debug;
import misc.GeometricObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import core.GameProperties;

public abstract class InteractionObject 
		extends InteractionManager 
		implements ICollisionable, RayCastCallback, IInteractable {
	
	private int grounded, bodyBlocked;
	private boolean hookable;
	private GameObject grabTarget, disposeTarget, hideTarget;

	private Vector2 hookPoint;
	private List<Vector2> hookPoints = new LinkedList<Vector2>();
	private int shuriken, hookRadius;
	private int shurikenThrown, enemiesHidden, unseen;
	private Set<GameObject> hiddenFrom = new HashSet<GameObject>();
	
	public void wasHiddenFrom(GameObject gameObject) {
		if(!hiddenFrom.contains(gameObject)) {
			unseen++;
			hiddenFrom.add(gameObject);
		}
	}
	
	protected InteractionObject(GameWorld gameWorld) {
		super(gameWorld);
	}
	
	private void calcBodyBlockedContact(boolean start) {
		bodyBlocked += start ? 1 : -1;
	}
	
	private void calcGroundedContact(boolean start) {
		grounded += start ? 1 : -1;
	}
	
	protected void processInteractionTransitions() {
		processGrabTarget();
		processHideTarget();
		
		if(this.getBodyObject().getBodyObjectType().equals(BodyObjectType.Player)) {
//			System.out.println(String.format("thrown:%d, hidden:%d, unseen:%d", shurikenThrown, enemiesHidden, unseen));
		
		}
	}
	
	private void processGrabTarget() {
		if(isGrabbing()) {
//			TODO hack
			if(grabTarget == null && getBodyObject().getJointGameObject() != null)
				grabTarget = getBodyObject().getJointGameObject();
			
			if(getInteractionState().equals(InteractionState.GRAB_PULL) 
					&& !grabTarget.getInteractionState().equals(InteractionState.PULLED))
				grabTarget.applyInteraction(InteractionState.PULLED);
			
			if(!getInteractionState().equals(InteractionState.GRAB_PULL)
					&& !grabTarget.getInteractionState().equals(InteractionState.STUNNED))
				grabTarget.applyInteraction(InteractionState.STUNNED);
		} else
			disposeTarget = null;
	}
	
	private void processHideTarget() {
		if(isHiding() && hideTarget != null) {
			hideTarget.getAnimationObject().setActive(false);
			if(getInteractionState().equals(InteractionState.HIDE_END))
				hideTarget.getAnimationObject().setActive(true);
		}
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
	public boolean decShuriken() {
		if(shuriken <= 0)
			return false;
		shurikenThrown++;
		shuriken--;
		return true;
	}
	
	@Override
	public int getShurikenQuantity() {
		return shuriken;
	}
	
	protected void setShurikenQuantity(int shuriken) {
		this.shuriken = shuriken;
	}
	
	
	@Override
	public boolean canHide() {
		return hideTarget != null;
	}
	
	@Override
	public boolean canGrab() {
		return grabTarget != null;
	}
	
	@Override
	public boolean canDispose() {
		return canGrab() && disposeTarget != null;
	}
	
	@Override
	public boolean startGrab() {
		if(canGrab()) {
			getBodyObject().joinBodies(grabTarget.getBodyObject());
			return true;
		}
		return false;
	}
	
	@Override
	public boolean endGrab() {
		if(isGrabbing()) {
			applyInteraction(getDefaultInteractionState());
			grabTarget = getBodyObject().uncoupleBodies();
			return true;
		}
		return false;
	}
	

	
	@Override
	public boolean disposeGrab() {
		if(isGrabbing() && canDispose()) {
			endGrab();
			grabTarget.dispose();
			grabTarget = null;
			enemiesHidden++;
			return true;
		}
		return false;
	}
	
	@Override
	public int getHookRadius() {
		return hookRadius;
	}
	
	public int getShurikenThrown() {
		return shurikenThrown;
	}
	
	public int getEnemiesHidden() {
		return enemiesHidden;
	}
	
	public int getUnseenFrom() {
		return unseen;
	}
	
	@Override
	public void setHookRadius(int hookRadius) {
		this.hookRadius = hookRadius;
	}
	
	@Override
	public Vector2 getHookPoint() {
		return hookPoint;
	}

	@Override
	public boolean tryToHook(Vector2 clickPoint) {
		if(!hookable)
			return false;
		
		Vector2 endPoint = clickPoint.cpy();
		endPoint = GameProperties.pixelToMeter(endPoint);
		Vector2 startPoint = getBodyObject().getLocalCenterInWorld();
		float hRiM = GameProperties.pixelToMeter(hookRadius);
		hookPoints.clear();
		getGameWorld().getWorld().rayCast(this, 
				startPoint,endPoint.sub(startPoint).clamp(hRiM, hRiM).add(startPoint));
		
		if(hookPoints.size() > 0) {
			hookPoint = hookPoints.get(0);
			for(Vector2 v : hookPoints)
				if(startPoint.dst(hookPoint) < startPoint.dst(v))
					hookPoint = v;
		
			if(hookPoint != null) {
//				Vector2 p = GameProperties.meterToPixel(hookPoint);
//				new GeometricObject(new Circle(p.x - 5, p.y - 5, 5), Color.GREEN);
//				System.out.println("hookPoint @"+hookPoint.toString());
				getBodyObject().applyImpulse(new Vector2(0,7));
				getBodyObject().setGravityScale(0);
				hookable = false;
				return true;
			}
		} 
		return false;
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		
		if (((BodyObject) fixture.getBody().getUserData()).getBodyObjectType().equals(BodyObjectType.Ground)) {
			hookPoints.add(point);
			
			if (Debug.isMode(Debug.Mode.GEOMETRIC)) {
				Vector2 p = GameProperties.meterToPixel(point);
				new GeometricObject(new Circle(p.x - 7, p.y - 7, 7), Color.BLUE);
			}

			return fraction;
		} else
			return 1;
	}
	
	private void resetHook() {
		hookPoint = null;
		getBodyObject().setGravityScale(1);
	}
	
//	COLLISION HANDLING
	@Override
	public boolean handleCollision(boolean start, boolean postSolve, Sensor mySensor,
			BodyObject other, Sensor otherSensor) {
		boolean handled = false;
		
//		POSTSOLVE
		if(postSolve) {
			if(other.getBodyObjectType().equals(BodyObjectType.Ground) 
					&& getInteractionState().equals(InteractionState.HOOK_FLY)) {
				resetHook();
				return true;
			}
			
//		BEGIN/END
		} else {

			if(!handled && mySensor != null) {
				
				if(other.getBodyObjectType().equals(BodyObjectType.Ground)) {
					switch(mySensor.getSensorType()) {
					case SensorTypes.BODY :
						calcBodyBlockedContact(start);
						return true;
					case SensorTypes.FOOT :
						calcGroundedContact(start);
						if(!hookable && isGrounded())
							hookable = true;
						return true;
					default:
						break;
					}
				

				} else if(other.getBodyObjectType().equals(BodyObjectType.Enemy)
						&& other.getParent().isStunned() && !isGrabbing()) {
					grabTarget = start ? other.getParent() : null;
					other.getParent().getAnimationObject().setActive(canGrab());	
					
				} else if(other.getBodyObjectType().equals(BodyObjectType.Hideable)
						&& grabTarget != null && isGrabbing()) {
					disposeTarget = start ? other.getParent() : null;
					other.getParent().getAnimationObject().setActive(canDispose());
					return true;
					
				} else if(other.getBodyObjectType().equals(BodyObjectType.Hideable)
						&& !isInAction()) {
					hideTarget = start ? other.getParent() : null;
					other.getParent().getAnimationObject().setActive(canHide());
					return true;
					
				} 
			}
		}
		
		return handled;
	}

}

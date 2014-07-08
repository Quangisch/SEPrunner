package gameObject.interaction;

import gameObject.GameObject;
import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.body.Sensor;
import gameObject.interaction.player.Hook;
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

public abstract class InteractionObject extends InteractionManager implements RayCastCallback, ICollisionable {

	private int groundedCounter, bodyBlockedCounter, grabCounter, hideCounter;
	private boolean hookable;
	protected GameObject grabTarget, hideTarget;
	private Hook hook;

	private Vector2 hookPoint;
	private List<Vector2> hookPoints = new LinkedList<Vector2>();
	private int shuriken, hookRadius;
	private int shurikenThrown, enemiesHidden;
	private Set<BodyObject> hiddenFrom = new HashSet<BodyObject>();
	private int feetGrounded;
	
	protected InteractionObject(GameWorld gameWorld) {
		super(gameWorld);
	}
	
	private void calcFeetsContact(boolean start) {
		feetGrounded += start ? 1 : -1;
	}
	
	private void calcBodyBlockedContact(boolean start) {
		bodyBlockedCounter += start ? 1 : -1;
	}
	
	private void calcGroundedContact(boolean start) {
		groundedCounter += start ? 1 : -1;
	}
	
	private void calcHideContact(boolean start) {
		hideCounter += start ? 1 : -1;
	}
	
	private void calcGrabContact(boolean start) {
		grabCounter += start ? 1 : -1;
	}
	
	protected void processInteractionTransitions() {
		processGrabTarget();
		processHideTarget();
		
		if(this.getBodyObject().getBodyObjectType().equals(BodyObjectType.Player)) {
//			System.out.println(String.format("thrown:%d, hidden:%d, unseen:%d", shurikenThrown, enemiesHidden, unseen));
//			System.out.println(feetGrounded + " "+areBothFeetsGrounded());
//			System.out.println("grabC@"+grabCounter+", hideC@"+hideCounter+"->"+hideTarget);
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
			
			if(grabTarget.hideTarget != null && hideTarget == null) { 
				hideTarget = grabTarget.hideTarget;
				hideTarget.getAnimationObject().setActive(true);
			}
			grabTarget.getBodyObject().setGravityScale(0.01f);
			grabTarget.getAnimationObject().setActive(false);
		}
	}
	
	private void processHideTarget() {
		if(hideCounter <= 0 && hideTarget != null) {
			hideCounter = 0;
			hideTarget = null;
		}
		
		if(isHiding() && hideTarget != null) {
			hideTarget.getAnimationObject().setActive(false);
			if(getInteractionState().equals(InteractionState.HIDE_END))
				hideTarget.getAnimationObject().setActive(true);
		}
	}
	
	@Override
	public boolean isBodyBlocked() {
		return bodyBlockedCounter > 0;
	}

	@Override
	public boolean isGrounded() {
		return groundedCounter > 0 || feetGrounded > 0;
	}
	
	@Override
	public boolean areBothFeetsGrounded() {
		return feetGrounded == 2;
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
		if(hideTarget != null)
			if(Math.abs(hideTarget.getBodyObject().getX() - getBodyObject().getX()) < 100)
				return true;
		hideTarget = null;
		return false;
	}
	
	@Override
	public boolean canGrab() {
		return grabTarget != null;
	}
	
	@Override
	public boolean canDispose() {
		return canGrab() && hideTarget != null;
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
			grabCounter = 0;
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
		return hiddenFrom.size();
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
		
		if(hook == null)
			hook = new Hook((GameObject) this);
		
		Vector2 endPoint = clickPoint.cpy();
		endPoint = GameProperties.pixelToMeter(endPoint);
		Vector2 startPoint = getBodyObject().getLocalCenterInWorld();
		float hRiM = GameProperties.pixelToMeter(hookRadius);
		hookPoints.clear();
		getGameWorld().getWorld().rayCast(this, 
				startPoint,endPoint.sub(startPoint).clamp(hRiM, hRiM).add(startPoint));
		getAnimationObject().setFlip(clickPoint.x < getBodyObject().getX());
		
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
				
				hook.activate(GameProperties.meterToPixel(hookPoint));
				
				return true;
			}
		} 
		
		hook.activate(clickPoint);
		
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
					case SensorTypes.LEFT_FOOT :
					case SensorTypes.RIGHT_FOOT :
						calcFeetsContact(start);
						return true;
					default:
						break;
					}
					
//				GRABTARGET
				} else if(mySensor.getSensorType() == SensorTypes.BODY
						&& otherSensor != null && otherSensor.getSensorType() == SensorTypes.BODY
						&& other.getBodyObjectType().equals(BodyObjectType.Enemy)) {
					calcGrabContact(start);
					grabTarget = grabCounter > 0 && other.getParent().isStunned() && !isGrabbing() 
									? other.getParent() : null;
					
					if(getBodyObject().getBodyObjectType().equals(BodyObjectType.Player))
						other.getParent().getAnimationObject().setActive(canGrab());
					
//				CALC HIDDEN FROM
				} else if(other.getBodyObjectType().equals(BodyObjectType.Enemy)
						&& isHiding() && hiddenFrom.add(other)) {
					return true;
					
//				HIDEABLE	
				} else if(other.getBodyObjectType().equals(BodyObjectType.Hideable)) {
					calcHideContact(start);
					hideTarget = hideCounter > 0 ? other.getParent() : null;
					
					if(getBodyObject().getBodyObjectType().equals(BodyObjectType.Player))
						other.getParent().getAnimationObject().setActive(canHide());
					return true;
				} 
			}
		}
		
		return handled;
	}

}

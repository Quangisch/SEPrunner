package core.ingame.input.interaction;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.interaction.GameObject;
import gameObject.interaction.InteractionState;
import gameObject.interaction.player.Shuriken;

import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap.ActionKey;

public class ActionMovement implements RayCastCallback {
	
	private GameObject gameObject;
	
	protected ActionMovement(GameObject gameObject) {
		this.gameObject = gameObject;
	}
	
	protected InteractionState process(Set<ActionKey> actions, Click click) {
		InteractionState nextState = null;
		nextState = end(actions);
		if(nextState == null)
			nextState = begin(actions, click);
		return nextState;
	}
	
//	BEGIN
	private InteractionState begin(Set<ActionKey> actions, Click click) {
		boolean inAction = gameObject.isInAction();
		InteractionState nextState = null;
		
		if(!inAction){
			if(click != null) {
				if(actions.contains(ActionKey.HOOK))
					nextState = processHook(click);
				if(nextState == null && actions.contains(ActionKey.THROW))
					nextState = processThrow(click);
			}
			
			if(nextState == null && actions.contains(ActionKey.ACTION))
				nextState = processActionKey();
		}
		
		return nextState;
	}
	
//	END
	private InteractionState end(Set<ActionKey> actions) {
		switch(gameObject.getInteractionState()) {	
		case GRAB:
		case GRAB_PULL:
			if(!actions.contains(ActionKey.ACTION))
				return InteractionState.CROUCH_STAND;
			else
				return null;
			
		case HIDE:
			if(!actions.contains(ActionKey.ACTION))
				return InteractionState.HIDE_END;
			else
				return null;
			
		default:
			return null;
		
		}
	}
	
//	HOOK
	private InteractionState processHook(Click click) {
		
		return null;
	}
	
//	THROW
	private InteractionState processThrow(Click click) {
		if(gameObject.decShuriken()) {
			Vector2 clickPoint = getClickPoint(click);
			new Shuriken(gameObject, clickPoint);
			gameObject.getAnimationObject().setFlip(click.screenX < gameObject.getBodyObject().getX());
			return InteractionState.THROW;
		}
		return null;
	}
	
//	ACTIONKEY
	private InteractionState processActionKey() {
		if(gameObject.canHide() && !(gameObject.isHiding() || gameObject.isHiding()))
			return InteractionState.HIDE_START;
		
		if(gameObject.canGrab() && !gameObject.isGrabbing() && gameObject.startGrab())
			return InteractionState.GRAB;
		
		if(gameObject.isGrabbing() && !gameObject.canDispose() && gameObject.endGrab())
			return InteractionState.STAND;
	
		if(gameObject.canDispose() && gameObject.isGrabbing() && gameObject.disposeGrab())
			return InteractionState.GRAB_DISPOSE;
		
		return null;
	}
	
// CLICKPOINT
	private Vector2 getClickPoint(Click click) {
		Vector2 clickPoint = new Vector2(click.screenX, click.screenY);
		gameObject.getGameWorld().getCamera().unproject(clickPoint);
		return clickPoint;
	}	
	
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		
		if (((BodyObject) fixture.getBody().getUserData()).getBodyObjectType().equals(BodyObjectType.Ground)) {

//			if (Debug.isMode(Debug.Mode.GEOMETRIC)) {
//				Vector2 p = GameProperties.meterToPixel(point);
//				new GeometricObject(new Circle(p.x - 5, p.y - 5, 5), Color.BLUE);
//			}

			return 0;
		} else
			return 1;
	}

}

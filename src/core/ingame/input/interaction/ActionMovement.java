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

import core.ingame.input.IInputHandler;
import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap.ActionKey;

public class ActionMovement implements RayCastCallback {
	
	private IInputHandler iHandler;
	private GameObject gameObject;
	
	protected ActionMovement(IInputHandler iHandler, GameObject gameObject) {
		this.iHandler = iHandler;
		this.gameObject = gameObject;
	}
	
	protected InteractionState process(Set<ActionKey> actions) {
		InteractionState nextState = null;
		nextState = end(actions);
		if(nextState == null)
			nextState = begin(actions);
		return nextState;
	}
	
//	BEGIN
	private InteractionState begin(Set<ActionKey> actions) {
		boolean inAction = gameObject.isInAction();
		InteractionState nextState = null;
		
		if(!inAction){
			if(iHandler.getClick() != null) {
				if(actions.contains(ActionKey.HOOK))
					nextState = processHook(iHandler.popClick());
				else if(actions.contains(ActionKey.THROW))
					nextState = processThrow(iHandler.popClick());
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
		Vector2 clickPoint = getClickPoint(click);
		// can't throw if click and directionActionKey are in opposite direction (pov gameObject)
		if((clickPoint.x < gameObject.getBodyObject().getX() && iHandler.isKeyDown(ActionKey.RIGHT))
				|| (clickPoint.x > gameObject.getBodyObject().getX() && iHandler.isKeyDown(ActionKey.LEFT)))
				return null;
		
		if(gameObject.decShuriken()) {
			new Shuriken(gameObject, clickPoint);
			gameObject.getAnimationObject().setFlip(clickPoint.x < gameObject.getBodyObject().getX());
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

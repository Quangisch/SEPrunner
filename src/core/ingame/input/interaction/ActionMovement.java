package core.ingame.input.interaction;

import gameObject.drawable.AnimationObject;
import gameObject.interaction.GameObject;
import gameObject.interaction.InteractionState;
import gameObject.interaction.player.Shuriken;

import java.util.Set;

import com.badlogic.gdx.math.Vector2;

import core.ingame.input.IInputHandler;
import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap.ActionKey;

public class ActionMovement  {
	
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
		InteractionState nextState = null;
		
		if(!gameObject.isInAction()){	
			if(actions.contains(ActionKey.HOOK))
				nextState = processHookStart(iHandler.popClick());
			
			if(nextState == null && actions.contains(ActionKey.THROW) && iHandler.getClass() != null)
				nextState = processThrow(iHandler.popClick());
			
			if(nextState == null && actions.contains(ActionKey.ACTION))
				nextState = processActionKey();
		}

		
		if(gameObject.isHooking())
			nextState = processHooking();
		if(gameObject.isGrabbing())
			nextState = processGrabbing();
		processHiding();
		

		
		
		return nextState;
	}
	
//	END
	private InteractionState end(Set<ActionKey> actions) {
		switch(gameObject.getInteractionState()) {	
		case GRAB_START:
			return InteractionState.GRAB;
		case GRAB_END:
			return InteractionState.STAND;
		case GRAB_PULL:
			if(!(actions.contains(ActionKey.LEFT) || actions.contains(ActionKey.RIGHT)))
				return InteractionState.GRAB;
			return null;
		case GRAB_DISPOSE:
//			if(gameObject.isInteractionFinished())
//			System.out.println("disposeStand");
//				gameObject.applyInteraction(InteractionState.STAND);
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
	private InteractionState processHookStart(Click click) {
		if(click != null && !gameObject.isInAction() && !gameObject.isCrouching()) {
			if(gameObject.getHookPoint() == null && click != null)
				gameObject.tryToHook(getClickPoint(click));
			
			if(gameObject.getHookPoint() != null)
				return InteractionState.HOOK;
		} 
		
		return null;
	}
	
	private InteractionState processHooking() {
		if(gameObject.getInteractionState().equals(InteractionState.HOOK)
				&& gameObject.isInteractionFinished()) {
			return InteractionState.HOOK_FLY;	
		}
		
		if(gameObject.getInteractionState().equals(InteractionState.HOOK_FLY)
				&& (gameObject.getHookPoint() == null))
			return InteractionState.JUMP;
		return null;
	}
	
//	THROW
	private InteractionState processThrow(Click click) {
		if(click != null) {
			final Vector2 clickPoint = getClickPoint(click);
			final boolean left = clickPoint.x < gameObject.getBodyObject().getX();
			
			// can't throw if click and directionActionKey are in opposite direction (pov gameObject)
			if((left && iHandler.isKeyDown(ActionKey.RIGHT))
					|| (!left && iHandler.isKeyDown(ActionKey.LEFT)))
					return null;
			
			if(gameObject.decShuriken()) {
				gameObject.getAnimationObject().setFlip(left);
				new Shuriken(gameObject, clickPoint);
				return InteractionState.THROW;
			}
		}
		return null;
	}
	
//	ACTIONKEY
	private InteractionState processActionKey() {
		if(gameObject.canGrab() && !gameObject.isGrabbing() && gameObject.startGrab()) {
			iHandler.keyUp(ActionKey.ACTION);
			return InteractionState.GRAB_START;
		}
		
		if(!gameObject.isInAction() && gameObject.canHide() && !gameObject.isHiding())
			return InteractionState.HIDE_START;
		
		return null;
	}
	
	private void processHiding() {
		AnimationObject ani = gameObject.getAnimationObject();
		if(gameObject.isHiding()) {
			if(gameObject.getInteractionState().equals(InteractionState.HIDE_START)) {
				ani.setAlpha(ani.getAlpha()-0.01f);
			} else if(gameObject.getInteractionState().equals(InteractionState.HIDE_END)) {
				ani.setAlpha(ani.getAlpha()+0.01f);
				ani.setLayer(3);
			} else
				ani.setLayer(-1);
				
		} else
			ani.setAlpha(1);
	}
	
	private InteractionState processGrabbing() {
		if(iHandler.isKeyDown(ActionKey.ACTION)) {
			if(gameObject.isGrabbing() && !gameObject.canDispose() && gameObject.endGrab()) {
				iHandler.keyUp(ActionKey.ACTION);
				return InteractionState.GRAB_END;
			}
		
			if(gameObject.canDispose() && gameObject.isGrabbing() && gameObject.disposeGrab()) {
				iHandler.keyUp(ActionKey.ACTION);
				return InteractionState.STAND;
			}
		}
		
		return null;
	}
	
// CLICKPOINT
	private Vector2 getClickPoint(Click click) {
		Vector2 clickPoint = new Vector2(click.screenX, click.screenY);
		gameObject.getGameWorld().getCamera().unproject(clickPoint);
		return clickPoint;
	}	

}

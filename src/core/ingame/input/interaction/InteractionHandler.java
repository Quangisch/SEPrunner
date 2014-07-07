package core.ingame.input.interaction;

import gameObject.interaction.GameObject;

import java.util.HashSet;
import java.util.Set;

import core.GameProperties;
import core.GameProperties.GameState;
import core.ingame.input.IInputHandler;
import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap.ActionKey;

public class InteractionHandler implements IInteractionHandler {
	
	private IInputHandler iHandler;
	private GameObject gameObject;
	private InputToInteraction inputToInteraction;
	private InteractionToWorld interactionToWorld;
	
	private Set<ActionKey> pressedActionKeys;
	
	public InteractionHandler(IInputHandler iHandler, GameObject gameObject) {
		this.iHandler = iHandler;
		this.gameObject = gameObject;
		
		inputToInteraction = new InputToInteraction(this, iHandler);
		interactionToWorld = new InteractionToWorld(this, iHandler);
		
		pressedActionKeys = new HashSet<ActionKey>();
	}
	
	public void run() {
		if(!GameProperties.isCurrentGameState(GameState.NORMAL))
			return;
		processPressedActionKeys();
		processFlip();
		inputToInteraction.process();
		interactionToWorld.process();
	}
	
	private void processFlip() {
		if(!gameObject.isGrabbing()) {
			if(pressedActionKeys.contains(ActionKey.LEFT) && !gameObject.getAnimationObject().isFlipped()) {
				gameObject.getAnimationObject().setFlip(true);
				iHandler.keyUp(ActionKey.RUN);
			} else if(pressedActionKeys.contains(ActionKey.RIGHT) && gameObject.getAnimationObject().isFlipped()) {
				gameObject.getAnimationObject().setFlip(false);
				iHandler.keyUp(ActionKey.RUN);
			}
		}
	}

	
	protected void processPressedActionKeys() {
		for(ActionKey aK : ActionKey.values()) {
			if(iHandler.isKeyDown(aK) || iHandler.isButtonDown(aK))
				pressedActionKeys.add(aK);
			else
				pressedActionKeys.remove(aK);
		}
	}
	
	public boolean isOnlyCrouching() {
		return pressedActionKeys.size() == 1 && pressedActionKeys.contains(ActionKey.CROUCH);
	}
	
	protected Set<ActionKey> getPressedActionKeys() {
		return pressedActionKeys;
	}
	
	protected GameObject getGameObject() {
		return gameObject;
	}
	
	protected Click getClick() {
		return iHandler.getClick();
	}
	
	protected Click popClick() {
		return iHandler.popClick();
	}
	
//	@Override
	public void setForceMultiplier(float walkMul, float runMul, float sneakMul, float pullMull) {
		interactionToWorld.setForceMultiplier(walkMul, runMul, sneakMul, pullMull);
	}

}

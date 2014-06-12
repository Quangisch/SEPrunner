package core.ingame.input.interaction;

import gameObject.interaction.GameObject;

import java.util.HashSet;
import java.util.Set;

import core.ingame.input.IInputHandler;
import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap.ActionKey;

public class InteractionHandler implements Runnable {
	
	private IInputHandler iHandler;
	private GameObject gameObject;
	private InputToInteraction inputToInteraction;
	private InteractionToWorld interactionToWorld;
	
	private Set<ActionKey> pressedActionKeys;
	
	public InteractionHandler(IInputHandler iHandler, GameObject gameObject) {
		this.iHandler = iHandler;
		this.gameObject = gameObject;
		
		inputToInteraction = new InputToInteraction(this);
		interactionToWorld = new InteractionToWorld(this, iHandler);
		
		pressedActionKeys = new HashSet<ActionKey>();
	}
	
	public void run() {
		processPressedActionKeys();
		processFlip();
		
		inputToInteraction.process();
		interactionToWorld.process();
	}
	
	private void processFlip() {
		gameObject.getAnimationObject().setFlip(pressedActionKeys.contains(ActionKey.LEFT));
	}

	
	protected void processPressedActionKeys() {
		for(ActionKey aK : ActionKey.values()) {
			if(iHandler.isKeyDown(aK) || iHandler.isButtonDown(aK))
				pressedActionKeys.add(aK);
			else
				pressedActionKeys.remove(aK);
		}
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

}
package gameObject.interaction.player;

import gameObject.body.BodyObjectType;
import gameWorld.GameWorld;
import misc.Debug;

import com.badlogic.gdx.math.Vector2;

import core.ingame.input.IInputHandler;
import core.ingame.input.InteractionHandler;
import core.ingame.input.interaction.InputToInteraction;

public class Player extends PlayerCollision {

//	private InteractionHandler interactionHandler;
	private InputToInteraction inputInteraction;
	
	public Player(IInputHandler inputHandler, GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
//		interactionHandler = new InteractionHandler(inputHandler, this);
		inputInteraction = new InputToInteraction(inputHandler, this);
		getGameWorld().getCamera().setToFollowMoveable(getBodyObject());
	}

	public void run() {
		if(Debug.isMode(Debug.Mode.CAMERA))
			return;
		super.run();
//		interactionHandler.run();
		inputInteraction.run();
	}

	
	@Override
	public void init(String name) {
		super.init(name);
		setBodyObjectType(BodyObjectType.Player);
		getAnimationObject().setLayer(3);
		
	}

}

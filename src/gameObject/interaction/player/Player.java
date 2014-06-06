package gameObject.interaction.player;

import gameObject.body.GameObjectType;
import gameWorld.GameWorld;
import misc.Debug;

import com.badlogic.gdx.math.Vector2;

import core.ingame.input.IInputHandler;
import core.ingame.input.InteractionHandler;

public class Player extends PlayerCollision {

	private InteractionHandler iHandler;
	
	public Player(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
		
		iHandler = new Interac
		setInputHandler(iHandler);
		getGameWorld().getCamera().setToFollowMoveable(this);
	}

	public void run() {
		if(Debug.isMode(Debug.Mode.CAMERA))
			return;
		super.run();
	}

	
	@Override
	public void init(String name) {
		super.init(name);
		setGameObjectType(GameObjectType.Player);
		setLayer(3);
		
	}

}

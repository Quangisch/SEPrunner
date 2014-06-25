package gameObject.interaction.player;

import gameObject.body.BodyObjectType;
import gameWorld.GameWorld;
import misc.Debug;

import com.badlogic.gdx.math.Vector2;

import core.GameProperties;
import core.PlayerProfile;
import core.ingame.input.IInputHandler;
import core.ingame.input.interaction.InteractionHandler;

public class Player extends PlayerCollision {

	private InteractionHandler interactionHandler;
	private PlayerProfile profile;
	
	public Player(IInputHandler inputHandler, GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
		interactionHandler = new InteractionHandler(inputHandler, this);
		getGameWorld().getCamera().setToFollowMoveable(getBodyObject());
	}

	public void run() {
		if(Debug.isMode(Debug.Mode.CAMERA))
			return;
		super.run();
		interactionHandler.run();
	}

	public void init(String name) {
		super.init(name);
		setBodyObjectType(BodyObjectType.Player);
		getAnimationObject().setLayer(3);
		
		System.out.println("init");
		profile = new PlayerProfile();
		setShurikenQuantity(profile.shuriken);
		setHookRadius(profile.hookRadius);
	}

	public void dispose() {
		super.dispose();
	}
	
	public void processRewards() {
		int points = GameProperties.calcStylePoints(getShurikenThrown(), getEnemiesHidden(), getUnseenFrom());
		profile.experience += points;
		profile.stylePoints += points;
	}
	
	public void saveProfile() {
		profile.shuriken = getShurikenQuantity();
		profile.hookRadius = getHookRadius();
		System.out.println("current: "+profile.toString());
		profile.saveProfile();
	}
}

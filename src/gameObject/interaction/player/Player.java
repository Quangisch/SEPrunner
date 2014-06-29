package gameObject.interaction.player;

import gameObject.body.BodyObjectType;
import gameWorld.GameWorld;
import misc.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

import core.GameProperties;
import core.GameProperties.GameState;
import core.Highscore;
import core.Highscore.Score;
import core.PlayerProfile;
import core.ingame.input.IInputHandler;
import core.ingame.input.interaction.InteractionHandler;

public class Player extends PlayerCollision {

	private InteractionHandler interactionHandler;
	private PlayerProfile profile;
	private Score score;
	
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

	public void init(JsonValue resources) {
		super.init(resources);
		setBodyObjectType(BodyObjectType.Player);
		getAnimationObject().setLayer(3);
		
		profile = new PlayerProfile();
		setShurikenQuantity(profile.shuriken);
		setHookRadius(profile.hookRadius);
	}

	public void dispose() {
		super.dispose();
	}
	
	public PlayerProfile getProfile() {
		return profile;
	}
	
	public Score getScore() {
		return score;
	}
	
	public void saveProfile() {
		int points = GameProperties.calcStylePoints(getShurikenThrown(), getEnemiesHidden(), getUnseenFrom());
		
		profile.shuriken = getShurikenQuantity();
		profile.hookRadius = getHookRadius();
		profile.experience += points;
		profile.stylePoints += points;
		
		profile.updateAndSaveProfile();
	}
	
	public void saveHighscore() {
		if(GameProperties.isCurrentGameState(GameState.WIN) && score == null) {
			
			score = new Score(GameProperties.gameScreen.INDEX, profile.name, getGameWorld().getTime());
			Highscore.addHighscore(score); // add score to local Highscore
			
//			TODO
//			HighscoreServer server = new HighscoreServer();
//			if(server.isConnected()) {
//				server.updateLocalHighscoreFile();
//				if(Highscore.getPosition(score) < MAX_SCOREPOSITION_TO_SERVER)
//					server.addHighScore(score.LEVEL_INDEX, score.PLAYER_NAME, score.TIME);
//			} else
//				System.err.println("HighscoreServer offline");
		}
	}

}

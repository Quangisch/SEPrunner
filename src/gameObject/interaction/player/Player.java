package gameObject.interaction.player;

import java.io.FileNotFoundException;
import java.io.FileReader;

import gameObject.body.BodyObjectType;
import gameWorld.GameWorld;
import misc.Debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.FilePath;
import core.ingame.input.IInputHandler;
import core.ingame.input.interaction.InteractionHandler;

public class Player extends PlayerCollision {

	private InteractionHandler interactionHandler;
	private int stylePoints;
	
	public Player(IInputHandler inputHandler, GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
		interactionHandler = new InteractionHandler(inputHandler, this);
		getGameWorld().getCamera().setToFollowMoveable(getBodyObject());
		
		
	}

	
	public void run() {
		if(Debug.isMode(Debug.Mode.CAMERA))
			return;
		super.run();
//		interactionHandler.run();
		interactionHandler.run();
		
	
	}

	public void init(String name) {
		super.init(name);
		setBodyObjectType(BodyObjectType.Player);
		getAnimationObject().setLayer(3);
		initProfile();
	}

	private boolean initProfile() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.profile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(root == null)
			return false;
		
		setShurikenQuantity(root.getInt("shuriken"));
		setHookRadius(root.getInt("hookRadius"));
		stylePoints = root.getInt("stylePoints");
		return true;
	}
	
	public void dispose() {
		saveProfile();
		super.dispose();
	}
	
	private boolean saveProfile() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.profile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		root.get("shuriken").set(getShurikenQuantity());
		root.get("hookRadius").set(getHookRadius());
		root.get("stylePoints").set(stylePoints);
		
		FileHandle file = Gdx.files.local(FilePath.profile);
		file.writeString(root.toString(), false);
		System.out.println("Profile Saved");
		return true;
	}
}

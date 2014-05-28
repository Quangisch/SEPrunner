package gameObject;

public class GameObjectData {
	
	public final int TYPE, SUBTYPE;
	public final GameObject GAMEOBJECT;
	
	public final static int GROUND = -1,
			PLAYER = 0,
			ENEMY = 1,
			SHURIKAN = 3,
			HIDEABLE = 4,
			HOOKABLE = 5;
	
	public GameObjectData(int type, int subType, GameObject gameObject) {
		this.TYPE = type;
		this.SUBTYPE = subType;
		this.GAMEOBJECT = gameObject;
	}
	
	public String toString() {
		return "GameObjectData: "+TYPE+", "+SUBTYPE; 
	}

}

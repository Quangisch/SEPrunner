package gameObject;

public class GameObjectData {
	
	public final int type, subType;
	public final static int GROUND = -1,
			PLAYER = 0,
			ENEMY = 1,
			SHURIKAN = 3,
			HIDEABLE = 4,
			HOOKABLE = 5;
	
	public GameObjectData(int type, int subType) {
		this.type = type;
		this.subType = subType;
	}
	
	public String toString() {
		return "GameObjectData: "+type+", "+subType; 
	}

}

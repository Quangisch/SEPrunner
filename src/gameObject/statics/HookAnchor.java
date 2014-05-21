package gameObject.statics;

import gameObject.GameObject;

import com.badlogic.gdx.math.Vector2;

public class HookAnchor extends GameObject implements Hookable {

	@Override
	public boolean isHookable(Vector2 currentPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Vector2 getHookPoint() {
		// TODO Auto-generated method stub
		return null;
	}

}

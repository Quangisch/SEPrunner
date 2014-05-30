package gameObject.statics;

import gameObject.GameObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class HookAnchor extends GameObject implements Hookable {

	public HookAnchor(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}

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

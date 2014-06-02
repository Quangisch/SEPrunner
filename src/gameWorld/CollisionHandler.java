package gameWorld;

import gameObject.GameObject;
import gameObject.Sensor;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import core.ingame.GameProperties;

public class CollisionHandler implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		boolean handled = handleCollision(contact, true);
		if (!handled && GameProperties.debugMode.equals(GameProperties.Debug.CONSOLE))
			System.err.println("Unhandled Collision (" + contact.toString() + ")");
	}
	
	private boolean handleCollision(Contact contact, boolean start) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

		GameObject objectA = (fixA.getBody().getUserData() instanceof GameObject) ? (GameObject) fixA.getBody()
				.getUserData() : null;
		GameObject objectB = (fixB.getBody().getUserData() instanceof GameObject) ? (GameObject) fixB.getBody()
				.getUserData() : null;
		Sensor sensorA = (fixA.getUserData() instanceof Sensor) ? (Sensor) fixA.getUserData() : null;
		Sensor sensorB = (fixB.getUserData() instanceof Sensor) ? (Sensor) fixB.getUserData() : null;

		boolean handled = false;
		handled = handled || ((sensorA != null && sensorB != null // 
				&& sensorA.getPriority() >= sensorB.getPriority() && sensorA.isActive()) // sensorB hits sensorA
				&& (sensorA.getGameObject().handleCollision(start, sensorA, sensorB.getGameObject(), sensorB)));
		handled = handled || ((sensorA != null && sensorB != null && sensorB.isActive()) // sensorA hits sensorB
				&& (sensorB.getGameObject().handleCollision(start, sensorB, sensorA.getGameObject(), sensorA)));
		handled = handled || ((sensorA != null && objectB != null && sensorA.isActive()) // sensorA hits objectB
				&& (sensorA.getGameObject().handleCollision(start, sensorA, objectB, null)));
		handled = handled || ((objectA != null && sensorB != null && sensorB.isActive()) // sensorB hits objectA
				&& (sensorB.getGameObject().handleCollision(start, sensorB, objectA, null)));
		handled = handled || ((objectA != null && objectB != null) // objectA hits objectB
				&& (objectA.handleCollision(start, null, objectB, null)));
		handled = handled || ((objectA != null && objectB != null) // objectB hits objectA
				&& (objectB.handleCollision(start, null, objectA, null)));

		return handled;
		
	}

	@Override
	public void endContact(Contact contact) {
		handleCollision(contact, false);

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}
}

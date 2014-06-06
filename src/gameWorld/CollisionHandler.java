package gameWorld;

import gameObject.body.BodyObject;
import gameObject.body.GameObjectType;
import gameObject.body.Sensor;
import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionHandler implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		boolean handled = handleCollision(contact, true);
		if (!handled) Debug.println("Unhandled Collision (" + contact.toString() + ")", Mode.CONSOLE);
	}

	private boolean handleCollision(Contact contact, boolean start) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

		if (fixA == null || fixB == null || fixA.getBody() == null || fixB.getBody() == null) return false;

		BodyObject objectA = (fixA.getBody().getUserData() instanceof BodyObject) ? (BodyObject) fixA.getBody()
				.getUserData() : null;
		BodyObject objectB = (fixB.getBody().getUserData() instanceof BodyObject) ? (BodyObject) fixB.getBody()
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

	public static class MoverContactFilter implements ContactFilter {

		@Override
		public boolean shouldCollide(Fixture fixA, Fixture fixB) {
			BodyObject objectA = (fixA.getBody().getUserData() instanceof BodyObject) ? (BodyObject) fixA.getBody()
					.getUserData() : null;
			BodyObject objectB = (fixB.getBody().getUserData() instanceof BodyObject) ? (BodyObject) fixB.getBody()
					.getUserData() : null;

			boolean isMovableA = objectA == null || objectA.getGameObjectType().equals(GameObjectType.Player)
					|| objectA.getGameObjectType().equals(GameObjectType.Enemy);

			boolean isMovableB = objectB == null || objectB.getGameObjectType().equals(GameObjectType.Player)
					|| objectB.getGameObjectType().equals(GameObjectType.Enemy);

			return !(isMovableA && isMovableB) || fixA.isSensor() || fixB.isSensor();
		}
	}
}

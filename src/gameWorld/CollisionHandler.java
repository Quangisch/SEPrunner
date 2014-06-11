package gameWorld;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.Sensor;
import gameObject.interaction.GameObject;
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

		GameObject objectA = ((BodyObject)fixA.getBody().getUserData()).getParent();
		GameObject objectB = ((BodyObject)fixB.getBody().getUserData()).getParent();
				
		Sensor sensorA = (fixA.getUserData() instanceof Sensor) ? (Sensor) fixA.getUserData() : null;
		Sensor sensorB = (fixB.getUserData() instanceof Sensor) ? (Sensor) fixB.getUserData() : null;

		if(objectA != null && objectB != null) {
			if(sensorA != null && sensorB != null)
				return sensorA.getPriority() >= sensorB.getPriority()
							? objectA.handleCollision(start, sensorA, objectB, sensorB)
							: objectB.handleCollision(start, sensorB, objectA, sensorA);
			else if(sensorA == null && sensorB != null)
				return objectB.handleCollision(start, sensorB, objectA, sensorA);
			else
				return objectA.handleCollision(start, sensorA, objectB, sensorB);
		} else if(objectA != null && objectB == null) {
			return objectA.handleCollision(start, sensorA, objectB, sensorB);
		} else if(objectA == null && objectB != null) {
			return objectB.handleCollision(start, sensorB, objectA, sensorA);
		}
		
//		unhandled collision			
		return false;
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
					
			return !(isMovable(objectA) && isMovable(objectB)) || fixA.isSensor() || fixB.isSensor();
		}
		
		private boolean isMovable(BodyObject object) {
			return object == null 
					|| object.getBodyObjectType().equals(BodyObjectType.Player)
					|| object.getBodyObjectType().equals(BodyObjectType.Enemy);
		}
	}
}

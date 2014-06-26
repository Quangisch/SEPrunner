package gameWorld;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.Sensor;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionHandler implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		handleCollision(contact, true, false);
	}

	private boolean handleCollision(Contact contact, boolean start, boolean postSolve) {
		
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

		if (fixA == null || fixB == null || fixA.getBody() == null || fixB.getBody() == null) 
			return false;

		BodyObject bodyA = (BodyObject)fixA.getBody().getUserData();
		BodyObject bodyB = (BodyObject)fixB.getBody().getUserData();
	
		Sensor sensorA = (fixA.getUserData() instanceof Sensor) ? (Sensor) fixA.getUserData() : null;
		Sensor sensorB = (fixB.getUserData() instanceof Sensor) ? (Sensor) fixB.getUserData() : null;

		boolean handled = false;
		
		if(bodyA.getParent() != null && bodyB.getParent() != null) {
			if(sensorA != null && sensorB != null)
				handled = sensorA.getPriority() >= sensorB.getPriority()
							? bodyA.getParent().handleCollision(start, postSolve, sensorA, bodyB, sensorB)
							: bodyB.getParent().handleCollision(start, postSolve, sensorB, bodyA, sensorA);
//			if(!handled && sensorA == null && sensorB != null)
			if(!handled)
				handled = bodyB.getParent().handleCollision(start, postSolve, sensorB, bodyA, sensorA);
			
//			if(!handled && sensorA != null && sensorB == null)
			if(!handled)
				handled = bodyA.getParent().handleCollision(start, postSolve, sensorA, bodyB, sensorB);
		}
		
		if(!handled && bodyA.getParent() != null && bodyB.getParent() == null)
			handled = bodyA.getParent().handleCollision(start, postSolve, sensorA, bodyB, sensorB);
		
		if(!handled && bodyA.getParent() == null && bodyB.getParent() != null)
			handled = bodyB.getParent().handleCollision(start, postSolve, sensorB, bodyA, sensorA);
		
		return false;
	}

	@Override
	public void endContact(Contact contact) {
		handleCollision(contact, false, false);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		handleCollision(contact, false, true);
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

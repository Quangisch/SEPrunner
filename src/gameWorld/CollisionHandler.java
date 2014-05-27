package gameWorld;

import gameObject.GameObjectData;
import gameObject.player.Player;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionHandler implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        
        if(!(fixA.isSensor() || fixB.isSensor()))
        	return;
        
       GameObjectData gameObjA = (GameObjectData) fixA.getBody().getUserData();
       GameObjectData gameObjB = (GameObjectData) fixB.getBody().getUserData();
       
       if(gameObjA == null || gameObjB == null) {
    	   System.out.println("beginNULL");
    	   return;
       }
    	   
       
       if((gameObjA.type == GameObjectData.PLAYER || gameObjB.type == GameObjectData.PLAYER)
    		   && (gameObjB.type == GameObjectData.GROUND || gameObjA.type == GameObjectData.GROUND)) {
    	   Player.getInstance().setGrounded(true);
    	   System.out.println("grounded");
       }
		
	}

	@Override
	public void endContact(Contact contact) {
//		Fixture fixA = contact.getFixtureA();
//		Fixture fixB = contact.getFixtureB();
//        
//		GameObjectData gameObjA = (GameObjectData) fixA.getBody().getUserData();
//		GameObjectData gameObjB = (GameObjectData) fixB.getBody().getUserData();
//		
//		if(gameObjA == null || gameObjB == null) {
//			System.out.println("endNULL");
//	    	return;
//	    }
//		
//		
//		if((gameObjA.type == GameObjectData.PLAYER || gameObjB.type == GameObjectData.PLAYER)
//	    		   && (gameObjB.type == GameObjectData.GROUND || gameObjA.type == GameObjectData.GROUND)) {
//	    	   Player.getInstance().setGrounded(false);
//	    	   System.out.println("not grounded");
//		}
//		
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

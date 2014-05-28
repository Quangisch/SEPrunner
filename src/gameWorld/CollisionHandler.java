package gameWorld;

import gameObject.GameObjectData;

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
        
        GameObjectData objDataA = (GameObjectData) fixA.getBody().getUserData();
        GameObjectData objDataB = (GameObjectData) fixB.getBody().getUserData();
        
        CollisionData colDataA = new CollisionData(fixA, objDataA);
        CollisionData colDataB = new CollisionData(fixB, objDataB);
        
//      checkNULL
        if(objDataA == null || objDataB == null) {
        	System.out.println("no UserData");
        	return;
        }

//		checkPlayer
        beginPlayerCollision(checkPlayer(colDataA, colDataB));
	}
	
	/**
	 * Check playerData.
	 * @param objDataA
	 * @param objDataB
	 * @return null if no objData corresponding to Data found else GameObjectData[0] -> playerData, GameObjectData[1] -> objData
	 */

	private CollisionData[] checkPlayer(CollisionData colDataA, CollisionData colDataB) {
		if(colDataA.gameObj.TYPE == GameObjectData.PLAYER)
   			return new CollisionData[]{colDataA, colDataB};
 
   		if(colDataB.gameObj.TYPE == GameObjectData.PLAYER)
   			return new CollisionData[]{colDataB, colDataA};
   		
   		return null;
	}
	
	private void beginPlayerCollision(CollisionData[] objects) {
		if(objects == null)
			return;
		
		CollisionData colDataPlayer = objects[0];
		CollisionData colDataObject = objects[1];
		
//		contact with ground
		if(colDataPlayer.fix.isSensor())
		if(colDataObject.gameObj.TYPE == GameObjectData.GROUND) {
			colDataPlayer.gameObj.GAMEOBJECT.setGrounded(true);
			System.out.println("player grounded");
			return;
		}
	}
	
	@Override
	public void endContact(Contact contact) {
//		Fixture fixA = contact.getFixtureA();
//		Fixture fixB = contact.getFixtureB();
//        
//		GameObjectData gameObjA = (GameObjectData) fixA.getUserData();
//		GameObjectData gameObjB = (GameObjectData) fixB.getUserData();
//		
//		if(gameObjA == null || gameObjB == null) {
//			System.out.println("beginNULL");
//	    	return;
//	    }
//		
//		
//		if((gameObjA.type == GameObjectData.PLAYER || gameObjB.type == GameObjectData.PLAYER)
//	    		   && (gameObjB.type == GameObjectData.GROUND || gameObjA.type == GameObjectData.GROUND)) {
//	    	   Player.getInstance().setGrounded(false);
//	    	   System.out.println("not grounded");
//		}
		
	}
	
	

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
	
	private class CollisionData {
		private final Fixture fix;
		private final GameObjectData gameObj;
		private CollisionData(Fixture fix, GameObjectData gameObj) {
			this.fix = fix;
			this.gameObj = gameObj;
		}
	}

}

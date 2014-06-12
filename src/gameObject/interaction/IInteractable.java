package gameObject.interaction;

import com.badlogic.gdx.math.Vector2;

public interface IInteractable {

boolean isBodyBlocked();
	
	boolean isGrounded();
	
	
	boolean decShuriken();
	
	int getShurikenQuantity();
	
	
	
	boolean canHide();
	
	boolean canGrab();
	
	boolean canDispose();
	
	
	boolean startGrab();
	
	boolean endGrab();
	
	boolean disposeGrab();
	
	
	int getHookRadius();
	
	void setHookRadius(int hookRadius);
	
	Vector2 getHookPoint();
	
	boolean tryToHook(Vector2 clickPoint);
	
}

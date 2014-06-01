package gameObject;

public interface IGameObjectStates {

	abstract public class GameObjectStates {
		public enum InteractionState {
			STAND,
			WALK,
			RUN,
			
			CROUCH_STAND,
			CROUCH_SNEAK,
			
			JUMP,
			JUMP_MOVE,
			
			HIDE,
			THROW,
			
			HOOK_START,
			HOOK_FLY,
			
			GRAB,
			GRAB_PULL,
			GRAB_DISPOSE,
			
			STUNNED;
		}
	}
	
	
}

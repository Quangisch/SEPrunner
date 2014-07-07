package gameObject.interaction.player;

import core.Highscore.Score;
import core.PlayerProfile;

public interface IPlayer {
	
	PlayerProfile getProfile();
	Score getScore();
	
	void saveProfile();
	void saveHighscore();

}

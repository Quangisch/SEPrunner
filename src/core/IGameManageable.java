package core;

import com.badlogic.gdx.math.Vector2;

import core.GameProperties.GameScreen;
import core.GameProperties.GameState;

public interface IGameManageable {

	//Conversion
	float meterToPixel(float meter);
	float pixelToMeter(float pixel);
	Vector2 meterToPixel(Vector2 vecMeter);
	Vector2 pixelToMeter(Vector2 vecPixel);

	GameState getGameState();
	void switchGameState(GameState newState);
	boolean switchGameScreen(GameScreen newGameScreen);
	void toogleIngamePause();
	void setGameOver(String message);
	void setWin();
	boolean isCurrentGameState(GameState state);
	boolean isCurrentGameScreen(GameScreen screen);
	boolean isInMenu();
	boolean isIngame();

	//DisplayModes
	void toogleFullScreen();
	void initPrefDisplayMode();
	void refreshDisplayMode();

	//Additional
	void resetUserData();
	
}

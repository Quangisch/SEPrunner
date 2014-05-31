package menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuOption implements Screen {
	
	private Stage stage;
	private Table table;
	private Skin skin;

	/** @return the directory the levels will be saved to and read from */
	public static FileHandle levelDirectory() {
		String prefsDir = Gdx.app.getPreferences(Project.TITLE).getString("leveldirectory").trim();
		if(prefsDir != null && !prefsDir.equals(""))
			return Gdx.files.absolute(prefsDir);
		else
			return Gdx.files.absolute((Gdx.files.external(Project.TITLE + "/levels").file().getAbsolutePath()));
	}
	
	/** @return if vSync is enabled */
	public static boolean vSync() {
		return Gdx.app.getPreferences(Project.TITLE).getBoolean("vsync");
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1); 		//black screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
		
//Table.drawDebug(stage);            // case debuglines needed 1/2
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
		table.invalidateHierarchy();
	}

	@Override
	public void show() {
		stage = new Stage();
		
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"), new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack")));
		
		table = new Table(skin);
		table.setFillParent(true);
		
//table.debug();            // case debuglines needed 2/2
		
		final CheckBox vSyncCheckBox = new CheckBox("vSync", skin);
		vSyncCheckBox.setChecked(Gdx.app.getPreferences(Project.TITLE).getBoolean("vsync"));
		
		final TextField levelDirectoryInput = new TextField(levelDirectory().path(),skin);
		levelDirectoryInput.setMessageText("level directory");
		
		final TextButton back = new TextButton("Zurueck", skin);
		back.pad(10);
		
		ClickListener buttonHandler = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// event.getListenerActor() returns the source of the event, e.g. a button was clicked
				if(event.getListenerActor() == vSyncCheckBox) {
					//save vSync
					Gdx.app.getPreferences(Project.TITLE).putBoolean("vsync", vSyncCheckBox.isChecked());
					
					//set vSync
					Gdx.graphics.setVSync(vSync());
					
					Gdx.app.log(Project.TITLE, "vSync " + (vSync() ? "enabled" : "disabled"));
				} else if(event.getListenerActor() == back) {
					//save level directory
					String actualLevelDirectory = levelDirectoryInput.getText().trim().equals("") ? Gdx.files.getExternalStoragePath() + Project.TITLE + "/levels" : levelDirectoryInput.getText().trim(); // shortened form of an if-statement: [boolean] ? [if true] : [else] // String#trim() removes spaces on both sides of the string
					Gdx.app.getPreferences(Project.TITLE).putString("leveldirectory", actualLevelDirectory);
					
					//save the settings to preferences file (Preferences#flush() writes the preferences from memory to the file)
					Gdx.app.getPreferences(Project.TITLE).flush();
					
					Gdx.app.log(Project.TITLE, "settings saved");
					
					((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
				}
			}
		};
		
		vSyncCheckBox.addListener(buttonHandler);
		
		back.addListener(buttonHandler);
		
		//putting stuff together
		table.add("Optionen").spaceBottom(50).colspan(3).expandX().row();
		table.add();
		table.add("level directory");
		table.add().row();
		table.add(vSyncCheckBox).top().expandY();
		table.add(levelDirectoryInput).top().fillX();
		table.add(back).bottom().right();
		
		stage.addActor(table);
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}

}

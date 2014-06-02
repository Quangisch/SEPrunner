package core.ingame;

import com.badlogic.gdx.assets.AssetManager;

public class ResourceManager extends AssetManager {
	
	private static ResourceManager manager;
	
	private ResourceManager() {
		
	}
	
	public static ResourceManager resetInstance() {
		manager.dispose();
		manager = null;
		return getInstance();
	}
	
	public static ResourceManager getInstance() {
		if(manager == null)
			manager = new ResourceManager();
		return manager;
	}
	
	public void dispose() {
		super.dispose();
	}

}

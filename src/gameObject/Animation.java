package gameObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Animation {

	protected List<PolygonShape> frameBoxes;
	protected List<TextureRegion> frameRegions;

	protected Iterator<PolygonShape> frameIter;
	protected Iterator<TextureRegion> regionIter;

	protected PolygonShape currentShape;
	protected TextureRegion currentRegion;

	public Animation() {
		frameBoxes = new LinkedList<PolygonShape>();
		frameRegions = new LinkedList<TextureRegion>();
		frameIter = frameBoxes.iterator();
		regionIter = frameRegions.iterator();
	}

	public void addFrame(PolygonShape box, TextureRegion texts) {
		frameBoxes.add(box);
		frameRegions.add(texts);
	}

	public void setNext() {
		if (!frameIter.hasNext()) {
			frameIter = frameBoxes.iterator();
			regionIter = frameRegions.iterator();
		}
		currentShape = frameIter.next();
		currentRegion = regionIter.next();
	}

	public PolygonShape getCurrentShape() {
		return currentShape;
	}

	public TextureRegion getCurrentRegion() {
		return currentRegion;
	}
}

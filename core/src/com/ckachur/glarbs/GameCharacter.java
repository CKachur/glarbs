package com.ckachur.glarbs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;

public final class GameCharacter {
	private static final String PATHING_LAYER_NAME = "Pathing";
	private TextureRegion currentFrame;
	private final Animation[] walkAnimations;
	private Vector2 point;
	private Vector2 renderPoint; //for gliding independent of grid
	private Facing facing;
	private boolean isMoving = false;
	private TextureRegion[][] spriteSheet;
	private float stateTime;
	private GameCharacterController controller;
	private Sound bumpSound;
	private float bumpedlastTime = 0;

	public GameCharacter(Texture texture, Vector2 point, GameCharacterController controller) {
		this.controller = controller;
		spriteSheet = TextureRegion.split(texture, 16, 16);
		walkAnimations = new Animation[4];// 4 cardinal directions
		setupAnimations();
		facing = Facing.DOWN;
		currentFrame = spriteSheet[1][0];
		this.point = point;
		renderPoint = new Vector2(point);

		bumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
	}

	public void render(SpriteBatch batch) {
		if( isMoving ) {
			stateTime += Gdx.graphics.getDeltaTime();
			currentFrame = walkAnimations[facing.ordinal()].getKeyFrame(stateTime, true);
		} else {
			currentFrame = spriteSheet[facing.ordinal()][0];
		}
		batch.draw(currentFrame, renderPoint.x, renderPoint.y, 1, 1);
	}

	public Vector2 getPoint() {
		return point;
	}
	
	public void setPoint(Vector2 point) {
		this.point = point;
		renderPoint = new Vector2(point);
		if( isMoving ) {
			point.add(facing.getStep());
		}
	}
	
	public Vector2 getRenderPoint() {
		return renderPoint;
	}
	
	public void update(TiledMap map) {
		// While we are moving we don't look at new movements.
		if( isMoving ) {
			Vector2 distanceToGo = new Vector2(point.x - renderPoint.x, point.y - renderPoint.y);
			renderPoint.add(distanceToGo.scl(1/(distanceToGo.len()*15)));
			if( renderPoint.dst2(point) <= 0.001f ) {
				renderPoint = new Vector2(point);
				isMoving = false;
			}
		}
		if (!isMoving) {
			Facing nextDirection = controller.getNextDirection();
			if( nextDirection != null ) {
				facing = nextDirection;
				Vector2 step = facing.getStep();
				Vector2 newPoint = new Vector2(point.x + step.x, point.y + step.y);

				//Move the player but keep from moving outside the map and into objects.
				TiledMapTileLayer mainLayer = (TiledMapTileLayer)map.getLayers().get(PATHING_LAYER_NAME);
				Cell cell = mainLayer.getCell((int)newPoint.x, (int)newPoint.y);
				if( newPoint.x >= 0 && newPoint.x < mainLayer.getWidth() &&
					newPoint.y >= 0 && newPoint.y < mainLayer.getHeight() &&
					(cell == null || cell.getTile().getId() == 1) ) {
					point = newPoint;
					isMoving = true;
				} else {
					// If we bump a new direction play bump sound.

					if (bumpedlastTime + 0.2f < stateTime) {
						bumpSound.play();
						bumpedlastTime = stateTime;
					}
				}
			}
		}
	}
	
	public boolean intersects(MapObject object) {
		MapProperties properties = object.getProperties();
		if( properties == null ) {
			return false;
		}
		float x = Float.parseFloat(properties.get("x").toString())/GameEnvironment.TILESIZE;
		float y = Float.parseFloat(properties.get("y").toString())/GameEnvironment.TILESIZE;
		float width = Float.parseFloat(properties.get("width").toString())/GameEnvironment.TILESIZE;
		float height = Float.parseFloat(properties.get("height").toString())/GameEnvironment.TILESIZE;
		return point.x + 0.5f >= x && point.x + 0.5f < x + width && point.y + 0.5f >= y && point.y + 0.5f < y + height;
	}

	private void setupAnimations() {
		int animationIndex = 0;
		for(TextureRegion[] row: spriteSheet) {
			TextureRegion[] animationFrames = new TextureRegion[2];
			for(int i = 0; i < 2; i++) {
				if( animationIndex >= 2 ) {
					// the left/right directions use the basic stand, and are index 2/3
					// so, they use indices 
					animationFrames[i] = row[i];
				} else {
					animationFrames[i] = row[i+1];
				}
			}
			walkAnimations[animationIndex] = new Animation(0.125f, animationFrames);
			animationIndex++;
		}
	}
}

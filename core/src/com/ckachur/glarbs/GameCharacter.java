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
	private String name;
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
	private GameCharacterInteractionListener interactionListener;

	public GameCharacter(String name, Texture texture, Vector2 point, GameCharacterController controller) {
		this.name = name;
		this.controller = controller;
		if( texture != null ) {
			spriteSheet = TextureRegion.split(texture, 16, 16);
			walkAnimations = new Animation[4];// 4 cardinal directions
			setupAnimations();
			currentFrame = spriteSheet[1][0];
		} else {
			walkAnimations = null;
		}
		facing = Facing.DOWN;
		this.point = point;
		renderPoint = new Vector2(point);

		bumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bump.wav"));
	}
	
	public String getName() {
		return name;
	}

	public void render(SpriteBatch batch) {
		if( spriteSheet == null ) {
			return;
		}
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
	

	public void update(GameEnvironment gameEnv, TiledMap map) {
		if( isMoving ) {
			Vector2 distanceToGo = new Vector2(point.x - renderPoint.x, point.y - renderPoint.y);
			renderPoint.add(distanceToGo.scl(1/(distanceToGo.len()*15)));
			if( renderPoint.dst2(point) <= 0.001f ) {
				renderPoint = new Vector2(point);
				isMoving = false;
			}
		}
		if (!isMoving) {
			Facing nextDirection = controller.getNextDirection(this);
			if( nextDirection != null ) {
				facing = nextDirection;
				if( controller.wantsMove(this) ) {
					Vector2 step = facing.getStep();
					Vector2 newPoint = new Vector2(point.x + step.x, point.y + step.y);
					TiledMapTileLayer mainLayer = (TiledMapTileLayer)map.getLayers().get(PATHING_LAYER_NAME);
					Cell cell = mainLayer.getCell((int)newPoint.x, (int)newPoint.y);
					Boolean checkInteractions = gameEnv.checkInteractions(this, newPoint);
					if( newPoint.x >= 0 && newPoint.x < mainLayer.getWidth() && newPoint.y >= 0 && newPoint.y < mainLayer.getHeight()
							&& !checkInteractions
							&& (cell == null || cell.getTile().getId() == 1)) {
						point = newPoint;
						isMoving = true;
					} else {
						// If we bump a direction play bump sound.
						if (!checkInteractions && bumpedlastTime + 0.2f < stateTime) {
							bumpSound.play();
							bumpedlastTime = stateTime;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Called on the outside when another game character is about to
	 * step on top of this one. Instead, we can return 'true' to say
	 * that the other character interacted with us, and must be
	 * halted.
	 * 
	 * @param source Who is trying to step on us
	 * @return Whether we stop them from stepping on us
	 */
	public boolean onInteracted(GameCharacter source) {
		if( interactionListener != null ) {
			return interactionListener.onInteracted(source);
		}
		return false;
	}
	
	public void setInteractionListener(GameCharacterInteractionListener interactionListener) {
		this.interactionListener = interactionListener;
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
	
	public float angleTowards(Vector2 target) {
		float dx = target.x - renderPoint.x;
		float dy = target.y - renderPoint.y;
		return (float)-Math.toDegrees(Math.atan2(dx, dy))+90;
	}

	private void setupAnimations() {
		int animationIndex = 0;
		for(TextureRegion[] row: spriteSheet) {
			if( row.length <= 1 ) {
				// static character (probably npc)
				walkAnimations[animationIndex] = new Animation(0.125f, row);
				animationIndex++;
			} else {
				// animating character (probably main guy)
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
}

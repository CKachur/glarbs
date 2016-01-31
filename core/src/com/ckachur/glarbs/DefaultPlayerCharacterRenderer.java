package com.ckachur.glarbs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class DefaultPlayerCharacterRenderer implements GameCharacterRenderer {

	private TextureRegion currentFrame;
	private final Animation[] walkAnimations;
	private TextureRegion[][] spriteSheet;
	private float stateTime;
	
	public DefaultPlayerCharacterRenderer(Texture texture) {
		if( texture != null ) {
			spriteSheet = TextureRegion.split(texture, 16, 16);
			walkAnimations = new Animation[4];// 4 cardinal directions
			setupAnimations();
			currentFrame = spriteSheet[1][0];
		} else {
			walkAnimations = null;
		}
	}

	@Override
	public void render(SpriteBatch batch, Vector2 point, Facing facing, boolean isMoving) {
		if( spriteSheet == null ) {
			return;
		}
		if( isMoving ) {
			stateTime += Gdx.graphics.getDeltaTime();
			currentFrame = walkAnimations[facing.ordinal()].getKeyFrame(stateTime, true);
		} else {
			currentFrame = spriteSheet[facing.ordinal()][0];
		}
		batch.draw(currentFrame, point.x, point.y, 1, 1);
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
				TextureRegion[] animationFrames;
				if( animationIndex < 2 ) {
					animationFrames = new TextureRegion[4];
					int[] frameIndices = { 1, 0, 2, 0 }; // simon says
					for(int i = 0; i < animationFrames.length; i++) {
						// the left/right directions use the basic stand, and are index 2/3
						// so, they use indices 
						animationFrames[i] = row[frameIndices[i]];
					}
				} else {
					animationFrames = new TextureRegion[2];
					for(int i = 0; i < animationFrames.length; i++) {
						animationFrames[i] = row[i];
					}
				}
				walkAnimations[animationIndex] = new Animation(0.125f, animationFrames);
				animationIndex++;
			}
		}
	}
}

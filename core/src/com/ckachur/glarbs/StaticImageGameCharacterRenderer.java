package com.ckachur.glarbs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class StaticImageGameCharacterRenderer implements GameCharacterRenderer {
	
	private final TextureRegion textureRegion;

	public StaticImageGameCharacterRenderer(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}

	@Override
	public void render(SpriteBatch batch, Vector2 point, Facing facing, boolean isMoving) {
		batch.draw(textureRegion, point.x, point.y);
	}

}

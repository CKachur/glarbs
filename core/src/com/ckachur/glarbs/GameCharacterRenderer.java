package com.ckachur.glarbs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface GameCharacterRenderer {
	void render(SpriteBatch batch, Vector2 point, Facing facing, boolean isMoving);
}

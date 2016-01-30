package com.ckachur.glarbs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class DevGuy {
	private final Texture texture;
	private Vector2 point;
	private float lastXSlide, lastYSlide;
	public DevGuy(Texture texture, Vector2 point) {
		this.texture = texture;
		this.point = point;
	}
	public void render(SpriteBatch batch) {
		batch.draw(texture, point.x, point.y, 1, 1);
	}
	public Vector2 getPoint() {
		return point;
	}
	public void translate(float x, float y) {
		point.x += x;
		point.y += y;
		if( Math.abs(x) > 0 ) {
			lastXSlide = x;
		}
		if( Math.abs(y) > 0) {
			lastYSlide = y;
		}
	}
	public void moveUntilGrid() {
		if( Math.abs(point.x - (int)point.x) >= 0.01f ) {
			point.x += lastXSlide;
		}
		if( Math.abs(point.y - (int)point.y) >= 0.01f ) {
			point.y += lastYSlide;
		}
	}
}

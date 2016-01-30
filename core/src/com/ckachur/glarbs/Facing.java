package com.ckachur.glarbs;

import com.badlogic.gdx.math.Vector2;

public enum Facing {
	UP(new Vector2(0,1)), DOWN(new Vector2(0,-1)), LEFT(new Vector2(-1,0)), RIGHT(new Vector2(1,0));
	
	private final Vector2 step;

	Facing(Vector2 step) {
		this.step = step;
	}
	
	public Vector2 getStep() {
		return step;
	}
}

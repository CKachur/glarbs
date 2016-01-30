package com.ckachur.glarbs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class GameCharacterKeyboardController implements GameCharacterController {
	private boolean enabled = true;

	@Override
	public Facing getNextDirection() {
		if( !enabled ) {
			return null;
		}
		if( Gdx.input.isKeyPressed(Input.Keys.DOWN) ) {
			return Facing.DOWN;
		} else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			return Facing.UP;
		} else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			return Facing.LEFT;
		} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			return Facing.RIGHT;
		}
		return null;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}

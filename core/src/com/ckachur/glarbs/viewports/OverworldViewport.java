package com.ckachur.glarbs.viewports;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ckachur.glarbs.GameEnvironment;

public class OverworldViewport extends ExtendViewport {

	private GameEnvironment gameEnvironment;
	
	public OverworldViewport(float minWorldWidth, float minWorldHeight, OrthographicCamera camera, GameEnvironment gameEnvironment) {
		super(minWorldWidth, minWorldHeight, camera);
		this.gameEnvironment = gameEnvironment;
	}
	
	public void render() {
		this.apply();
		gameEnvironment.render((OrthographicCamera)(this.getCamera()));
	}
	
}

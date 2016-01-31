package com.ckachur.glarbs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class Glarbs extends Game implements GameEventsListener {

	Screen overworldScreen;

	@Override
	public void create () {
		overworldScreen = new OverworldScreen(this);
		setScreen(overworldScreen);
	}

	@Override
	public void render () {
		getScreen().render(0);
	}


	@Override
	public void resize(int width, int height) {
		//	super.resize(width, height);
		//	viewport.update(width, height);
		//	camera.update();
		//	hudViewport.update(width, height);
		//	hudCamera.update();
	}

	@Override
	public void showMessagePopup(String message) {
		//	message = message.replace("\\n", "\n");
		//	gameEnvironment.disableControls();
		//	messageStateTime = 0;
		//	currentMessageBuffer.setLength(0);
		//	currentMessage = message;
	}

	@Override
	public void enterBattle() {
		//	gameEnvironment.disableControls();
		//	battleTime = 0;
		//	inBattle = true;
	}
}
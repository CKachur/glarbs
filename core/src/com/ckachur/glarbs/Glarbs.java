package com.ckachur.glarbs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class Glarbs extends Game {

	Screen overworldScreen;

	@Override
	public void create() {
		overworldScreen = new OverworldScreen(this);
		setScreen(overworldScreen);
	}

	@Override
	public void render() {
		getScreen().render(0);
	}
}
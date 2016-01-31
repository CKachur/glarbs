package com.ckachur.glarbs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class Glarbs extends Game {

	OverworldScreen overworldScreen;

	@Override
	public void create() {
		overworldScreen = new OverworldScreen(this);
		setScreen(overworldScreen);
	}
	
	public OverworldScreen getOverworldScreen() {
		return overworldScreen;
	}
}
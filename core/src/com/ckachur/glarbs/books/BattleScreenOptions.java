package com.ckachur.glarbs.books;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BattleScreenOptions {
	private final List<Option> options;
	private final Texture boxBackdrop;
	private final Texture selectionArrow;
	private int selectedOptionIndex = 0;
	private BitmapFont font;
	private int numberOfRows;
	private Sound interactionSound;
	
	public BattleScreenOptions() {
		options = new ArrayList<>();
		boxBackdrop = new Texture("battle/miniBox.png");
		selectionArrow = new Texture("battle/selectionArrow.png");
		font = new BitmapFont();
		numberOfRows = 2;
		interactionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/select.wav"));
	}
	
	public void addOption(String text, BattleScreenOptionListener listener) {
		options.add(new Option(text, listener));
		numberOfRows = (int)Math.max(Math.ceil(Math.sqrt(options.size())), 1);
	}
	
	public void render(Viewport viewport, SpriteBatch spriteBatch) {
		float boxBackdropX = viewport.getWorldWidth() * 2/5;
		float boxBackdropHeight = viewport.getWorldWidth()*44/155;
		spriteBatch.draw(boxBackdrop, boxBackdropX, 0, viewport.getWorldWidth()*3/5, boxBackdropHeight);
		
		int index = 0;
		float optionSpacing = 32;
		font.setColor(Color.BLACK);
		for(Option option: options) {
			float optionX = boxBackdropX + optionSpacing + (index / numberOfRows)*optionSpacing*4;
			float optionY = boxBackdropHeight - optionSpacing - (index % numberOfRows)*optionSpacing;
			font.draw(spriteBatch, option.getText(), optionX, optionY);
			if( index == selectedOptionIndex ) {
				spriteBatch.draw(selectionArrow, optionX - selectionArrow.getWidth(), optionY - selectionArrow.getHeight());
			}
			index++;
		}
	}
	
	public void update() {
		if( Gdx.input.isKeyJustPressed(Input.Keys.DOWN) ) {
			int relativeY = selectedOptionIndex%numberOfRows;
			int newY = (relativeY + 1)%numberOfRows;
			selectedOptionIndex += (newY - relativeY);
			interactionSound.play();
		}
		if( Gdx.input.isKeyJustPressed(Input.Keys.UP) ) {
			int relativeY = selectedOptionIndex%numberOfRows;
			int newY = (((relativeY - 1)%numberOfRows)+numberOfRows)%numberOfRows;
			selectedOptionIndex += (newY - relativeY);
			interactionSound.play();
		}
		if( Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) ) {
			selectedOptionIndex = (((selectedOptionIndex + options.size()/numberOfRows)%options.size())+options.size())%options.size();
			interactionSound.play();
		}
		if( Gdx.input.isKeyJustPressed(Input.Keys.LEFT) ) {
			selectedOptionIndex = (((selectedOptionIndex - options.size()/numberOfRows)%options.size())+options.size())%options.size();
			interactionSound.play();
		}
		if( Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ) {
			options.get(selectedOptionIndex).getListener().onSelected();
			interactionSound.play();
		}
	}
	
	private static final class Option {
		private final String text;
		private final BattleScreenOptionListener listener;
		public Option(String text, BattleScreenOptionListener listener) {
			this.text = text;
			this.listener = listener;
		}
		public String getText() {
			return text;
		}
		public BattleScreenOptionListener getListener() {
			return listener;
		}
	}
}

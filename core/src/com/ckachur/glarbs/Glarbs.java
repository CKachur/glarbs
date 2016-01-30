package com.ckachur.glarbs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Glarbs extends ApplicationAdapter implements MessagePopupListener {
	private OrthographicCamera camera;
	private Viewport viewport;
	private GameEnvironment gameEnvironment;
	private OrthographicCamera hudCamera;
	private Viewport hudViewport;
	private String currentMessage = null;
	private BitmapFont font;
	private GlyphLayout glyphLayout;
	private SpriteBatch textSpriteBatch;
	
	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 50);
		viewport = new ExtendViewport(10, 10, camera);
		viewport.apply();
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(5, 100-5, 0);
		camera.update();
		

		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, 640, 480);
		hudViewport = new FitViewport(640, 480, camera);
		hudViewport.apply();
		hudViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//mapRenderer.setView(camera);
		gameEnvironment = new GameEnvironment(this);
		
		font = new BitmapFont();
		textSpriteBatch = new SpriteBatch();
		glyphLayout = new GlyphLayout();
	}

	@Override
	public void render () {
		gameEnvironment.update(camera);
		if( Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ) {
			currentMessage = null;
			gameEnvironment.enableControls();
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.apply();
		gameEnvironment.render(camera);
		

		hudViewport.apply();
		if( currentMessage != null ) {
			textSpriteBatch.setProjectionMatrix(hudCamera.combined);
			textSpriteBatch.begin();
			glyphLayout.setText(font, currentMessage);
			font.draw(textSpriteBatch, currentMessage, (hudViewport.getWorldWidth()-glyphLayout.width)/2, font.getLineHeight()*3);
			textSpriteBatch.end();
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height);
		camera.update();
		hudViewport.update(width, height);
		hudCamera.update();
	}

	@Override
	public void showMessagePopup(String message) {
		gameEnvironment.disableControls();
		currentMessage = message;
	}
}

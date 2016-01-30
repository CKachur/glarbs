package com.ckachur.glarbs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Glarbs extends ApplicationAdapter {
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private OrthographicCamera camera;
	private Viewport viewport;
	private OrthogonalTiledMapRenderer mapRenderer;
	private SpriteBatch spriteBatch;
	private DevGuy devGuy;
	
	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		mapLoader = new TmxMapLoader();
		map = mapLoader.load("pokeMon.tmx");
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 50);
		viewport = new ExtendViewport(10, 10, camera);
		viewport.apply();
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(5, 100-5, 0);
		camera.update();
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1/16f);
		devGuy = new DevGuy(new Texture("guy.png"), new Vector2(5,95));
		//mapRenderer.setView(camera);
	}

	@Override
	public void render () {
		processInput();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		spriteBatch.setProjectionMatrix(camera.combined);
		mapRenderer.setView(camera);
		mapRenderer.render();
		spriteBatch.begin();
		devGuy.render(spriteBatch);
		spriteBatch.end();
	}
	
	private void processInput() {
		if( Gdx.input.isKeyPressed(Input.Keys.DOWN) ) {
			devGuy.translate(0, -0.05f);
		}
		else if( Gdx.input.isKeyPressed(Input.Keys.LEFT) ) {
			devGuy.translate(-0.05f, 0);
		}
		else if( Gdx.input.isKeyPressed(Input.Keys.UP) ) {
			devGuy.translate(0, 0.05f);
		}
		else if( Gdx.input.isKeyPressed(Input.Keys.RIGHT) ) {
			devGuy.translate(0.05f, 0);
		} else {
			devGuy.moveUntilGrid();
		}
		camera.position.set(devGuy.getPoint(), 0);
		camera.update();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height);
		camera.update();
	}
}

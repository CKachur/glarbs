package com.ckachur.glarbs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ckachur.glarbs.viewports.OverworldHudViewport;
import com.ckachur.glarbs.viewports.OverworldViewport;

/**
 * Created by Tyler Wolverton on 1/30/2016.
 */
public class OverworldScreen implements Screen {
    Glarbs glarbs;
    private OrthographicCamera camera;
    private OverworldViewport overworldViewport;
    private OverworldHudViewport overworldHudViewport;
    private GameEnvironment gameEnvironment;
    private OrthographicCamera hudCamera;

    public OverworldScreen(Glarbs glarbsIn) {
        glarbs = glarbsIn;
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 30, 50);
        camera.position.set(5, 100-5, 0);
        camera.update();

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, 640, 480);
        overworldHudViewport = new OverworldHudViewport(640, 480, camera, hudCamera);
        overworldHudViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        gameEnvironment = new GameEnvironment(overworldHudViewport);
        overworldHudViewport.setGameEnvironment(gameEnvironment);
        
        overworldViewport = new OverworldViewport(10, 10, camera, gameEnvironment);
        overworldViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render (float delta) {
        gameEnvironment.update(camera);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        overworldViewport.render();
        overworldHudViewport.render();
    }

    public void resize(int width, int height) {
        overworldViewport.update(width, height);
        camera.update();
        overworldHudViewport.update(width, height);
        hudCamera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {
        
    }

    @Override
    public void dispose() {

    }
}

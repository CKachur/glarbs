package com.ckachur.glarbs.books;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ckachur.glarbs.Glarbs;

public class BookemonBattleScreen extends ScreenAdapter implements Screen {
    Glarbs glarbs;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BookemonTrainer lowerTrainer, upperTrainer;

    @Override
    public void show()
    {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        viewport = new FitViewport(640, 480, camera);
        viewport.apply();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
    }
}

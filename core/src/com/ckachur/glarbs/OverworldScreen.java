package com.ckachur.glarbs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ckachur.glarbs.books.BookemonBattleScreen;

/**
 * Created by Tyler Wolverton on 1/30/2016.
 */
public class OverworldScreen implements Screen, OverworldGameEventsListener {
    Glarbs glarbs;
    private OrthographicCamera camera;
    private Viewport viewport;
    private GameEnvironment gameEnvironment;
    private OrthographicCamera hudCamera;
    private Viewport hudViewport;
    private String currentMessage = null;
    private StringBuffer currentMessageBuffer;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private SpriteBatch textSpriteBatch;
    private Texture messageBackdrop;
    private float messageStateTime;
    private Animation pixelatedWhirl;
    private boolean inBattle = false;
    private float battleTime;

    public OverworldScreen(Glarbs glarbsIn)
    {
        glarbs = glarbsIn;
    }

    @Override
    public void render (float delta) {
        gameEnvironment.update(camera);
        if( currentMessage != null && Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ) {
            currentMessage = null;
            battleTime = 0;
            gameEnvironment.enableControls();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        gameEnvironment.render(camera);


        hudViewport.apply();
        if( inBattle || pixelatedWhirl.isAnimationFinished(battleTime) ) {
            battleTime += Gdx.graphics.getDeltaTime();
            textSpriteBatch.setProjectionMatrix(hudCamera.combined);
            textSpriteBatch.begin();
            TextureRegion keyFrame = pixelatedWhirl.getKeyFrame(battleTime);
            textSpriteBatch.draw(keyFrame, 0, 0, hudViewport.getWorldWidth(), hudViewport.getWorldHeight());
            textSpriteBatch.end();
            if( inBattle && battleTime > pixelatedWhirl.getAnimationDuration()*2 ) {
                inBattle = false;
                battleTime = 0;
                glarbs.setScreen(BookemonBattleScreen.createTestBattle(glarbs));
                //showMessagePopup("You tried to enter a battle, but we didn't code those yet.");
            }
        }
        if( currentMessage != null ) {
            messageStateTime += Gdx.graphics.getDeltaTime();
            for(int i = currentMessageBuffer.length(); i < Math.min(messageStateTime*60,currentMessage.length()); i++) {
                currentMessageBuffer.append(currentMessage.charAt(i));
            }
            textSpriteBatch.setProjectionMatrix(hudCamera.combined);
            textSpriteBatch.begin();
            float messageBackdropHeight = hudViewport.getWorldWidth() * messageBackdrop.getHeight() / messageBackdrop.getWidth();
            textSpriteBatch.draw(messageBackdrop, 0, 0, hudViewport.getWorldWidth(), messageBackdropHeight);
            font.setColor(Color.BLACK);
            String textToPrint = currentMessageBuffer.toString();
            String[] linesOfTextToPrint = textToPrint.split("\n");
            int linesPrinted = 0;
            for(String line: linesOfTextToPrint) {
                font.draw(textSpriteBatch, line, font.getLineHeight()*2, messageBackdropHeight - font.getLineHeight()*(2 + linesPrinted));
                linesPrinted++;
            }
            textSpriteBatch.end();
        }
    }

    public void resize(int width, int height) {
        //super.resize(width, height);
        viewport.update(width, height);
        camera.update();
        hudViewport.update(width, height);
        hudCamera.update();
    }


    public void showMessagePopup(String message) {
        message = message.replace("\\n", "\n");
        gameEnvironment.disableControls();
        messageStateTime = 0;
        currentMessageBuffer.setLength(0);
        currentMessage = message;
    }

    public void enterBattle() {
        gameEnvironment.disableControls();
        battleTime = 0;
        inBattle = true;
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void show()
    {
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

        // code for messages on the screen, should probably move elsewhere later
        currentMessageBuffer = new StringBuffer();
        messageBackdrop = new Texture("textbox.png");
        font = new BitmapFont();
        textSpriteBatch = new SpriteBatch();
        glyphLayout = new GlyphLayout();

        // pixelated whirl for battle
        pixelatedWhirl = new Animation(0.025f, TextureRegion.split(new Texture("pixelatedWhirl.png"), 160, 142)[0]);
    }

    @Override
    public void dispose()
    {

    }
}

package com.ckachur.glarbs.viewports;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ckachur.glarbs.GameEnvironment;
import com.ckachur.glarbs.GameEventsListener;

public class OverworldHudViewport extends FitViewport implements GameEventsListener {

	private OrthographicCamera hudCamera;
	private GameEnvironment gameEnvironment;
	private String currentMessage = null;
	private StringBuffer currentMessageBuffer;
	private BitmapFont font;
	private SpriteBatch textSpriteBatch;
	private Texture messageBackdrop;
	private float messageStateTime;
	private Animation pixelatedWhirl;
	private boolean inBattle = false;
	private float battleTime;
	
	public OverworldHudViewport(float minWorldWidth, float minWorldHeight, OrthographicCamera camera, OrthographicCamera hudCamera) {
		super(minWorldWidth, minWorldHeight, camera);
		
		this.hudCamera = hudCamera;
		currentMessageBuffer = new StringBuffer();
		messageBackdrop = new Texture("textbox.png");
		font = new BitmapFont();
		textSpriteBatch = new SpriteBatch();
		pixelatedWhirl = new Animation(0.025f, TextureRegion.split(new Texture("pixelatedWhirl.png"), 160, 142)[0]);
	}
	
	public void setGameEnvironment(GameEnvironment gameEnvironment) {
		this.gameEnvironment = gameEnvironment;
	}
	
	public void render() {
		if( Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ) {
            currentMessage = null;
            battleTime = 0;
            gameEnvironment.enableControls();
        }
		
		this.apply();
        if( inBattle || pixelatedWhirl.isAnimationFinished(battleTime) ) {
            battleTime += Gdx.graphics.getDeltaTime();
            textSpriteBatch.setProjectionMatrix(hudCamera.combined);
            textSpriteBatch.begin();
            TextureRegion keyFrame = pixelatedWhirl.getKeyFrame(battleTime);
            textSpriteBatch.draw(keyFrame, 0, 0, this.getWorldWidth(), this.getWorldHeight());
            textSpriteBatch.end();
            if( inBattle && battleTime > pixelatedWhirl.getAnimationDuration()*2 ) {
                inBattle = false;
                showMessagePopup("You tried to enter a battle, but we didn't code those yet.");
            }
        }
        if( currentMessage != null ) {
            messageStateTime += Gdx.graphics.getDeltaTime();
            for(int i = currentMessageBuffer.length(); i < Math.min(messageStateTime*60,currentMessage.length()); i++) {
                currentMessageBuffer.append(currentMessage.charAt(i));
            }
            textSpriteBatch.setProjectionMatrix(hudCamera.combined);
            textSpriteBatch.begin();
            float messageBackdropHeight = this.getWorldWidth() * messageBackdrop.getHeight() / messageBackdrop.getWidth();
            textSpriteBatch.draw(messageBackdrop, 0, 0, this.getWorldWidth(), messageBackdropHeight);
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
	
}

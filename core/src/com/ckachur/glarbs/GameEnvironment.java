package com.ckachur.glarbs;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public final class GameEnvironment {
	public static final float TILESIZE = 16f;
	private SpriteBatch spriteBatch;
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private GameCharacter devGuy;
	private int[] layersToDraw = { 0 };
	private Set<String> usedMessagePopups = new HashSet<String>();
	private String lastPopupName;
	private GameCharacterKeyboardController playerController;
	private MessagePopupListener messagePopupListener;
	private Sound doorSound;
	
	public GameEnvironment(MessagePopupListener messagePopupListener) {
		this.messagePopupListener = messagePopupListener;
		spriteBatch = new SpriteBatch();
		mapLoader = new TmxMapLoader();
		map = mapLoader.load("pokeMon.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1/TILESIZE);
		playerController = new GameCharacterKeyboardController();
		devGuy = new GameCharacter(new Texture("guySprite.png"), new Vector2(5, 95), playerController);

		doorSound = Gdx.audio.newSound(Gdx.files.internal("sounds/dooropen.wav"));
	}

	public void render(OrthographicCamera camera) {
		spriteBatch.setProjectionMatrix(camera.combined);
		mapRenderer.setView(camera);
		mapRenderer.render(layersToDraw);
		spriteBatch.begin();
		devGuy.render(spriteBatch);
		spriteBatch.end();
		
	}

	
	public void update(OrthographicCamera camera) {
		devGuy.update(map);
		MapLayer transportsLayer = map.getLayers().get("Objects");
		for(MapObject object: transportsLayer.getObjects()) {
			Object typeProperty = object.getProperties().get("type");
			if( typeProperty.equals("levelTransition") && devGuy.intersects(object) ) {
				// load the target level
				doorSound.play();
				map = mapLoader.load(object.getProperties().get("target").toString());
				mapRenderer = new OrthogonalTiledMapRenderer(map, 1/TILESIZE);
				devGuy.setPoint(
						new Vector2(
								Float.parseFloat(object.getProperties().get("targetX").toString()),
								Float.parseFloat(object.getProperties().get("targetY").toString())));
			} else if( typeProperty.equals("messagePopup") && devGuy.intersects(object) && !(object.getProperties().containsKey("oneTime") && object.getProperties().get("oneTime").equals("true") && usedMessagePopups.contains(object.getName())) && !object.getName().equals(lastPopupName) ) {
				// load the target level
				messagePopupListener.showMessagePopup(object.getProperties().get("message").toString());
				if( object.getProperties().containsKey("oneTime") && object.getProperties().get("oneTime").equals("true") ) {
					usedMessagePopups.add(object.getName());
				}
				lastPopupName = object.getName();
				System.out.println(lastPopupName);
			}
		}
		camera.position.set(devGuy.getRenderPoint(), 0);
		camera.update();
	}
	
	public void enableControls() {
		playerController.setEnabled(true);
	}
	
	public void disableControls() {
		playerController.setEnabled(false);
	}
}

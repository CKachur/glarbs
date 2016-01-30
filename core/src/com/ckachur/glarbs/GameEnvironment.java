package com.ckachur.glarbs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
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
	//private MessagePopupListener messagePopupListener;
	private Sound doorSound;
	private GameEventsListener gameEventsListener;
	private List<GameCharacter> gameCharacters;
	private Texture devGuyTexture;
	
	public GameEnvironment(GameEventsListener messagePopupListener) {
		this.gameEventsListener = messagePopupListener;
		spriteBatch = new SpriteBatch();
		mapLoader = new TmxMapLoader();
		map = mapLoader.load("pokeMon.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1/TILESIZE);
		playerController = new GameCharacterKeyboardController();

		doorSound = Gdx.audio.newSound(Gdx.files.internal("sounds/dooropen.wav"));
		devGuyTexture = new Texture("guySprite.png");
		devGuy = new GameCharacter("Player", devGuyTexture, new Vector2(5, 95), playerController);
		gameCharacters = new ArrayList<GameCharacter>();
		onMapLoad();
	}

	public void render(OrthographicCamera camera) {
		spriteBatch.setProjectionMatrix(camera.combined);
		mapRenderer.setView(camera);
		mapRenderer.render(layersToDraw);
		spriteBatch.begin();
		for(GameCharacter character: gameCharacters) {
			character.render(spriteBatch);
		}
		spriteBatch.end();
		
	}

	
	public void update(OrthographicCamera camera) {
		for(GameCharacter character: gameCharacters) {
			character.update(this, map);
		}
		MapLayer objectsLayer = map.getLayers().get("Objects");
		for(MapObject object: objectsLayer.getObjects()) {
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
				onMapLoad();
			} else if( typeProperty.equals("messagePopup") && devGuy.intersects(object) && !(object.getProperties().containsKey("oneTime") && object.getProperties().get("oneTime").equals("true") && usedMessagePopups.contains(object.getName())) && !object.getName().equals(lastPopupName) ) {
				// load the target level
				gameEventsListener.showMessagePopup(object.getProperties().get("message").toString());
				if( object.getProperties().containsKey("oneTime") && object.getProperties().get("oneTime").equals("true") ) {
					usedMessagePopups.add(object.getName());
				}
				lastPopupName = object.getName();
			}
		}
		camera.position.set(devGuy.getRenderPoint(), 0);
		camera.update();
	}
	
	public boolean checkInteractions(GameCharacter source, Vector2 targetPoint) {
		boolean interacted = false;
		for(GameCharacter character: gameCharacters) {
			if( Math.floor(character.getPoint().x+0.5f) == Math.floor(targetPoint.x+0.5f) && Math.floor(character.getPoint().y+0.5f) == Math.floor(targetPoint.y+0.5f)) {
				interacted = interacted || character.onInteracted(source);
			}
		}
		return interacted;
	}
	
	private void onMapLoad() {
		gameCharacters.clear();
		gameCharacters.add(devGuy);
		MapLayer objectLayer = map.getLayers().get("Objects");
		for(MapObject object: objectLayer.getObjects()) {
			MapProperties properties = object.getProperties();
			Object typeProperty = properties.get("type");
			if( typeProperty.equals("npc") ) {
				float x = Float.parseFloat(properties.get("x").toString())/GameEnvironment.TILESIZE;
				float y = Float.parseFloat(properties.get("y").toString())/GameEnvironment.TILESIZE;
				float width = Float.parseFloat(properties.get("width").toString())/GameEnvironment.TILESIZE;
				float height = Float.parseFloat(properties.get("height").toString())/GameEnvironment.TILESIZE;
				Vector2 point = new Vector2(x + width/2 - 0.5f, y + height/2 - 0.5f);
				Texture texture = null;
				if( properties.containsKey("spriteSheet") ) {
					texture = new Texture(properties.get("spriteSheet").toString());
				}
				GameCharacterController controller = new GameCharacterDummyController(devGuy);
				if( properties.containsKey("lockFacing") ) {
					controller = new GameCharacterLockFacingController(Facing.valueOf(properties.get("lockFacing").toString().toUpperCase()));
				}
				GameCharacter npc = new GameCharacter(object.getName(), texture, point, controller);
				if( properties.containsKey("talkText") ) {
					final String talkText = properties.get("talkText").toString();
					npc.setInteractionListener(new GameCharacterInteractionListener() {
						@Override
						public boolean onInteracted(GameCharacter source) {
							if( source == devGuy ) {
								gameEventsListener.showMessagePopup("Bob" + ":\n" + talkText);
								return true;
							}
							return false;
						}
					});
				} else if( properties.containsKey("action") && properties.get("action").equals("fight")) {
					npc.setInteractionListener(new GameCharacterInteractionListener() {
						@Override
						public boolean onInteracted(GameCharacter source) {
							if( source == devGuy ) {
								gameEventsListener.enterBattle();
								return true;
							}
							return false;
						}
					});
				}
				gameCharacters.add(npc);
			}
		}
	}

	public void enableControls() {
		playerController.setEnabled(true);
	}
	
	public void disableControls() {
		playerController.setEnabled(false);
	}
}

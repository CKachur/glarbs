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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ckachur.glarbs.books.Book;
import com.ckachur.glarbs.books.BookAbility;
import com.ckachur.glarbs.books.BookType;
import com.ckachur.glarbs.books.BookTypes;
import com.ckachur.glarbs.books.BookemonBattleResultListener;
import com.ckachur.glarbs.books.BookemonTrainer;

public final class GameEnvironment {
	public static final float TILESIZE = 16f;
	private SpriteBatch spriteBatch;
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private GameCharacter devGuy;
	private BookemonTrainer devGuyBookRoster;// probably the wrong place for this object
	private int[] layersToDraw = { 0 };
	private Set<String> usedObjects = new HashSet<String>();
	private Set<String> removedObjects = new HashSet<String>();
	private List<GameCharacter> toDestroy = new ArrayList<>();
	private String lastPopupName;
	private GameCharacterKeyboardController playerController;
	//private MessagePopupListener messagePopupListener;
	private Sound doorSound;
	private Sound interactionSound;
	private Sound newBookSound;
	private OverworldGameEventsListener gameEventsListener;
	private List<GameCharacter> gameCharacters;
	private Texture devGuyTexture;

	
	public GameEnvironment(OverworldGameEventsListener messagePopupListener) {
		this.gameEventsListener = messagePopupListener;
		spriteBatch = new SpriteBatch();
		mapLoader = new TmxMapLoader();
		map = mapLoader.load("hicks.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1/TILESIZE);
		playerController = new GameCharacterKeyboardController();
		doorSound = Gdx.audio.newSound(Gdx.files.internal("sounds/dooropen.wav"));
		interactionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/select.wav"));
		newBookSound = Gdx.audio.newSound(Gdx.files.internal("sounds/MC_Fanfare_Item.wav"));
		devGuyTexture = new Texture("guySprite.png");
		devGuy = new GameCharacter("Player", new DefaultPlayerCharacterRenderer(devGuyTexture), new Vector2(10,10), playerController);
		devGuyBookRoster = new BookemonTrainer("Dev Guy", new TextureRegion(new Texture("battle/trainer.png")));
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
		for(GameCharacter character: toDestroy) {
			gameCharacters.remove(character);
		}
		toDestroy.clear();
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
			} else if( typeProperty.equals("messagePopup") && devGuy.intersects(object) && !(object.getProperties().containsKey("oneTime") && object.getProperties().get("oneTime").equals("true") && usedObjects.contains(object.getName())) && !object.getName().equals(lastPopupName) ) {
				// load the target level
				gameEventsListener.showMessagePopup(object.getProperties().get("message").toString());
				if( object.getProperties().containsKey("oneTime") && object.getProperties().get("oneTime").equals("true") ) {
					usedObjects.add(object.getName());
				}
				lastPopupName = object.getName();
			}
		}
		//Center on the player, but do not show the blank sides.
		TiledMapTileLayer mainMapLayer = (TiledMapTileLayer) map.getLayers().get(0);
		float mapWidth = mainMapLayer.getWidth();
		float mapHeight = mainMapLayer.getHeight();


		float camX = Math.max(6.75f, Math.min(mapWidth - 6.75f, devGuy.getRenderPoint().x));
		float camY = Math.max(5, Math.min(mapHeight - 5, devGuy.getRenderPoint().y));

		camera.position.set(new Vector2(camX, camY), 0);
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
			float x = Float.parseFloat(properties.get("x").toString())/GameEnvironment.TILESIZE;
			float y = Float.parseFloat(properties.get("y").toString())/GameEnvironment.TILESIZE;
			float width = Float.parseFloat(properties.get("width").toString())/GameEnvironment.TILESIZE;
			float height = Float.parseFloat(properties.get("height").toString())/GameEnvironment.TILESIZE;
			Vector2 point = new Vector2(x + width/2 - 0.5f, y + height/2 - 0.5f);
			if( removedObjects.contains(object.getName()) ) {
				continue;
			}
			if( typeProperty.equals("npc") ) {
				Texture texture = null;
				GameCharacterRenderer renderer = null;
				if( properties.containsKey("spriteSheet") ) {
					texture = new Texture(properties.get("spriteSheet").toString());
					renderer = new DefaultPlayerCharacterRenderer(texture);
				} else if ( properties.containsKey("sprite") ) {
					texture = new Texture(properties.get("sprite").toString());
					if( properties.containsKey("spriteID") && properties.containsKey("spriteSize") ) {
						int spriteID = Integer.parseInt(properties.get("spriteID").toString());
						int spriteSize = Integer.parseInt(properties.get("spriteSize").toString());
						TextureRegion[][] splitTexture = TextureRegion.split(texture, spriteSize, spriteSize);
						renderer = new StaticImageGameCharacterRenderer(splitTexture[spriteID/splitTexture[0].length][spriteID%splitTexture[0].length]);
					} else {
						renderer = new StaticImageGameCharacterRenderer(new TextureRegion(texture));
					}
				}
				GameCharacterController controller = new GameCharacterDummyController(devGuy);
				if( properties.containsKey("lockFacing") ) {
					controller = new GameCharacterLockFacingController(Facing.valueOf(properties.get("lockFacing").toString().toUpperCase()));
				}
				final GameCharacter npc = new GameCharacter(object.getName(), renderer, point, controller);
				if( properties.containsKey("talkText") ) {

					final String talkText = properties.get("talkText").toString();
					final boolean doRestore = properties.containsKey("action") && properties.get("action").equals("restore");
					npc.setInteractionListener(new GameCharacterInteractionListener() {
						@Override
						public boolean onInteracted(GameCharacter source) {
							if( source == devGuy ) {
								if( doRestore ) {
									devGuyBookRoster.restore();
								}
								interactionSound.play();
								gameEventsListener.showMessagePopup(npc.getName() + ":\n" + talkText);
								
								return true;
							}
							return false;
						}
					});
				} else if( properties.containsKey("action") && properties.get("action").equals("fight")) {
					int rewardMoney = 0;
					if( properties.containsKey("reward") ) {
						rewardMoney = Integer.parseInt(properties.get("reward").toString());
					}
					final List<Book> rewardBooks = new ArrayList<Book>();
					if( properties.containsKey("rewardBooks") ) {
						String[] books = properties.get("rewardBooks").toString().split(",");
						for(String book: books) {
							rewardBooks.add(new Book(BookTypes.TYPES.get(Integer.parseInt(book))));
						}
					}
					final int totalRewardMoney = rewardMoney;
					String battleSprite = ("battle/trainerRed.png");
					if( properties.containsKey("battleSprite") ) {
						battleSprite = properties.get("battleSprite").toString();
					}
					final BookemonTrainer trainer = new BookemonTrainer(object.getName(), new TextureRegion(new Texture(battleSprite)));
					String[] books = properties.get("books").toString().split(",");
					for(String book: books) {
						trainer.addBook(new Book(BookTypes.TYPES.get(Integer.parseInt(book))));
					}
					final boolean canFlee = !properties.containsKey("canFlee") || properties.get("canFlee").equals("true");
					final boolean bossMusic = properties.containsKey("bossMusic") && properties.get("bossMusic").equals("true");
					final boolean removeOnLose = properties.containsKey("removeOnLose") && properties.get("removeOnLose").equals("true");
					npc.setInteractionListener(new GameCharacterInteractionListener() {
						@Override
						public boolean onInteracted(GameCharacter source) {
							if( usedObjects.contains(trainer.getName()) ) {
								return true;
							}
							if( source == devGuy ) {
								if( devGuyBookRoster.isDefeated() ) {
									interactionSound.play();
									gameEventsListener.showMessagePopup("This person wants to fight you in a battle of knowledge, but you are not ready.\nCome back when you've got your books ready and at your side.");
								} else {
									trainer.restore();
									gameEventsListener.enterBattle(trainer, new BookemonBattleResultListener() {
										@Override
										public void onWin() {
											gameEventsListener.showMessagePopup("You were awarded $" + totalRewardMoney + " for defeating " + trainer.getName() + ".");
											usedObjects.add(trainer.getName());
											for(Book book: rewardBooks) {
												if( devGuyBookRoster.addBook(book)) {
													newBookSound.play();
													String message = "You were awarded $" + totalRewardMoney + " for defeating " + trainer.getName() + ".\nYou also got a new book for winning: " + book.getName() + "!\n\nAbilities:\n";
													for(BookAbility ability: book.getAbilities()) {
														if( ability != null ) {
															message += " - " + ability.getName() + "\n";
														}
													}
													gameEventsListener.showMessagePopup(message);
													removedObjects.add(book.getName());
													break; // it only can show one right now TODO fix this
												}
											}
										}
										
										@Override
										public void onLose() {
											gameEventsListener.showMessagePopup("You should go repair your books.");
											if( removeOnLose ) {
												destroy(npc);
											}
										}
										
										@Override
										public void onFlee() {
											if( removeOnLose ) {
												destroy(npc);
											}
										}
									}, canFlee, bossMusic);
								}
								return true;
							}
							return false;
						}
					});
				}
				gameCharacters.add(npc);
			} else if (typeProperty.equals("book")) {
				GameCharacterController controller = new GameCharacterLockFacingController(Facing.DOWN);
				if( properties.containsKey("lockFacing") ) {
					controller = new GameCharacterLockFacingController(Facing.valueOf(properties.get("lockFacing").toString().toUpperCase()));
				}
				final BookType bookType = BookTypes.TYPES.get(Integer.parseInt(properties.get("bookId").toString()));
				GameCharacterRenderer renderer = new StaticImageGameCharacterRenderer(bookType.getClosedIcon());
				final GameCharacter book = new GameCharacter(object.getName(), renderer, point, controller);
				book.setInteractionListener(new GameCharacterInteractionListener() {
					@Override
					public boolean onInteracted(GameCharacter source) {
						Book bookemon = new Book(bookType);
						if( devGuyBookRoster.addBook(bookemon)) {
							newBookSound.play();
							String message = "You got a new book: " + bookemon.getName() + "!\n\nAbilities:\n";
							for(BookAbility ability: bookemon.getAbilities()) {
								if( ability != null ) {
									message += " - " + ability.getName() + "\n";
								}
							}
							gameEventsListener.showMessagePopup(message);
							removedObjects.add(book.getName());
							destroy(book);
						}
						return true;
					}
				});
				gameCharacters.add(book);
			}
		}
	}
	
	public void destroy(GameCharacter character) {
		toDestroy.add(character);
	}

	public void enableControls() {
		playerController.setEnabled(true);
	}
	
	public void disableControls() {
		playerController.setEnabled(false);
	}
	
	public BookemonTrainer getDevGuyBookRoster() {
		return devGuyBookRoster;
	}
}

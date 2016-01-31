package com.ckachur.glarbs.books;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ckachur.glarbs.Glarbs;

public class BookemonBattleScreen extends ScreenAdapter implements Screen {
    Glarbs glarbs;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BookemonTrainer lowerTrainer, upperTrainer;
    private Texture messageBackdrop;
    private Texture ballBracket;
    private SpriteBatch spriteBatch;
	private String currentMessage;
	private float stateTime;
	private StringBuffer currentMessageBuffer;
	private BitmapFont font;
	private BattleState state;
	private GlyphLayout glyphLayout;
	private BattleScreenOptions options;
	private final BattleScreenOptions basicOptions;
	private boolean canFlee = true;
	private Runnable messageAction;
	private BookemonBattleResultListener resultListener;
	
	private final Runnable enemyTurn = new Runnable() {
		@Override
		public void run() {
			List<BookAbility> randomizedChoices = new ArrayList<BookAbility>();
			Book enemyBook = upperTrainer.getActiveBook();
			if( enemyBook.isDead() ) {
				if( !upperTrainer.isDefeated() ) {
					int nextPokemon = upperTrainer.getFirstLivingBookIndex();
					messageAction = new Runnable() {
						@Override
						public void run() {
							upperTrainer.setActiveBookIndex(nextPokemon);
							options = basicOptions;
						}
					};
					showMessagePopup(upperTrainer.getName() + " chooses " + upperTrainer.getBooks()[nextPokemon].getName() + "!");
				} else {
					messageAction = new Runnable() {
						@Override
						public void run() {
							glarbs.setScreen(glarbs.getOverworldScreen());
							resultListener.onWin();
						}
					};
					showMessagePopup(upperTrainer.getName() + " has been defeated! " + lowerTrainer.getName() + " is the winner!");
				}
				return;
			}
			for(BookAbility ability: enemyBook.getAbilities()) {
				if( ability != null && enemyBook.getPPRemaining(ability) > 0 ) {
					randomizedChoices.add(ability);
				}
			}
			if( randomizedChoices.isEmpty() ) {
				showMessagePopup(enemyBook.getName() + " is out of moves, and did nothing.");
				return;
			}
			Collections.shuffle(randomizedChoices);
			BookAbility bookAbility = randomizedChoices.get(0);
			if( enemyBook.use(bookAbility, lowerTrainer.getActiveBook()) ) {
				showMessagePopup(enemyBook.getName() + " used " + bookAbility.getName() + "!");
			} else {
				showMessagePopup(enemyBook.getName() + " has no more pp remaining for " + bookAbility.getName() + "!\nIt's turn was wasted.");
			}
			if( lowerTrainer.isDefeated() ) {
				messageAction = new Runnable() {
					@Override
					public void run() {
						showMessagePopup(lowerTrainer.getName() + " was defeated by " + upperTrainer.getName() + "!");
						messageAction = new Runnable() {
							@Override
							public void run() {
								glarbs.setScreen(glarbs.getOverworldScreen());
								resultListener.onLose();
							}
						};
					}
				};
			}
		}
	};

    public BookemonBattleScreen(Glarbs glarbs, BookemonTrainer lowerTrainer, BookemonTrainer upperTrainer, BookemonBattleResultListener listener) {
		this.glarbs = glarbs;
		this.lowerTrainer = lowerTrainer;
		this.upperTrainer = upperTrainer;
		this.resultListener = listener;
		currentMessage = "You have entered a battle with " + upperTrainer.getName() + "!\n\nPress SPACE to begin.";
		state = BattleState.SLIDE_IN;
        
        // create basic fight options

        basicOptions = new BattleScreenOptions();
        basicOptions.addOption("FIGHT", new BattleScreenOptionListener() {
			@Override
			public void onSelected() {
				options = new BattleScreenOptions();
				for(final BookAbility ability: lowerTrainer.getActiveBook().getAbilities()) {
					if( ability != null ) {
						options.addOption(ability.getName() + " " + lowerTrainer.getActiveBook().getPPRemaining(ability) + "/" + ability.getMaxPP(), new BattleScreenOptionListener() {
							@Override
							public void onSelected() {
								options = null;
								messageAction = enemyTurn;
								if( lowerTrainer.getActiveBook().use(ability, upperTrainer.getActiveBook()) ) {
									showMessagePopup(lowerTrainer.getActiveBook().getName() + " used " + ability.getName() +".");
								} else {
									showMessagePopup(lowerTrainer.getActiveBook().getName() + " has no more pp remaining for " + ability.getName() +".");
								}
							}
						});
					}
				}
			}
		});
        basicOptions.addOption("PACK", new BattleScreenOptionListener() {
			@Override
			public void onSelected() {
				showMessagePopup("There is no pack coded into this game.");
			}
		});
        basicOptions.addOption("SWAP", new BattleScreenOptionListener() {
			@Override
			public void onSelected() {
				showMessagePopup("You can't swap because honestly who wants to program that?");
			}
		});
        basicOptions.addOption("RUN", new BattleScreenOptionListener() {
			@Override
			public void onSelected() {
				if( canFlee ) {
					messageAction = new Runnable() {
						@Override
						public void run() {
		    				glarbs.setScreen(glarbs.getOverworldScreen());
		    				resultListener.onFlee();
						}
					};
					showMessagePopup("You flee from " + upperTrainer.getName() + ".");
				} else {
					showMessagePopup("Unable to flee.");
				}
			}
		});
	}
    
    public static BookemonBattleScreen createTestBattle(Glarbs glarbs) {
    	BookemonTrainer trainer1 = new BookemonTrainer("Player", new TextureRegion(new Texture("battle/trainer.png")));
    	trainer1.setBook(0, new Book(BookTypes.ANSI_C));
    	BookemonTrainer trainer2 = new BookemonTrainer("PROFESSOR", new TextureRegion(new Texture("battle/professor.png")));
    	trainer2.setBook(0, new Book(BookTypes.ENG_106));
    	trainer2.setBook(1, new Book(BookTypes.ENG_106));
    	trainer2.setBook(2, new Book(BookTypes.ENG_106));
    	return new BookemonBattleScreen(glarbs, trainer1, trainer2, new BookemonBattleResultListener() {
			@Override
			public void onWin() {
				
			}
			
			@Override
			public void onLose() {
				
			}

			@Override
			public void onFlee() {
				
			}
		});
    }
    public void setCanFlee(boolean canFlee) {
		this.canFlee = canFlee;
	}
	@Override
    public void show()
    {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        viewport = new FitViewport(640, 480, camera);
        viewport.apply();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch = new SpriteBatch();
        
        // fancy elements for battle screen
        ballBracket = new Texture("battle/ballBracket.png");

        // code for messages on the screen, should probably move elsewhere later
        currentMessageBuffer = new StringBuffer();
        messageBackdrop = new Texture("textbox.png");
        font = new BitmapFont();
        spriteBatch = new SpriteBatch();
        glyphLayout = new GlyphLayout();
    }

    @Override
    public void render (float delta) {
    	if( currentMessage != null && Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ) {
    		currentMessage = null;
    		if( state == BattleState.SLIDE_IN ) {
    			state = BattleState.SPAWN_POKEMON;
    			stateTime = 0;
    		}
    		if( state == BattleState.FIGHT ) {
    			if( messageAction != null ) {
    				Runnable currentAction = messageAction;
    				messageAction = null;
    				currentAction.run();
    			} else if( options == null ) {
    				options = basicOptions;
    			}
    		}
    	} else if( options != null ) {
    		options.update();
    	}
    	
    	
    	stateTime += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        float messageBackdropHeight = viewport.getWorldWidth() * messageBackdrop.getHeight() / messageBackdrop.getWidth();
        spriteBatch.draw(messageBackdrop, 0, 0,viewport.getWorldWidth(), messageBackdropHeight);
        
        if( state == BattleState.SLIDE_IN || state == BattleState.SPAWN_POKEMON ) {
            float dudeWidth = viewport.getWorldHeight()/3;
            float trainerPositionAtTime = stateTime*300;
    		float upperDudeX = Math.min(viewport.getWorldWidth()-dudeWidth, trainerPositionAtTime);
            float lowerDudeX = Math.max(0, viewport.getWorldWidth() - dudeWidth - trainerPositionAtTime);
            if( state == BattleState.SPAWN_POKEMON ) {
            	upperDudeX = viewport.getWorldWidth()-dudeWidth + trainerPositionAtTime;
            	lowerDudeX = 0 - trainerPositionAtTime;
            }
            spriteBatch.draw(upperTrainer.getTextureRegion(), upperDudeX, viewport.getWorldHeight()-dudeWidth, dudeWidth, dudeWidth);
    		spriteBatch.draw(lowerTrainer.getTextureRegion(), lowerDudeX, messageBackdropHeight, dudeWidth, dudeWidth);
            boolean pokemonSpawnTime = state == BattleState.SPAWN_POKEMON;// && trainerPositionAtTime < dudeWidth*3;
			if( (state == BattleState.SLIDE_IN && trainerPositionAtTime > viewport.getWorldWidth()-dudeWidth)
            		|| (pokemonSpawnTime)) {
            	
            	float ballBracketIndent = 16;
            	int ballBracketWidth = ballBracket.getWidth()*3;
            	int ballBracketHeight = ballBracket.getHeight()*3;
    			float ballBracketX = viewport.getWorldWidth()-ballBracketWidth - ballBracketIndent;
    			float ballBracketY = messageBackdropHeight + ballBracketIndent;
    			spriteBatch.draw(ballBracket, ballBracketX, ballBracketY, ballBracketWidth, ballBracketHeight);
    			
    			float enemyBracketX = ballBracketIndent;
    			float enemyBracketY = viewport.getWorldHeight()-dudeWidth + ballBracketIndent;
    			spriteBatch.draw(ballBracket, enemyBracketX, enemyBracketY, ballBracketWidth, ballBracketHeight, 0, 0, ballBracket.getWidth(), ballBracket.getHeight(), true, false);

    			if( state != BattleState.SPAWN_POKEMON || trainerPositionAtTime < dudeWidth*3 ) {
        			float bookIconSize = 32;
        			Book[] upperBooks = upperTrainer.getBooks();
        			for(int i = 0; i < upperBooks.length; i++) {
        				Book book = upperBooks[i];
        				TextureRegion textureRegion;
        				if( book != null ) {
        					textureRegion = book.getBookType().getClosedIcon();
        				} else {
        					textureRegion = BookTypes.EMPTY_TEXTURE;
        				}
        				if( book != null && book.isDead() ) {
        					spriteBatch.setColor(Color.BLACK);
        				} else {
        					spriteBatch.setColor(Color.WHITE);
        				}
        				spriteBatch.draw(textureRegion, enemyBracketX + ballBracketWidth - bookIconSize * (i+1), enemyBracketY + ballBracketIndent,bookIconSize, bookIconSize);
        			}
        			Book[] lowerBooks = lowerTrainer.getBooks();
        			for(int i = 0; i < lowerBooks.length; i++) {
        				Book book = lowerBooks[i];
        				TextureRegion textureRegion;
        				if( book != null ) {
        					textureRegion = book.getBookType().getClosedIcon();
        				} else {
        					textureRegion = BookTypes.EMPTY_TEXTURE;
        				}
        				if( book != null && book.isDead() ) {
        					spriteBatch.setColor(Color.BLACK);
        				} else {
        					spriteBatch.setColor(Color.WHITE);
        				}
        				spriteBatch.draw(textureRegion, ballBracketX + bookIconSize * i, ballBracketY + ballBracketIndent,bookIconSize, bookIconSize);
        			}
    			}
    			if( pokemonSpawnTime && trainerPositionAtTime > dudeWidth*2 ) {
    				TextureRegion openIcon = upperTrainer.getActiveBook().getBookType().getOpenIcon();
    				int bookWidth = openIcon.getRegionWidth()*8;
    				int bookHeight = openIcon.getRegionHeight()*8;
					spriteBatch.draw(openIcon, viewport.getWorldWidth()-(dudeWidth+bookWidth)/2, viewport.getWorldHeight()-(dudeWidth+bookHeight)/2 + ballBracketIndent, bookWidth, bookHeight);
    				openIcon = lowerTrainer.getActiveBook().getBookType().getOpenIcon();
    				spriteBatch.draw(openIcon, (dudeWidth-bookWidth)/2, messageBackdropHeight + (dudeWidth-bookHeight)/2, bookWidth, bookHeight);
    			}
            }
			if( state == BattleState.SPAWN_POKEMON && trainerPositionAtTime > dudeWidth*3 ) {
				state = BattleState.FIGHT;
				options = basicOptions;
			}
        } else if( state == BattleState.FIGHT ) {
//        	lowerTrainer.update();
//        	upperTrainer.update();
        	
        	
            float dudeWidth = viewport.getWorldHeight()/3;
    		float upperDudeX = viewport.getWorldWidth()-dudeWidth;
            float lowerDudeX = 0;
        	
            // ================Draw the brackets holding the HP bars================
        	float ballBracketIndent = 16;
        	int ballBracketWidth = ballBracket.getWidth()*3;
        	int ballBracketHeight = ballBracket.getHeight()*3;
			float ballBracketX = viewport.getWorldWidth()-ballBracketWidth - ballBracketIndent;
			float ballBracketY = messageBackdropHeight + ballBracketIndent;
			spriteBatch.draw(ballBracket, ballBracketX, ballBracketY, ballBracketWidth, ballBracketHeight);

			// setup for brackets
			float bookIconSize = 32;
			float enemyBracketX = ballBracketIndent;
			float enemyBracketY = viewport.getWorldHeight()-dudeWidth + ballBracketIndent;
			
			spriteBatch.draw(ballBracket, enemyBracketX, enemyBracketY, ballBracketWidth, ballBracketHeight, 0, 0, ballBracket.getWidth(), ballBracket.getHeight(), true, false);

			if( !lowerTrainer.getActiveBook().isDead() ) {
				// draw HP bar for player
				font.draw(spriteBatch, lowerTrainer.getActiveBook().getName(), ballBracketX, ballBracketY + ballBracketHeight + font.getLineHeight());
				font.draw(spriteBatch, lowerTrainer.getActiveBook().getHealth() + "/" + lowerTrainer.getActiveBook().getMaxHealth(), ballBracketX, ballBracketY + ballBracketHeight);
				
				
				TextureRegion openIcon = lowerTrainer.getActiveBook().getBookType().getOpenIcon();
				int bookWidth = openIcon.getRegionWidth()*8;
				int bookHeight = openIcon.getRegionHeight()*8;
				spriteBatch.draw(openIcon, (dudeWidth-bookWidth)/2, messageBackdropHeight + (dudeWidth-bookHeight)/2, bookWidth, bookHeight);
			} else {
	    		spriteBatch.draw(lowerTrainer.getTextureRegion(), lowerDudeX, messageBackdropHeight, dudeWidth, dudeWidth);
    			Book[] lowerBooks = lowerTrainer.getBooks();
    			for(int i = 0; i < lowerBooks.length; i++) {
    				Book book = lowerBooks[i];
    				TextureRegion textureRegion;
    				if( book != null ) {
    					textureRegion = book.getBookType().getClosedIcon();
    				} else {
    					textureRegion = BookTypes.EMPTY_TEXTURE;
    				}
    				if( book != null && book.isDead() ) {
    					spriteBatch.setColor(Color.BLACK);
    				} else {
    					spriteBatch.setColor(Color.WHITE);
    				}
    				spriteBatch.draw(textureRegion, ballBracketX + bookIconSize * i, ballBracketY + ballBracketIndent,bookIconSize, bookIconSize);
    			}
			}
			if( !upperTrainer.getActiveBook().isDead() ) {
				// draw HP bar for enemy
				String text = upperTrainer.getActiveBook().getName();
				glyphLayout.setText(font, text);
				font.draw(spriteBatch, text, enemyBracketX + ballBracketWidth - glyphLayout.width, enemyBracketY + ballBracketHeight + font.getLineHeight());
				text = upperTrainer.getActiveBook().getHealth() + "/" + upperTrainer.getActiveBook().getMaxHealth();
				glyphLayout.setText(font, text);
				font.draw(spriteBatch, text, enemyBracketX + ballBracketWidth - glyphLayout.width, enemyBracketY + ballBracketHeight);

				TextureRegion openIcon = upperTrainer.getActiveBook().getBookType().getOpenIcon();
				int bookWidth = openIcon.getRegionWidth()*8;
				int bookHeight = openIcon.getRegionHeight()*8;
				spriteBatch.draw(openIcon, viewport.getWorldWidth()-(dudeWidth+bookWidth)/2, viewport.getWorldHeight()-(dudeWidth+bookHeight)/2 + ballBracketIndent, bookWidth, bookHeight);
			} else {
	            spriteBatch.draw(upperTrainer.getTextureRegion(), upperDudeX, viewport.getWorldHeight()-dudeWidth, dudeWidth, dudeWidth);
    			Book[] upperBooks = upperTrainer.getBooks();
    			for(int i = 0; i < upperBooks.length; i++) {
    				Book book = upperBooks[i];
    				TextureRegion textureRegion;
    				if( book != null ) {
    					textureRegion = book.getBookType().getClosedIcon();
    				} else {
    					textureRegion = BookTypes.EMPTY_TEXTURE;
    				}
    				if( book != null && book.isDead() ) {
    					spriteBatch.setColor(Color.BLACK);
    				} else {
    					spriteBatch.setColor(Color.WHITE);
    				}
    				spriteBatch.draw(textureRegion, enemyBracketX + ballBracketWidth - bookIconSize * (i+1), enemyBracketY + ballBracketIndent,bookIconSize, bookIconSize);
    			}
			}
			
        }
        if( options != null ) {
        	options.render(viewport, spriteBatch);
        }
        
        
        if( currentMessage != null ) {
            for(int i = currentMessageBuffer.length(); i < Math.min(stateTime*60,currentMessage.length()); i++) {
                currentMessageBuffer.append(currentMessage.charAt(i));
            }
            font.setColor(Color.BLACK);
            String textToPrint = currentMessageBuffer.toString();
            String[] linesOfTextToPrint = textToPrint.split("\n");
            int linesPrinted = 0;
            for(String line: linesOfTextToPrint) {
                font.draw(spriteBatch, line, font.getLineHeight()*2, messageBackdropHeight - font.getLineHeight()*(2 + linesPrinted));
                linesPrinted++;
            }
        }
        
        spriteBatch.end();
    }

    public void showMessagePopup(String message) {
        message = message.replace("\\n", "\n");
        stateTime = 0;
        options = null;
        currentMessageBuffer.setLength(0);
        currentMessage = message;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
    }
    
    public static enum BattleState {
    	SLIDE_IN, SPAWN_POKEMON, FIGHT;
    }
}

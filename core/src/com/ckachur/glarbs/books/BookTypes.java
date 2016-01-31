package com.ckachur.glarbs.books;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BookTypes {
	public static final ArrayList<BookType> TYPES = new ArrayList<BookType>();
	public static final BookType ANSI_C;
	public static final BookType ENG_106;
	public static final TextureRegion EMPTY_TEXTURE;
	
	static {
		TextureRegion[][] split = TextureRegion.split(new Texture("glarbsBooks.png"), 16, 16);
		ANSI_C = new BookType("ANSI C Programming", split[0][2], split[0][0], 40, 6);
		ANSI_C.addAbilityLearned(1, new BookAbilityBasicDamage("strchomp", 15, 10));
		ANSI_C.addAbilityLearned(1, new BookAbilityBasicDamage("malloc", 45, 4));
		TYPES.add(ANSI_C);
		ENG_106 = new BookType("English 106", split[1][2], split[1][0], 30, 4);
		ENG_106.addAbilityLearned(1, new BookAbilityBasicDamage("Metaphor", 10, 30));
		TYPES.add(ENG_106);
		
		EMPTY_TEXTURE = split[0][4];
	}
}

package com.ckachur.glarbs.books;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BookTypes {
	public static final ArrayList<BookType> TYPES = new ArrayList<BookType>();
	public static final BookType ANSI_C;
	public static final BookType ENG_106;
	public static final BookType INTRO_TO_ALGORITHMS;
	public static final BookType COMPUTER_ARCHITECTURE;
	public static final BookType BOOK_OF_MORMON;
	public static final TextureRegion EMPTY_TEXTURE;
	
	static {
		TextureRegion[][] split = TextureRegion.split(new Texture("glarbsBooks.png"), 16, 16);
		ANSI_C = new BookType("ANSI C Programming", split[1][3], split[1][1], 40, 6);
		ANSI_C.addAbilityLearned(1, new BookAbilityBasicDamage("strchomp", 15, 10));
		ANSI_C.addAbilityLearned(1, new BookAbilityBasicSelfHeal("malloc", 45, 4));
		TYPES.add(ANSI_C);
		ENG_106 = new BookType("English 106", split[1][2], split[1][0], 30, 4);
		ENG_106.addAbilityLearned(1, new BookAbilityBasicDamage("Metaphor", 10, 30));
		TYPES.add(ENG_106);
		INTRO_TO_ALGORITHMS = new BookType("Introduction to Algorithms", split[0][3], split[0][1], 35, 10);
		INTRO_TO_ALGORITHMS.addAbilityLearned(1, new BookAbilityBasicDamage("Contradiction", 8, 30));
		INTRO_TO_ALGORITHMS.addAbilityLearned(1, new BookAbilityBasicDamage("Induction", 18, 10));
		TYPES.add(INTRO_TO_ALGORITHMS);
		COMPUTER_ARCHITECTURE = new BookType("Computer Architecture", split[3][3], split[3][1], 35, 10);
		COMPUTER_ARCHITECTURE.addAbilityLearned(1, new BookAbilityBasicDamage("Intel x86", 20, 30));
		COMPUTER_ARCHITECTURE.addAbilityLearned(1, new BookAbilityBasicDamage("Cache Hit", 100, 3));
		TYPES.add(COMPUTER_ARCHITECTURE);
		BOOK_OF_MORMON = new BookType("Book of Mormon", split[0][2], split[0][0], 40, 6);
		BOOK_OF_MORMON.addAbilityLearned(1, new BookAbilityBasicDamage("Free Pamphlets", 20, 999));
		BOOK_OF_MORMON.addAbilityLearned(1, new BookAbilityBasicSelfHeal("Prayer", 10, 6));
		TYPES.add(BOOK_OF_MORMON);
		
		EMPTY_TEXTURE = split[0][4];
	}
}

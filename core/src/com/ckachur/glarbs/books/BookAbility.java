package com.ckachur.glarbs.books;

public interface BookAbility {
	void doEffect(Book source, Book opponent);
	int getMaxPP();
	String getName();
}

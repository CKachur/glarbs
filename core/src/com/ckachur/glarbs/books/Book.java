package com.ckachur.glarbs.books;

import java.util.HashMap;
import java.util.Map;

public class Book {
	private String name;
	private BookType bookType;
	private int health;
	private int level;
	private BookAbility[] abilities;
	private Map<BookAbility,Integer> ppRemaining;
	public Book(BookType bookType) {
		name = bookType.getName();
		this.bookType = bookType;
		level = 1;
		health = bookType.getMaxHealth(level);
		abilities = new BookAbility[4];
		ppRemaining = new HashMap<>();
		for(BookAbility ability: bookType.getAbilitiesLearnedAtLevel(level)) {
			addAbility(ability);
		}
	}
	public String getName() {
		return name;
	}
	public BookType getBookType() {
		return bookType;
	}
	public void setBookType(BookType bookType) {
		this.bookType = bookType;
	}
	public int getHealth() {
		return health;
	}
	public int getMaxHealth() {
		return bookType.getMaxHealth(level);
	}
	public void setHealth(int health) {
		int maxHealth = getMaxHealth();
		if( health > maxHealth ) {
			health = maxHealth;
		}
		this.health = health;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public boolean isDead() {
		return health <= 0;
	}
	public void setAbility(int index, BookAbility ability) {
		abilities[index] = ability;
	}
	public boolean addAbility(BookAbility ability) {
		for(int i = 0; i < abilities.length; i++) {
			if( abilities[i] == null ) {
				abilities[i] = ability;
				ppRemaining.put(ability, ability.getMaxPP());
				return true;
			}
		}
		return false;
	}
	public BookAbility[] getAbilities() {
		return abilities;
	}
	public int getPPRemaining(BookAbility ability) {
		Integer pp = ppRemaining.get(ability);
		if( pp == null ) {
			pp = 0;
		}
		return pp;
	}
	public boolean use(BookAbility ability, Book other) {
		int remaining = getPPRemaining(ability);
		if( remaining > 0 ) {
			ppRemaining.put(ability, remaining-1);
			ability.doEffect(this, other);
			return true;
		}
		return false;
	}
	public void restore() {
		setHealth(getMaxHealth());
		for(BookAbility ability: abilities) {
			if( ability != null ) {
				ppRemaining.put(ability, ability.getMaxPP());
			}
		}
	}
}

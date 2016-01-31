package com.ckachur.glarbs.books;

public class Book {
	private String name;
	private BookType bookType;
	private int health;
	private int level;
	public Book(BookType bookType) {
		name = bookType.getName();
		this.bookType = bookType;
		level = 1;
		health = bookType.getMaxHealth(level);
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
}

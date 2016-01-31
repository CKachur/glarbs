package com.ckachur.glarbs.books;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BookemonTrainer {
	private String name;
	private final Book[] books;
	private TextureRegion textureRegion;
	private int activeBookIndex;
	public BookemonTrainer(String name, TextureRegion textureRegion) {
		this.name = name;
		this.textureRegion = textureRegion;
		books = new Book[6];
	}
	public String getName() {
		return name;
	}
	public TextureRegion getTextureRegion() {
		return textureRegion;
	}
	public void setBook(int index, Book book) {
		books[index] = book;
	}
	public Book[] getBooks() {
		return books;
	}
	public boolean isDefeated() {
		for(Book book: books) {
			if( book != null && !book.isDead() ) {
				return false;
			}
		}
		return true;
	}
	public Book getActiveBook() {
		return books[activeBookIndex];
	}
	public int getActiveBookIndex() {
		return activeBookIndex;
	}
	public void setActiveBookIndex(int activeBookIndex) {
		this.activeBookIndex = activeBookIndex;
	}
//	public void update() {
//		if( books[activeBookIndex] == null || books[activeBookIndex].isDead() ) {
//			int firstLivingBookIndex = getFirstLivingBookIndex();
//			if( firstLivingBookIndex >= 0 ) {
//				activeBookIndex = firstLivingBookIndex;
//			}
//		}
//	}
	public int getBookIndex(Book book) {
		for(int i = 0; i < books.length; i++) {
			if( books[i] == book ) {
				return i;
			}
		}
		return -1;
	}
	public int getFirstLivingBookIndex() {
		for(int i = 0; i < books.length; i++) {
			if( books[i] != null && !books[i].isDead() ) {
				return i;
			}
		}
		return -1;
	}
	public boolean addBook(Book book) {
		for(int i = 0; i < books.length; i++) {
			if( books[i] == null ) {
				books[i] = book;
				return true;
			}
		}
		return false;
	}
	public void restore() {
		for(Book book: books) {
			if( book != null ) {
				book.restore();
			}
		}
	}
}

package com.ckachur.glarbs.books;

public class BookAbilityBasicSelfHeal implements BookAbility {
	private final String name;
	private final int healthRestored;
	private final int maxPP;

	public BookAbilityBasicSelfHeal(String name, int healthRestored, int maxPP) {
		this.name = name;
		this.healthRestored = healthRestored;
		this.maxPP = maxPP;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void doEffect(Book source, Book opponent) {
		source.setHealth(source.getHealth()+healthRestored);
	}

	@Override
	public int getMaxPP() {
		return maxPP;
	}

}

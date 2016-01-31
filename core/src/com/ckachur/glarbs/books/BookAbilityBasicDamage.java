package com.ckachur.glarbs.books;

public class BookAbilityBasicDamage implements BookAbility {
	private final String name;
	private final int damage;
	private final int maxPP;

	public BookAbilityBasicDamage(String name, int damage, int maxPP) {
		this.name = name;
		this.damage = damage;
		this.maxPP = maxPP;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void doEffect(Book source, Book opponent) {
		opponent.setHealth(opponent.getHealth()-damage);
	}

	@Override
	public int getMaxPP() {
		return maxPP;
	}

}

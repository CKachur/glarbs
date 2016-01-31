package com.ckachur.glarbs.books;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class BookType {
	private final TextureRegion closedIcon;
	private final TextureRegion openIcon;
	private final Map<Integer, BookAbility> levelToAbilityLearned;
	private String name;
	private int maxHealth;
	private int healthPerLevel;
	public BookType(String name, TextureRegion closedIcon, TextureRegion openIcon, int baseMaxHealth, int healthPerLevel) {
		this.name = name;
		this.closedIcon = closedIcon;
		this.openIcon = openIcon;
		this.levelToAbilityLearned = new HashMap<>();
		this.maxHealth = baseMaxHealth;
		this.healthPerLevel = healthPerLevel;
	}
	public String getName() {
		return name;
	}
	public TextureRegion getClosedIcon() {
		return closedIcon;
	}
	public TextureRegion getOpenIcon() {
		return openIcon;
	}
	public Map<Integer, BookAbility> getLevelToAbilityLearned() {
		return levelToAbilityLearned;
	}
	public void addAbilityLearned(int level, BookAbility ability) {
		levelToAbilityLearned.put(level, ability);
	}
	public int getMaxHealth(int level) {
		return maxHealth + healthPerLevel * level;
	}
	public int getHealthPerLevel() {
		return healthPerLevel;
	}
}

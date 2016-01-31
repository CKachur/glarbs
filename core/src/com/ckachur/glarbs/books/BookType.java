package com.ckachur.glarbs.books;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class BookType {
	private final TextureRegion closedIcon;
	private final TextureRegion openIcon;
	private final Map<Integer, List<BookAbility>> levelToAbilityLearned;
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
	public List<BookAbility> getAbilitiesLearnedAtLevel(int level) {
		List<BookAbility> abilities = levelToAbilityLearned.get(level);
		if( abilities == null ) {
			abilities = new ArrayList<>();
			levelToAbilityLearned.put(level, abilities);
		}
		return abilities;
	}
	public void addAbilityLearned(int level, BookAbility ability) {
		getAbilitiesLearnedAtLevel(level).add(ability);
	}
	public int getMaxHealth(int level) {
		return maxHealth + healthPerLevel * level;
	}
	public int getHealthPerLevel() {
		return healthPerLevel;
	}
}

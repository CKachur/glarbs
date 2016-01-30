package com.ckachur.glarbs;

public interface GameCharacterInteractionListener {
	/**
	 * Called on the outside when another game character is about to
	 * step on top of this one. Instead, we can return 'true' to say
	 * that the other character interacted with us, and must be
	 * halted.
	 * 
	 * @param source Who is trying to step on us
	 * @return Whether we stop them from stepping on us
	 */
	boolean onInteracted(GameCharacter source);
}

package com.ckachur.glarbs;

public interface GameCharacterController {
	Facing getNextDirection(GameCharacter character);
	boolean wantsMove(GameCharacter character);
}

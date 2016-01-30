package com.ckachur.glarbs;

/**
 * A dummy character control, to make them always face our main character.
 * 
 * @author Eric
 *
 */
public class GameCharacterLockFacingController implements GameCharacterController {
	
	private Facing facing;

	public GameCharacterLockFacingController(Facing facing) {
		this.facing = facing;
	}

	@Override
	public Facing getNextDirection(GameCharacter aiCharacter) {
		return facing;
	}

	@Override
	public boolean wantsMove(GameCharacter character) {
		return false;
	}

}

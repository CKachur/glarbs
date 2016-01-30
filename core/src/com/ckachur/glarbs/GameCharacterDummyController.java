package com.ckachur.glarbs;

/**
 * A dummy character control, to make them always face our main character.
 * 
 * @author Eric
 *
 */
public class GameCharacterDummyController implements GameCharacterController {
	
	private final GameCharacter whomToFace;

	public GameCharacterDummyController(GameCharacter toFace) {
		this.whomToFace = toFace;
	}

	@Override
	public Facing getNextDirection(GameCharacter aiCharacter) {
		float myFacingAngle = aiCharacter.angleTowards(whomToFace.getPoint());
		if( myFacingAngle >= -45 && myFacingAngle < 45 ) {
			return Facing.RIGHT;
		} else if( myFacingAngle >= 45 && myFacingAngle < 135 ) {
			return Facing.UP;
		} else if( myFacingAngle >= 135 && myFacingAngle < 215 || myFacingAngle < -135 ) {
			return Facing.LEFT;
		} else if( myFacingAngle >= -135 && myFacingAngle < -45 ) {
			return Facing.DOWN;
		}
		return null;
	}

	@Override
	public boolean wantsMove(GameCharacter character) {
		return false;
	}

}

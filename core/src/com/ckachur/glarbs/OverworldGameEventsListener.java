package com.ckachur.glarbs;

import com.ckachur.glarbs.books.BookemonBattleResultListener;
import com.ckachur.glarbs.books.BookemonTrainer;

public interface OverworldGameEventsListener {
	void showMessagePopup(String message);
	void enterBattle(BookemonTrainer opponent, BookemonBattleResultListener resultListener);
}

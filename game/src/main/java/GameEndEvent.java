package main.java;

import javafx.event.Event;
import javafx.event.EventType;

/** Triggered when the game ends. **/
public class GameEndEvent extends Event
{
	public static final EventType<GameEndEvent> ANY = new EventType<>(Event.ANY, "GAME_END_EVENT");

	public static final EventType<GameEndEvent> WIN = new EventType<>(ANY, "WIN");

	public static final EventType<GameEndEvent> LOSE = new EventType<>(ANY, "LOSE");

	/** Did the game end in a win or lose? **/
	private boolean win;

	public GameEndEvent(EventType<? extends Event> eventType, boolean win)
	{
		super(eventType);
		this.win = win;
	}

	/** Gets if the game was won.
	 * @return true if win, false if loss **/
	public boolean isWin()
	{
		return win;
	}
}

package com.chriscarr.game;

import com.chriscarr.bang.Character;
import com.chriscarr.bang.Role;
import com.chriscarr.bang.Setup;
import com.chriscarr.bang.gamestate.GameStateListener;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.HashMap;
import java.util.Map;


public class WebInit {
	
	private static final Map<Integer, UserInterface> userInterfaces = new HashMap<>();
	private static final Map<Integer, GameStateListener> gameStateListeners = new HashMap<>();
		
	public void setup(int numPlayers, UserInterface userInterface, GameStateListener gameStateListener, int gameId, boolean sidestep, Role pRole, Character pChar){
		Thread gameThread = new GameThread(numPlayers, userInterface, gameStateListener, sidestep, pRole, pChar);
		userInterfaces.put(gameId, userInterface);
		gameStateListeners.put(gameId, gameStateListener);
		gameThread.start();
	}
	
	public static UserInterface getUserInterface(int gameId){
		return WebInit.userInterfaces.get(gameId);
	}
	
	public static void remove(int gameId){
		userInterfaces.remove(gameId);
		gameStateListeners.remove(gameId);
	}
	
	static class GameThread extends Thread{
		int numPlayers;
		UserInterface userInterface;
		GameStateListener gameStateListener;
		boolean sidestep;
		Role pRole;
		Character pChar;
		GameThread(int numPlayers, UserInterface userInterface, GameStateListener gameStateListener, boolean sidestep, Role pRole, Character pChar){
				this.numPlayers = numPlayers;
				this.userInterface = userInterface;
				this.gameStateListener = gameStateListener;
				this.sidestep = sidestep;
				this.pRole = pRole;
				this.pChar = pChar;
		}
		public void run(){
			new Setup(numPlayers, userInterface, gameStateListener, sidestep, pRole, pChar);
		}
	}
}



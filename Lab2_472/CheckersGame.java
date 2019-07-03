package Lab2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CheckersGame<S, A, P> implements Game<S, A, P> {

	private static State state;

	private static Action action;

	private static Player[] players;

	private static int turn; // 0 - Red's turn //1 - Black's turn

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		CheckersGame<State, Action, Player> game = new CheckersGame<State, Action, Player>();
		State currentState = game.getstate();
		AlphaBetaSearch<State, Action, Player> search = new AlphaBetaSearch<State, Action, Player>(game);
		while (!game.endGame(currentState)) {
			game.printBoard(currentState);
			System.out.println(
					"Please enter your move in the format: " + "<startRow> <startColumn> <endRow> <endColumn>");
			boolean legalInput = false;
			while (legalInput != true) {
				int startRow = sc.nextInt();
				int startCol = sc.nextInt();
				int endRow = sc.nextInt();
				int endCol = sc.nextInt();
				game.setTurn(0);
				Action move = new Action(startRow, startCol, endRow, endCol);
				if (game.getActions(currentState).contains(move)) {
					legalInput = true;
					currentState = new State(game.getResult(currentState, move).getState());
					System.out.println("It's your move:");
					game.printBoard(currentState);
					// AI plays
					System.out.println("It's computer move:");
					game.setTurn(1);
					String[][] temp = new String[8][8];
					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 8; j++) {
							temp[i][j] = currentState.getState()[i][j];
						}
					}
					Action AImove = search.makeDecision(currentState);
					State old_state = new State(temp);
					currentState = new State(game.getResult(old_state, AImove).getState());

				} else {
					System.out.println("Illegal move! Please enter a legal move again");
				}
			}
		}
		System.out.println("Player " + game.getPlayer(game.getstate()).getName() + " loose, nice trying!");

	}

	/**
	 * Print the current board
	 * 
	 * @param state
	 */
	public void printBoard(State state) {
		int i = 0;
		int j = 0;
		System.out.println("  0 1 2 3 4 5 6 7");
		for (String[] s : state.getState()) {
			System.out.print(i + " ");
			i++;
			for (String str : s) {
				System.out.print(str + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * Constructor of CheckersGame
	 */
	public CheckersGame() {
		initialize();
		this.turn = 0;
		this.players = new Player[2];
		players[0] = new Player("b");
		players[1] = new Player("r");
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	/**
	 * Initialize the state
	 */
	public void initialize() {
		String[][] state = { { "r", "_", "r", "_", "r", "_", "r", "_" }, { "_", "r", "_", "r", "_", "r", "_", "r" },
				{ "r", "_", "r", "_", "r", "_", "r", "_" }, { "_", "_", "_", "_", "_", "_", "_", "_" },
				{ "_", "_", "_", "_", "_", "_", "_", "_" }, { "_", "b", "_", "b", "_", "b", "_", "b" },
				{ "b", "_", "b", "_", "b", "_", "b", "_" }, { "_", "b", "_", "b", "_", "b", "_", "b" } };
		this.state = new State(state);

	}

	/**
	 * Get the current state of the game
	 * 
	 * @return S state
	 */
	public S getstate() {
		return (S) this.state;
	}

	/**
	 * Get the initial state of the game
	 */
	@Override
	public S getInitialState() {

		return (S) this.state;
	}

	/**
	 * Get all players of the game
	 */
	@Override
	public P[] getPlayers() {

		return (P[]) this.players;
	}

	/**
	 * Get the current player of the game
	 */
	@Override
	public P getPlayer(S state) {
		if (this.turn == 0)
			return (P) new Player("b");
		else
			return (P) new Player("r");
	}

	/**
	 * Get all the legal moves
	 */
	@Override
	public List<A> getActions(S state) {
		if (turn != 0 && turn != 1)
			return null;

		String player;
		String playerKing;
		if (turn == 0) {
			playerKing = "B";
			player = "b";
		} else {
			playerKing = "R";
			player = "r";
		}
		String[][] currentState = ((State) state).getState();

		ArrayList<Action> moves = new ArrayList<Action>();

		// Check for possible jumps
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if (currentState[row][col] == player || currentState[row][col] == playerKing) {
					if (canJump(state, player, row, col, row + 1, col + 1, row + 2, col + 2)) {
						moves.add(new Action(row, col, row + 2, col + 2));
					}
					if (canJump(state, player, row, col, row - 1, col + 1, row - 2, col + 2)) {
						moves.add(new Action(row, col, row - 2, col + 2));
					}
					if (canJump(state, player, row, col, row + 1, col - 1, row + 2, col - 2)) {
						moves.add(new Action(row, col, row + 2, col - 2));
					}
					if (canJump(state, player, row, col, row - 1, col - 1, row - 2, col - 2)) {
						moves.add(new Action(row, col, row - 2, col - 2));
					}
				}
			}
		}

		// Check for regular moves if no legal jumps were found.
		if (moves.size() == 0) {
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					if (currentState[row][col] == player || currentState[row][col] == playerKing) {
						if (canMove(state, player, row, col, row + 1, col + 1))
							moves.add(new Action(row, col, row + 1, col + 1));
						if (canMove(state, player, row, col, row - 1, col + 1))
							moves.add(new Action(row, col, row - 1, col + 1));
						if (canMove(state, player, row, col, row + 1, col - 1))
							moves.add(new Action(row, col, row + 1, col - 1));
						if (canMove(state, player, row, col, row - 1, col - 1))
							moves.add(new Action(row, col, row - 1, col - 1));
					}
				}
			}
		}

		if (moves.size() == 0)
			return null;

		return (List<A>) moves;
	}

	/**
	 * Helper methods to check whether the player can jump from (r1,c1) to (r3,c3).
	 * The player's piece is at (r1,c1). (r3,c3) is the destination of the jump and
	 * (r2,c2) is the square between (r1,c1) and (r3,c3).
	 */
	private boolean canJump(S state, String player, int r1, int c1, int r2, int c2, int r3, int c3) {

		if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
			return false;

		String[][] currentState = ((State) state).getState();
		if (currentState[r3][c3] != "_")
			return false;

		if (player == "b") {
			if (currentState[r1][c1] == "b" && r3 > r1)
				return false;
			if (currentState[r2][c2] != "r" && currentState[r2][c2] != "R")
				return false;
			return true;
		} else {
			if (currentState[r1][c1] == "r" && r3 < r1)
				return false;
			if (currentState[r2][c2] != "b" && currentState[r2][c2] != "B")
				return false;
			return true;
		}

	}

	/**
	 * Helper method to determine whether the player can move from (r1,c1) to
	 * (r2,c2).
	 */
	private boolean canMove(S state, String player, int r1, int c1, int r2, int c2) {
		String[][] currentState = ((State) state).getState();
		if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
			return false;

		if (currentState[r2][c2] != "_")
			return false;

		if (player == "b") {
			if (currentState[r1][c1] == "b" && r2 > r1)
				return false;
			return true;
		} else {
			if (currentState[r1][c1] == "r" && r2 < r1)
				return false;
			return true;
		}

	}

	/**
	 * Returns a resulting state from a move
	 */
	@Override
	public S getResult(S state, A action) {
		String[][] currentState = ((State) state).getState();
		Action transition_model = (Action) action;
		int startRow = ((Action) action).getPrevRow();
		int startCol = ((Action) action).getPrevCol();
		int endRow = ((Action) action).getNewRow();
		int endCol = ((Action) action).getNewCol();
		String player = currentState[startRow][startCol];
		currentState[transition_model.getPrevRow()][transition_model.getPrevCol()] = "_";
		// remove the piece that is jumped over in the move
		if (isJump(startRow, endRow)) {
			// jump to upper left
			if (endRow - startRow < 0 && endCol - startCol < 0)
				currentState[startRow - 1][startCol - 1] = "_";
			// jump to upper right
			else if (endRow - startRow < 0 && endCol - startCol > 0)
				currentState[startRow - 1][startCol + 1] = "_";
			// jump to lower left
			else if (endRow - startRow > 0 && endCol - startCol < 0)
				currentState[startRow + 1][startCol - 1] = "_";
			// jump to lower right
			else
				currentState[startRow + 1][startCol + 1] = "_";
		}
		if (player.contains("b") && endRow == 0)
			currentState[endRow][endCol] = "B";
		else if (player.contains("r") && endRow == 7)
			currentState[endRow][endCol] = "R";
		else
			currentState[endRow][endCol] = player;

		return (S) new State(currentState);
	}

	/**
	 * check if a move is a jump
	 */
	boolean isJump(int startRow, int endRow) {
		// The piece moves two rows in a jump
		return (startRow - endRow == 2 || startRow - endRow == -2);
	}

	/**
	 * Check if it is a terminal state. Terminal state when there is no legal
	 * actions for the player during player turn.
	 */
	@Override
	public boolean isTerminal(S state) {
		if (getActions(state) == null)
			return true;
		return false;
	}

	/**
	 * Check if there is a winner of the game
	 * 
	 * @param state
	 * @return boolean value
	 */
	public boolean endGame(S state) {
		return isTerminal(state);
	}

	/**
	 * Utility function
	 */
	@Override
	public double getUtility(S state, P player) {
		String p = ((Player) player).getName();
		if (isTerminal(state)) {
			if (((State) state).getNumberOfPieces(p) == 0) {
				return -1000;
			} else if (getActions(state) == null) {
				return -1000;
			} else
				return 1000;
		}
		//here we can change the evaluation functions
		return eval_improve(state, player);
	}

	/**
	 * First evaluation function
	 */
	public double eval_initial(S state, P player) {
		int remains = 0;

		int num_black = ((State) state).getNumberOfPieces("b");
		int num_red = ((State) state).getNumberOfPieces("r");
		if (((Player) getPlayer(state)).getName() == "b")
			remains = num_black - num_red;
		else
			remains = num_red - num_black;

		return remains;

	}

	/**
	 * Improved evaluation function
	 */
	public double eval_improve(S state, P player) {
		int remains = 0;

		int num_black = ((State) state).getNumberOfPieces("b");
		int num_red = ((State) state).getNumberOfPieces("r");
		int num_blackKing = ((State) state).getNumberOfPieces("B");
		int num_redKing = ((State) state).getNumberOfPieces("R");
		if (((Player) getPlayer(state)).getName() == "b")
			remains = (num_black - num_blackKing + num_blackKing * 10) - (num_red - num_redKing + num_redKing * 10);
		else
			remains = (num_red - num_redKing + num_redKing * 10) - (num_black - num_blackKing + num_blackKing * 10);
		return remains;

	}

}

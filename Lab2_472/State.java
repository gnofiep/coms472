package Lab2;

import java.util.Arrays;

public class State {

	private String[][] state;

	public State() {

	}

	public State(String[][] state) {
		this.state = state;
	}

	public String[][] getState() {
		return this.state;
	}

	public int getNumberOfPieces(String color) {
		int count = 0;
		for (String[] s : state) {
			for (String str : s)
				if (str.contains(color))
					count++;
		}
		return count;
	}
}

package Lab2;

public class Action {

	int fromRow, fromCol; // Position of piece to be moved.
	int toRow, toCol; // Position of piece need to move to.

	public Action(int r1, int c1, int r2, int c2) {
		// Constructor. Just set the values of the instance variables.
		fromRow = r1;
		fromCol = c1;
		toRow = r2;
		toCol = c2;
	}

	public int getPrevCol() {
		return this.fromCol;
	}

	public int getPrevRow() {
		return this.fromRow;
	}

	public int getNewCol() {
		return this.toCol;
	}

	public int getNewRow() {
		return this.toRow;
	}

	@Override
	public boolean equals(Object obj) {

		return (this.fromRow == ((Action) obj).fromRow) && this.fromCol == (((Action) obj).fromCol)
				&& this.toRow == (((Action) obj).toRow) && this.toCol == (((Action) obj).toCol);

	}

}

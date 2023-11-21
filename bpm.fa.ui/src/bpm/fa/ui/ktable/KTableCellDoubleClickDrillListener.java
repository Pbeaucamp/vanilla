package bpm.fa.ui.ktable;

public interface KTableCellDoubleClickDrillListener{

	/**
	 * Implements the drill behavior when double clicking on the ItemElement.
	 */
	public void fixedCellDoubleClicked(int col, int row, int statemask);
}

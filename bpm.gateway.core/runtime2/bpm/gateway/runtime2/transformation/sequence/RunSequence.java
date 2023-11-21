package bpm.gateway.runtime2.transformation.sequence;

import bpm.gateway.core.transformations.Sequence;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.gateway.runtime2.tools.IdGenerator;

public class RunSequence extends RuntimeStep {

	private boolean isFromValue = false;

	// private long min = 0;
	private Long min = null;
	private int minFieldIndex = -1;
	private long max = 0;
	private long offset = 0;
	private Long currentValue;

	public RunSequence(Sequence transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		Sequence seq = (Sequence) getTransformation();

		if (seq.isFromValue()) {
			min = seq.getMinValue();
			this.isFromValue = true;
		}
		else {
			try {
				minFieldIndex = seq.getMinField();
				if (minFieldIndex < 0) {
					throw new Exception("");
				}
			} catch (Exception ex) {
				String message = " Sequence need a min field define.";
				error(message);
				throw new Exception(message);
			}
		}

		max = seq.getMaxValue();
		offset = seq.getStep();

		info(" inited");
	}

	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()) {
			if (inputEmpty()) {
				setEnd();
			}
		}

		if (isEnd() && inputEmpty()) {
			return;
		}

		if (!isEnd() && inputEmpty()) {
			try {
				Thread.sleep(10);
				return;
			} catch (Exception e) {

			}
		}

		Row row = readRow();

		Row newRow = RowFactory.createRow(this, row);

		if (!isFromValue) {
			if (min == null) {
				this.min = setMinValue(row);
			}
		}

		if (currentValue == null) {
			currentValue = min;
		}
		else {
			try {
				currentValue = IdGenerator.generateId(min, max, offset, currentValue);
			} catch (Exception e) {
				error(" cannot perform " + currentValue + "+" + offset, e);
				throw e;
			}
		}

		newRow.set(newRow.getMeta().getSize() - 1, currentValue);
		// currentValue += offset;
		writeRow(newRow);

	}

	private Long setMinValue(Row row) throws Exception {
		try {
			return Long.parseLong(row.get(minFieldIndex).toString());
		} catch (Exception e) {
			e.printStackTrace();
			Object value = null;
			try {
				value = row.get(minFieldIndex);
			} catch (Exception e1) {
				e1.printStackTrace();
				value = "Unknown";
			}
			throw new Exception("Value " + value + " cannot be cast to long to be use as minimum value for the sequence.");
		}
	}

	@Override
	public void releaseResources() {
		info(" released");

	}

}

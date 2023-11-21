package bpm.gateway.runtime2.transformation.aggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import bpm.gateway.core.transformations.AggregateTransformation;
import bpm.gateway.core.transformations.utils.Aggregate;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunAggragtion extends RuntimeStep {
	private List<Aggregate> aggs;
	private List<Integer> keyIndice;
	private boolean nullModeZero = false;

	/*
	 * Key : groupBy Key
	 */
	private HashMap<List<Object>, Row> outDatas;

	private HashMap<Aggregate, HashMap<List<Object>, List<Object>>> distinctValuesByKeys;

	/*
	 * for average, store the number of element for each row
	 */
	private HashMap<Row, Integer> averageCount;
	private boolean needAverage = false;

	private Iterator<List<Object>> outIterator;

	public RunAggragtion(AggregateTransformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);

	}

	@Override
	public void init(Object adapter) throws Exception {
		AggregateTransformation ag = (AggregateTransformation) getTransformation();
		aggs = ag.getAggregates();

		outDatas = new HashMap<List<Object>, Row>();
		averageCount = new HashMap<Row, Integer>();
		keyIndice = ag.getGroupBy();
		distinctValuesByKeys = new HashMap<Aggregate, HashMap<List<Object>, List<Object>>>();
		for (Aggregate a : aggs) {
			if (a.getFunction() == Aggregate.AVERAGE) {
				needAverage = true;

			}
			distinctValuesByKeys.put(a, new HashMap<List<Object>, List<Object>>());

		}
		nullModeZero = ag.getNullMode() == AggregateTransformation.MODE_ZERO;
		isInited = true;
	}

	@Override
	public void performRow() throws Exception {
		if (!areInputStepAllProcessed()) {
			Thread.sleep(1000);
			return;
		}

		if (outIterator == null) {
			outIterator = outDatas.keySet().iterator();
		}

		if (outIterator.hasNext()) {
			Row row = outDatas.get(outIterator.next());
			writeRow(row);
		}
		else {
			setEnd();
		}

	}

	@Override
	protected void writeRow(Row row) throws InterruptedException {
		if (needAverage) {
			int pos = keyIndice.size();
			for (Aggregate a : aggs) {

				if (a.getFunction() == Aggregate.AVERAGE) {
					row.set(pos, ((Number) row.get(pos)).doubleValue() / averageCount.get(row));
				}
				pos++;
			}
		}
		super.writeRow(row);
	}

	@Override
	public void releaseResources() {
		try {
			aggs.clear();
			aggs = null;
			averageCount.clear();
			averageCount = null;
			distinctValuesByKeys.clear();
			distinctValuesByKeys = null;
			keyIndice.clear();
			keyIndice = null;
			outDatas.clear();
			outDatas = null;
			outIterator = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		info("Resources released");

	}

	/**
	 * if the Key is stored in the outDatas, return the stored key if the key is
	 * new, we create a new row for this key and store it in the outputDatas(agg
	 * values are inited at 0)
	 * 
	 * @param row
	 * @return the GroupBy element from an inputRow
	 */
	private List<Object> extractKey(Row row) throws Exception {
		List<Object> key = new ArrayList<Object>(keyIndice.size());
		for (Integer i : keyIndice) {
			key.add(row.get(i));
		}

		for (List<Object> k : outDatas.keySet()) {
			boolean match = true;
			for (int i = 0; i < k.size(); i++) {

				if ((k.get(i) == null && key.get(i) != null) || (k.get(i) != null && key.get(i) == null) || (!k.get(i).toString().equals(key.get(i).toString()))) {
					match = false;
					break;
				}
			}
			if (match) {
				return k;
			}
		}

		Row newrow = RowFactory.createRow(this);
		int k = 0;
		for (Integer i : keyIndice) {
			newrow.set(k++, row.get(i));
		}

		for (Aggregate a : aggs) {

			Integer index = ((AggregateTransformation) getTransformation()).getStreamElementPositionByName(a.getStreamElementName());

			if (a.getFunction() == Aggregate.MAXIMUM || a.getFunction() == Aggregate.MINIMUM) {
				newrow.set(k++, null);
			}
			else {
				newrow.set(k++, 0);
			}

		}

		outDatas.put(key, newrow);

		return key;
	}

	/**
	 * 
	 * @param row
	 * @param agg
	 * @return the Value to aggregate for the given aggregate function
	 */
	private Object extractValue(Row row, Aggregate agg) {
		return row.get(((AggregateTransformation) getTransformation()).getStreamElementPositionByName(agg.getStreamElementName()));
	}

	/**
	 * return true if for Aggregate a, the given their is alreay the given data
	 * 
	 * @param a
	 * @param key
	 * @return
	 */
	private boolean distinctValuesContains(Aggregate a, List<Object> key, Object data) {

		List<Object> l = distinctValuesByKeys.get(a).get(key);

		if (l == null) {
			l = new ArrayList<Object>();
			l.add(data);
			distinctValuesByKeys.get(a).put(key, l);
		}
		else {
			for (Object o : l) {
				if ((o == null && data == null) || (o != null && o.equals(data)) || (data != null && data.equals(o))) {
					return true;
				}
			}
			distinctValuesByKeys.get(a).get(key).add(data);
		}
		return false;
	}

	private void performAggregation(Row row, Aggregate agg, int aggPos) throws Exception {
		List<Object> key = extractKey(row);
		Object value = extractValue(row, agg);

		Row currentRow = outDatas.get(key);
		Number currentVal = (Number) currentRow.get(aggPos);

		switch (agg.getFunction()) {
		case Aggregate.COUNT:
			currentRow.set(aggPos, currentVal.intValue() + 1);
			break;
		case Aggregate.DISTINCT_COUNT:
			if (!distinctValuesContains(agg, key, value)) {
				currentRow.set(aggPos, currentVal.intValue() + 1);

			}
			break;
		case Aggregate.MAXIMUM:
			if (value != null) {
				if (currentVal == null) {
					currentRow.set(aggPos, getDoubleValue(value));
				}
				else {
					currentRow.set(aggPos, Math.max(currentVal.doubleValue(), getDoubleValue(value)));
				}

			}

			break;
		case Aggregate.MINIMUM:
			if (value != null) {
				if (currentVal == null) {
					currentRow.set(aggPos, getDoubleValue(value));
				}
				else {
					currentRow.set(aggPos, Math.min(currentVal.doubleValue(), getDoubleValue(value)));

				}

			}

			break;
		case Aggregate.AVERAGE:
			Integer i = averageCount.get(currentRow);
			if (i == null) {
				if (nullModeZero) {
					averageCount.put(currentRow, 1);
				}

			}
			else {
				averageCount.put(currentRow, i + 1);
			}
		case Aggregate.SUM:
			if (value != null) {
				currentRow.set(aggPos, currentVal.doubleValue() + getDoubleValue(value));
			}
		}
	}

	private double getDoubleValue(Object value) {
		try {
			return ((Number) value).doubleValue();
		} catch (Exception e) {
			return Double.parseDouble(value.toString());
		}
	}

	@Override
	public void insertRow(Row data, RuntimeStep caller) throws InterruptedException {

		int pos = keyIndice.size();
		for (Aggregate a : aggs) {
			try {
				Integer index = ((AggregateTransformation) getTransformation()).getStreamElementPositionByName(a.getStreamElementName());

				performAggregation(data, a, pos++);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		readedRows++;
	}

}

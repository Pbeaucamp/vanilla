package bpm.vanilla.api.runtime.dto;

import bpm.fa.api.item.ItemValue;

public class CubeValueCell extends CubeCell {
	private float value;

	public CubeValueCell(ItemValue itVal) {
		super(itVal, itVal.getDrillThroughSql(), "ItemValue");
		value = Float.parseFloat(itVal.getValue());
		if ((Float.isNaN(value)) || (value < 0.01)) {
			value = 0;
		}
	}

	public float getValue() {
		return value;
	}

}

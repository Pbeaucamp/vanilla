package bpm.united.olap.runtime.formatter;

import java.text.NumberFormat;

/**
 * Format a number as a percent
 * @author Marc Lanquetin
 *
 */
public class PercentFormatter implements IFormatter {

	@Override
	public String formatValue(Double value) {

		return NumberFormat.getPercentInstance().format(value);
	}

}

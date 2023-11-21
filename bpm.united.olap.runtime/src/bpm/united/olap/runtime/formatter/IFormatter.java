package bpm.united.olap.runtime.formatter;

/**
 * 
 * @author Marc Lanquetin
 *
 */
public interface IFormatter {

	public static final String PERCENT = "Percent";
	
	/**
	 * Format the value
	 * @param value
	 * @return
	 */
	public String formatValue(Double value);
	
}

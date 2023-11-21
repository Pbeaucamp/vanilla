package bpm.united.olap.runtime.formatter;

/**
 * An helper class to find the right formatter
 * @author Marc Lanquetin
 *
 */
public class FormatterFactory {

	/**
	 * Return the formatter instance for the format parameter
	 * @param format
	 * @return
	 */
	public static IFormatter getFormatter(String format) {
		
		if(format.equalsIgnoreCase(IFormatter.PERCENT)) {
			return new PercentFormatter();
		}
		
		return null;
	}
	
}

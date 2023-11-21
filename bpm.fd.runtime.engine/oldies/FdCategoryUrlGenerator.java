package bpm.fd.runtime.engine.chart.jfree.generator;

import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.chart.urls.URLUtilities;
import org.jfree.data.category.CategoryDataset;

public class FdCategoryUrlGenerator extends StandardCategoryURLGenerator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8033046058147774428L;
	private String url;
	private String parameterName;
	private boolean useValue;
	
	/**
	 * @param prefix
	 * @param seriesParameterName
	 * @param categoryParameterName
	 */
	public FdCategoryUrlGenerator(String prefix, String parameterName, boolean useValue) {
		super(prefix, parameterName == null ? "" : parameterName, "");
		this.url = prefix;
		this.parameterName = parameterName;
		if (parameterName == null){
			this.parameterName = "";
		}
		
		this.useValue = useValue;

	}
	
	 public String generateURL(CategoryDataset dataset, int series, int category) {
		String url = this.url;
		Comparable seriesKey = dataset.getRowKey(series);
		Comparable categoryKey = dataset.getColumnKey(category);
		
		boolean firstParameter = url.indexOf("?") == -1;
		url += firstParameter ? "?" : "&amp;";
		if (useValue){
			url += this.parameterName + "=" + URLUtilities.encode(dataset.getValue(series, category).toString(), "UTF-8");
		}
		else{
			url += this.parameterName + "=" + URLUtilities.encode(seriesKey.toString(), "UTF-8");
		}
		return url;
	 }

}

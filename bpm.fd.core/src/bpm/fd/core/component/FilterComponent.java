package bpm.fd.core.component;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentData;
import bpm.fd.core.IComponentOption;
import bpm.vanilla.platform.core.beans.data.Dataset;

public class FilterComponent extends DashboardComponent implements IComponentData, IComponentOption {

	private static final long serialVersionUID = 1L;

	private Dataset dataset;

	private Integer columnLabelIndex;
	private Integer columnValueIndex;
	private Integer columnOrderIndex;

	private FilterRenderer renderer;

	private boolean submitOnChange = true;
	private boolean selectFirstValue = false;
	private boolean initParameterWithFirstValue = true;
	private boolean hidden = false;
	private boolean required = false;
	private String defaultValue;

	// dropdown and menu only
	private int size = 1;

	// menu only
	private boolean isVertical = false;

	// multiple values only
	private boolean multipleValues = false;

	// slider only
	private int delay = 5000;
	private boolean autoRun = false;
	private String barColor = "silver";
	private String sliderColor = "#666666";

	@Override
	public Dataset getDataset() {
		return dataset;
	}

	@Override
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public Integer getColumnLabelIndex() {
		return columnLabelIndex;
	}

	public void setColumnLabelIndex(Integer columnLabelIndex) {
		this.columnLabelIndex = columnLabelIndex;
	}

	public Integer getColumnValueIndex() {
		return columnValueIndex;
	}

	public void setColumnValueIndex(Integer columnValueIndex) {
		this.columnValueIndex = columnValueIndex;
	}

	public Integer getColumnOrderIndex() {
		return columnOrderIndex;
	}

	public void setColumnOrderIndex(Integer columnOrderIndex) {
		this.columnOrderIndex = columnOrderIndex;
	}

	public FilterRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(FilterRenderer renderer) {
		this.renderer = renderer;
	}

	public boolean isSubmitOnChange() {
		return submitOnChange;
	}

	public void setSubmitOnChange(boolean submitOnChange) {
		this.submitOnChange = submitOnChange;
	}

	public boolean isSelectFirstValue() {
		return selectFirstValue;
	}

	public void setSelectFirstValue(boolean selectFirstValue) {
		this.selectFirstValue = selectFirstValue;
	}

	public boolean isInitParameterWithFirstValue() {
		return initParameterWithFirstValue;
	}

	public void setInitParameterWithFirstValue(boolean initParameterWithFirstValue) {
		this.initParameterWithFirstValue = initParameterWithFirstValue;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isVertical() {
		return isVertical;
	}

	public void setVertical(boolean isVertical) {
		this.isVertical = isVertical;
	}

	public boolean isMultipleValues() {
		return multipleValues;
	}

	public void setMultipleValues(boolean multipleValues) {
		this.multipleValues = multipleValues;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public boolean isAutoRun() {
		return autoRun;
	}

	public void setAutoRun(boolean autoRun) {
		this.autoRun = autoRun;
	}

	public String getBarColor() {
		return barColor;
	}

	public void setBarColor(String barColor) {
		this.barColor = barColor;
	}

	public String getSliderColor() {
		return sliderColor;
	}

	public void setSliderColor(String sliderColor) {
		this.sliderColor = sliderColor;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.FILTER;
	}

	@Override
	protected void clearData() {
		this.dataset = null;

		this.columnLabelIndex = null;
		this.columnValueIndex = null;
		this.columnOrderIndex = null;
	}

}

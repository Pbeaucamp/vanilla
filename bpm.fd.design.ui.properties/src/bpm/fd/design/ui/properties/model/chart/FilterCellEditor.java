package bpm.fd.design.ui.properties.model.chart;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;

public class FilterCellEditor extends DialogCellEditor{

	public static class FilterCellEditorData{
		private DataAggregation agg;
		private IChartData chartDatas;
		public FilterCellEditorData(DataAggregation agg, IChartData chartDatas) {
			super();
			this.agg = agg;
			this.chartDatas = chartDatas;
		}
		/**
		 * @return the agg
		 */
		public DataAggregation getAgg() {
			return agg;
		}
		/**
		 * @return the chartDatas
		 */
		public IChartData getChartDatas() {
			return chartDatas;
		}
		
	}
	private FilterCellEditorData datas;
	
	public FilterCellEditor(Composite parent){
		super(parent, SWT.READ_ONLY);
	}
	
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		DialogSerieDataFilters dial = new DialogSerieDataFilters(
				cellEditorWindow.getShell(),
				datas.getAgg(), datas.getChartDatas()
				);
		dial.open();
		return null;
	}

	@Override
	protected void doSetValue(Object value) {
		datas = (FilterCellEditorData)value;
		super.doSetValue(value);
	}
}

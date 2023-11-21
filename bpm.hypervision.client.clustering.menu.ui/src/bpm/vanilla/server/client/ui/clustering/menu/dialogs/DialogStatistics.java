package bpm.vanilla.server.client.ui.clustering.menu.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import bpm.vanilla.platform.core.beans.UOlapQueryBean;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.uolap.viewers.QueryLogsLabelProvider;

public class DialogStatistics extends Dialog {

	private List<UOlapQueryBean> data;
	
	public DialogStatistics(Shell parentShell, List<UOlapQueryBean> data) {
		super(parentShell);
		this.data = data;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
	}
	
	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
	    if (id == IDialogConstants.CANCEL_ID) return null;
	    if (id == IDialogConstants.OK_ID) {
	    	label = Messages.DialogStatistics_0;
	    }
	    return super.createButton(parent, id, label, defaultButton);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(1, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		ChartComposite cChart = new ChartComposite(main, SWT.NONE);
		cChart.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		cChart.setBackground(main.getBackground());
		
		drawChart(cChart, data);
		
		return main;
	}

	private void drawChart(ChartComposite cChart, List<UOlapQueryBean> data) {

		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

		for (UOlapQueryBean bean : data) {
			dataSet.setValue(bean.getExecutionTime() / 1000., QueryLogsLabelProvider.getGroupName(bean.getVanillaGroupId()), bean.getExecutionDate());
		}

		JFreeChart chart = ChartFactory.createLineChart(Messages.ManagementView_44, Messages.ManagementView_45, Messages.ManagementView_46, dataSet, PlotOrientation.VERTICAL, true, false, false);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryAxis xAxis = (CategoryAxis) plot.getDomainAxis();
		xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		plot.setBackgroundPaint(ChartColor.WHITE);
		chart.setBackgroundPaint(ChartColor.WHITE);
		cChart.setChart(chart);
		chart.fireChartChanged();
	}
}


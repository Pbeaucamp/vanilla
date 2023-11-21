package bpm.es.sessionmanager.views.composite;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.jfree.experimental.chart.swt.ChartComposite;

import bpm.es.sessionmanager.api.SessionManager;


public abstract class AChartComposite extends Composite{
	
	protected Composite generalComposite;
	protected ChartComposite compositeChart;
	protected ComboViewer comboObject;
	
	//given fields
	private String title;
	private String selectionLabel;
	private FormToolkit toolkit;
	
	protected AChartComposite(Composite parent, int style, String title, String selectionLabel, 
			FormToolkit toolkit) {
		super(parent, style);
		
		this.title = title;
		this.selectionLabel = selectionLabel;
		this.toolkit = toolkit;
		
		createComposite();
		initProviders();
	}
	
	private void createComposite() {
		Section actionSection = toolkit.createSection(this, Section.TITLE_BAR | Section.EXPANDED);
		actionSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		actionSection.setLayout(new GridLayout());
		actionSection.setText(title);
		
		generalComposite = toolkit.createComposite(actionSection);
		generalComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		generalComposite.setLayout(new GridLayout());
		
		actionSection.setClient(generalComposite);
		
		Composite buttonBar = toolkit.createComposite(generalComposite);
		//Composite buttonBar = new Composite(chartObject, SWT.NONE);
		buttonBar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		buttonBar.setLayout(new GridLayout(2, false));
		
		Label l1 = new Label(buttonBar, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(selectionLabel);
		
		comboObject = new ComboViewer(buttonBar, SWT.NONE);
		comboObject.getCombo().setLayoutData(new GridData());
		
		compositeChart = new ChartComposite(generalComposite, SWT.NONE);
		compositeChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		//compositeChart.set
	}
	
	/**
	 * Yours to implement how to setup content and label providers 
	 */
	protected abstract void initProviders();
	
//	/**
//	 * Yours to implement to create and display chart
//	 */
//	protected abstract void showChart(int objectId);

	/**
	 * set data input so we can draw a chart, will provoke the drawing
	 * @param manager
	 * @param userId
	 */
	public abstract void setInput(SessionManager manager, int userId);
}

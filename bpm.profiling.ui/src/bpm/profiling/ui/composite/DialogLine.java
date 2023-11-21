package bpm.profiling.ui.composite;

import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jfree.data.category.DefaultCategoryDataset;

import bpm.profiling.runtime.core.Result;

public class DialogLine extends Dialog {
	private CompositeLineChart chart;
	private DefaultCategoryDataset dataset;
	private Composite parent;
	
	public DialogLine(Shell parentShell, List<Result> results) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.TITLE | SWT.CLOSE);
		
		if (results != null){
			dataset = new DefaultCategoryDataset();
			
			String[] series = new String[results.get(0).getLabels().size()];
			
			for(int i =0 ; i< results.get(0).getLabels().size(); i++){
				series[i] = results.get(0).getLabels().get(i);
			}
			
			String[] colKeys = new String[results.size()];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			
			int i = 0;
			for(Result result : results){
				colKeys[i++] = sdf.format(result.getDate());
				
			}
			
			int k = 0;
			for(String s : series){
				int j = 0;
				for(Result r : results){
					if (r.getValues().get(k) instanceof Long){
						dataset.addValue((Long)r.getValues().get(k), s, colKeys[j]);
					}
					else{
						dataset.addValue((Float)r.getValues().get(k), s, colKeys[j]);
					}
					j++;
				}
				k++;
			}
				
		}

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		this.parent = parent;
		chart = new CompositeLineChart(parent, dataset,"Evolution");
		chart.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return chart;
	}

	@Override
	protected void initializeBounds() {
		
		getShell().setSize(600, 400);
		chart.drawChart(parent.getBounds());
		
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	
}

package bpm.fd.design.ui.component.chart.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.PieGenericOptions;
import bpm.fd.design.ui.component.ColorTools;
import bpm.fd.design.ui.component.Messages;



public class PieChartGenericOptionPage extends ChartGenericOptionsPage{

	private Text pieSliceDepth, slicingDistance;
	private ModifyListener listener = new ModifyListener(){

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		public void modifyText(ModifyEvent e) {
			Text t = (Text)e.widget;
			
			try{
				Integer.parseInt(t.getText());
				t.setBackground(null);
				setErrorMessage(null);
				
			}catch(NumberFormatException ex){
				t.setBackground(ColorTools.getColor(ColorTools.COLOR_ERROR));
				setErrorMessage(Messages.PieChartGenericOptionPage_0);
			}
			
			getContainer().updateButtons();
			
		}
		
		
	};
	
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return super.isPageComplete() && pieSliceDepth.getBackground() != ColorTools.getColor(ColorTools.COLOR_ERROR) && slicingDistance.getBackground() != ColorTools.getColor(ColorTools.COLOR_ERROR);
	}

	@Override
	protected Composite createContent(Composite parent){
		Composite main = super.createContent(parent);
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.PieChartGenericOptionPage_1);
		
		pieSliceDepth = new Text(main, SWT.BORDER);
		pieSliceDepth.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		pieSliceDepth.addModifyListener(listener);
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.PieChartGenericOptionPage_2);
		
		slicingDistance = new Text(main, SWT.BORDER);
		slicingDistance.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		slicingDistance.addModifyListener(listener);
		
		return main;
	}
	
	public IComponentOptions getOptions(){
		IComponentOptions opt = super.getOptions();
		try{
			PieGenericOptions  _opt = null;
			if (getControl() == null || getControl().isDisposed()){
				 _opt = (PieGenericOptions)opt.getAdapter(ChartNature.getNature(ChartNature.PIE));
				
			}
			else{
				_opt = (PieGenericOptions)opt.getAdapter(ChartNature.getNature(ChartNature.PIE));
				try{
					_opt.setPieSliceDepth(Integer.parseInt(pieSliceDepth.getText()));
					_opt.setSlicingDistance(Integer.parseInt(slicingDistance.getText()));
				}catch(NumberFormatException e){
					
				}
				
			}
			
			
			return _opt;
		}catch(Exception e){
			e.printStackTrace();
		}
		return opt;
	}
	
	@Override
	public void fill(){
		super.fill();
		caption.setText(((GenericOptions)options).getCaption());
		subCaption.setText(((GenericOptions)options).getSubCaption());
		showLabels.setSelection(((GenericOptions)options).isShowLabel());
		showValues.setSelection(((GenericOptions)options).isShowValues());
		
		if (options instanceof PieGenericOptions){
			slicingDistance.setText(((PieGenericOptions)options).getSlicingDistance() + ""); //$NON-NLS-1$
			pieSliceDepth.setText(((PieGenericOptions)options).getPieSliceDepth() + ""); //$NON-NLS-1$
		}
		
	}
}

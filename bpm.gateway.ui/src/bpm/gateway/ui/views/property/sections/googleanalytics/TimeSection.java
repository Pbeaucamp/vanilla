package bpm.gateway.ui.views.property.sections.googleanalytics;

import java.util.Calendar;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.googleanalytics.VanillaAnalyticsGoogle;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class TimeSection extends AbstractPropertySection {

	private VanillaAnalyticsGoogle model;
	private String beginDate;
	private String endDate;
	
	private Text txtBegin, txtEnd;
	private Button button;
	
	public TimeSection() { }
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		final Shell shell = parent.getShell();

		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		
		Label lblInfos = getWidgetFactory().createLabel(composite, Messages.TimeSection_0);
		lblInfos.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));
		
		Label lblBegin = getWidgetFactory().createLabel(composite, Messages.TimeSection_1);
		lblBegin.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		txtBegin = getWidgetFactory().createText(composite, beginDate);
		txtBegin.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		txtBegin.addSelectionListener(new DateSelectionAdapter(shell, txtBegin, false));
		
		Button begin = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		begin.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		begin.addSelectionListener (new DateSelectionAdapter(shell, txtBegin, true));
		
		
		Label lblEnd = getWidgetFactory().createLabel(composite, Messages.TimeSection_3);
		lblEnd.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		txtEnd = getWidgetFactory().createText(composite, endDate, SWT.BORDER);
		txtEnd.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		txtEnd.addSelectionListener(new DateSelectionAdapter(shell, txtEnd, false));
		
		Button end = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		end.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		end.addSelectionListener (new DateSelectionAdapter(shell, txtEnd, false));
		
		Label lblGroupDate = getWidgetFactory().createLabel(composite, Messages.TimeSection_5);
		lblGroupDate.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		button = getWidgetFactory().createButton(composite, "", SWT.CHECK); //$NON-NLS-1$
		button.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		button.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent theEvent) {
				model.setGroupByDate(((Button)(theEvent.widget)).getSelection());
	        }
	    });
	}
	
	@Override
	public void refresh() {
		String dB = model.getBeginDate();
		String dE = model.getEndDate();
		
		if(dB != null && !dB.isEmpty()){
			this.beginDate = dB;
			txtBegin.setText(beginDate);
		}
		else {
			Calendar cal = Calendar.getInstance();
			String year = cal.get(Calendar.YEAR) + ""; //$NON-NLS-1$
			String month = cal.get(Calendar.MONTH)+1 < 10 ? "0" + (cal.get(Calendar.MONTH)+1) : cal.get(Calendar.MONTH)+1 + ""; //$NON-NLS-1$ //$NON-NLS-2$
			String day = cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH) + ""; //$NON-NLS-1$ //$NON-NLS-2$
			this.beginDate = year + "-" + month + "-" + day; //$NON-NLS-1$ //$NON-NLS-2$
			model.setBeginDate(beginDate);
			txtBegin.setText(beginDate);
		}
		
		if(dE != null && !dE.isEmpty()){
			this.endDate = dE;
			txtEnd.setText(endDate);
		}
		else {
			Calendar cal = Calendar.getInstance();
			String year = cal.get(Calendar.YEAR) + ""; //$NON-NLS-1$
			String month = cal.get(Calendar.MONTH)+1 < 10 ? "0" + (cal.get(Calendar.MONTH)+1) : cal.get(Calendar.MONTH)+1 + ""; //$NON-NLS-1$ //$NON-NLS-2$
			String day = cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH) + ""; //$NON-NLS-1$ //$NON-NLS-2$
			this.endDate = year + "-" + month + "-" + day; //$NON-NLS-1$ //$NON-NLS-2$
			model.setEndDate(endDate);
			txtEnd.setText(endDate);
		}
		
		button.setSelection(model.isGroupByDate());
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.model = (VanillaAnalyticsGoogle)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	private class DateSelectionAdapter extends SelectionAdapter {
		
		private Shell shell;
		private Text txtDate;
		private boolean isDateBegin;
		
		public DateSelectionAdapter(Shell shell, Text txtDate, boolean isDateBegin) {
			this.shell = shell;
			this.txtDate = txtDate;
			this.isDateBegin = isDateBegin;
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			final Shell dialog = new Shell (shell, SWT.DIALOG_TRIM);
			dialog.setLayout (new GridLayout ());
			dialog.setSize(250, 220);

			final DateTime calendar = new DateTime (dialog, SWT.CALENDAR | SWT.BORDER);

			Button ok = new Button (dialog, SWT.PUSH);
			ok.setText ("OK"); //$NON-NLS-1$
			ok.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, false, false));
			ok.addSelectionListener (new SelectionAdapter () {
				public void widgetSelected (SelectionEvent e) {
					Calendar cal = Calendar.getInstance();
					cal.set(calendar.getYear (), calendar.getMonth (), calendar.getDay ());
					String year = cal.get(Calendar.YEAR) + ""; //$NON-NLS-1$
					String month = cal.get(Calendar.MONTH)+1 < 10 ? "0" + (cal.get(Calendar.MONTH)+1) : cal.get(Calendar.MONTH)+1 + ""; //$NON-NLS-1$ //$NON-NLS-2$
					String day = cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH) + ""; //$NON-NLS-1$ //$NON-NLS-2$
					String date = year + "-" + month + "-" + day; //$NON-NLS-1$ //$NON-NLS-2$
					if(isDateBegin){
						beginDate = date;
						model.setBeginDate(beginDate);
					}
					else {
						endDate = date;
						model.setEndDate(endDate);
					}
					txtDate.setText(date);
					dialog.close ();
				}
			});
			dialog.setDefaultButton (ok);
			dialog.open ();
		}
	}
}

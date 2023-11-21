package bpm.workflow.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jfree.data.general.DefaultPieDataset;

import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.communication.LoggerBIW;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.composites.CompositeChartMetrics;
import bpm.workflow.ui.icons.IconsNames;
import bpm.workflow.ui.preferences.PreferencesConstants;

/**
 * Dialog for the visualization of the metrics relative to the execution of a process (the current imported one)
 * 
 * @author Charles MARTIN
 * 
 */
public class DialogMetrics extends Dialog {

	RepositoryItem idCurrent;
	private CompositeChartMetrics compositemetrics, compositemetricsProcess;

	private Combo datesCombo;
	Label timeperact, timeperproc, timeprocesslab, timeactivitylab;
	private org.eclipse.swt.widgets.List timeperActList;
	private HashMap<String, List<String>> list = new HashMap<String, List<String>>();
	private List<String> listDates = new ArrayList<String>();

	public DialogMetrics(Shell parentShell, RepositoryItem _idCurrent) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.idCurrent = _idCurrent;

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Button lab = new Button(main, SWT.PUSH);
		lab.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.CHECKMETRICS_32));
		lab.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		lab.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				String vanillaUrl = store.getString(PreferencesConstants.P_URLVANILLA);
				LoggerBIW logger = new LoggerBIW(vanillaUrl);

				try {
					String time = logger.getTimeExec(idCurrent.getId());
					list = logger.getTimeperAct(idCurrent.getId());
					listDates = logger.getDates(idCurrent.getId());

					String[] itemstime = new String[list.size()];

					int i = 0;
					for(String name : list.keySet()) {
						itemstime[i] = name;
						i++;
					}

					timeperproc.setText(time + " milliseconds" + "   (" + Integer.parseInt(time) / 1000 + " seconds)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					timeperActList.setItems(itemstime);

					String[] itemsdates = new String[listDates.size()];

					int i2 = 0;
					for(String date : listDates) {
						itemsdates[i2] = date;
						i2++;
					}

					datesCombo.setItems(itemsdates);

					fillgraphics();

				} catch(Exception et) {
					et.printStackTrace();
				}

			}

		});
		Label globtime = new Label(main, SWT.NONE);
		globtime.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		globtime.setText(Messages.DialogMetrics_3);

		timeperproc = new Label(main, SWT.NONE);
		timeperproc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));

		Label actlable = new Label(main, SWT.NONE);
		actlable.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 3, 1));
		actlable.setText(Messages.DialogMetrics_4 + idCurrent.getItemName());

		timeperActList = new org.eclipse.swt.widgets.List(main, SWT.V_SCROLL | SWT.BORDER);
		timeperActList.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 6));

		timeperActList.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				timeperact.setText(list.get(timeperActList.getSelection()[0]).get(0) + " milliseconds"); //$NON-NLS-1$

			}

		});

		compositemetrics = new CompositeChartMetrics(main, Messages.DialogMetrics_0);
		compositemetrics.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));

		compositemetricsProcess = new CompositeChartMetrics(main, Messages.DialogMetrics_7);
		compositemetricsProcess.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));

		Label timeact = new Label(main, SWT.NONE);
		timeact.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		timeact.setText(Messages.DialogMetrics_8);

		timeperact = new Label(main, SWT.NONE);
		timeperact.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));

		Label dates = new Label(main, SWT.NONE);
		dates.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		dates.setText(Messages.DialogMetrics_9);

		datesCombo = new Combo(main, SWT.READ_ONLY);
		datesCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		datesCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				String vanillaUrl = store.getString(PreferencesConstants.P_URLVANILLA);
				LoggerBIW logger = new LoggerBIW(vanillaUrl);

				try {
					String timeactivity = logger.getTimeActivityDate(idCurrent.getId(), timeperActList.getSelection()[0], listDates.get(datesCombo.getSelectionIndex()));
					timeactivitylab.setText(timeactivity);

					String timeprocess = logger.getTimeProcessDate(idCurrent.getId(), listDates.get(datesCombo.getSelectionIndex()));
					timeprocesslab.setText(timeprocess);

				} catch(Exception e1) {
					e1.printStackTrace();
				}
			}

		});
		Label timeprocess = new Label(main, SWT.NONE);
		timeprocess.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		timeprocess.setText(Messages.DialogMetrics_10);

		timeprocesslab = new Label(main, SWT.NONE);
		timeprocesslab.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label timeactivity = new Label(main, SWT.NONE);
		timeactivity.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		timeactivity.setText(Messages.DialogMetrics_11);

		timeactivitylab = new Label(main, SWT.NONE);
		timeactivitylab.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		return main;
	}

	private void fillgraphics() {
		DefaultPieDataset dataset = new DefaultPieDataset();

		for(String name : list.keySet()) {
			String timet = list.get(name).get(0);

			dataset.setValue(name, Double.parseDouble(timet));
		}

		compositemetrics.setDataSet(dataset);
		compositemetrics.drawChart(compositemetrics.getBounds());

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String vanillaUrl = store.getString(PreferencesConstants.P_URLVANILLA);
		LoggerBIW logger = new LoggerBIW(vanillaUrl);

		DefaultPieDataset dataset2 = new DefaultPieDataset();

		for(String date : listDates) {

			try {

				String timeprocess = logger.getTimeProcessDate(idCurrent.getId(), date);
				dataset2.setValue(date, Double.parseDouble(timeprocess));
			} catch(Exception e) {
				e.printStackTrace();
			}

		}

		compositemetricsProcess.setDataSet(dataset2);
		compositemetricsProcess.drawChart(compositemetricsProcess.getBounds());

	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogMetrics_12 + idCurrent.getItemName());
		getShell().setSize(800, 600);

	}

	@Override
	protected void okPressed() {

		super.okPressed();
	}

}

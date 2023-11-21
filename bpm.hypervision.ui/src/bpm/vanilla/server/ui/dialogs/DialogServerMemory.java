package bpm.vanilla.server.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.views.ViewServerInfo;
import bpm.vanilla.server.ui.views.VisualConstants;

public class DialogServerMemory extends Dialog {
	private ChartComposite composite;
	private TimeSeries total, free, occupied;

	public DialogServerMemory(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.MODELESS | SWT.RESIZE | SWT.BORDER | SWT.MIN);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new ChartComposite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setChart(createChart());

		return composite;
	}

	@Override
	public boolean close() {

		if (active) {
			active = false;
			try {
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return super.close();
	}

	private JFreeChart createChart() {
		occupied = new TimeSeries(Messages.DialogServerMemory_0);
		free = new TimeSeries(Messages.DialogServerMemory_1);
		total = new TimeSeries(Messages.DialogServerMemory_2);

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(occupied);
		dataset.addSeries(free);
		dataset.addSeries(total);

		JFreeChart chart = ChartFactory.createTimeSeriesChart(Messages.DialogServerMemory_3, Messages.DialogServerMemory_4, Messages.DialogServerMemory_5, dataset, true, false, false);
		XYPlot plot = chart.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(100000.0); // 60 seconds
		axis = plot.getRangeAxis();
		axis.setRange(0.0, 200000000.0);

		return chart;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		ViewServerInfo part = (ViewServerInfo) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(VisualConstants.SERVER_INFO_VIEW_ID);
		if (part == null) {
			getShell().setSize(500, 600);
		}
		else {
			getShell().setSize(part.getSize().x, 600);
		}
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		Point mainPt = sh.getLocation();
		Point mainSz = sh.getSize();

		getShell().setLocation(mainPt.x, mainPt.y + mainSz.y - getShell().getSize().y);
		getShell().setText(Messages.DialogServerMemory_6);
		th.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected void okPressed() {
		active = false;
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.okPressed();
	}

	boolean active = true;

	Thread th = new Thread() {

		public void run() {
			while (active) {
				try {
					Thread.sleep(1000);

					try {
						final long[] l = Activator.getDefault().getRemoteServerManager().getMemory();
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								free.add(new Millisecond(), l[0]);
								occupied.add(new Millisecond(), l[2] - l[0]);
								total.add(new Millisecond(), l[2]);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
}

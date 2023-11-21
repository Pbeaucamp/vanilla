package bpm.vanilla.server.ui.views.composite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.viewers.RepositoryColumnImageProvider;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;

public class TopTenComposite extends Composite {

	private static final String[] filterTypeNames = new String[] {
		"Report", //$NON-NLS-1$
		"Gateway", //$NON-NLS-1$
		"Workflow", //$NON-NLS-1$
	};
	
	private ComboViewer filterType, groupViewer;
	private Button checkStart, checkEnd;
	private DateTime dateStart, dateEnd;

	private TableViewer topTenViewer;

	private HashMap<RepositoryItem, Double> topTens;

	public TopTenComposite(FormToolkit toolkit, Composite parent, int style) {
		super(parent, style);

		buildContent(toolkit);
	}

	private void buildContent(FormToolkit toolkit) {
		Composite main = toolkit.createComposite(this);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());
		toolkit.paintBordersFor(main);

		createFilterSection(toolkit, main);
		createTableViewer(toolkit, main);

		loadGroups();
	}

	private void createFilterSection(FormToolkit toolkit, Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		toolkit.adapt(composite);
		toolkit.paintBordersFor(composite);

		Label l = toolkit.createLabel(composite, Messages.TopTenComposite_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		filterType = new ComboViewer(new CCombo(composite, SWT.PUSH | SWT.BORDER));
		filterType.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filterType.setContentProvider(new ArrayContentProvider());
		filterType.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof String) {
					return ((String) element);
				}
				return super.getText(element);
			}
		});
		filterType.setInput(filterTypeNames);

		l = toolkit.createLabel(composite, Messages.ItemHistoView_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		groupViewer = new ComboViewer(new CCombo(composite, SWT.PUSH | SWT.BORDER));
		groupViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groupViewer.setContentProvider(new ArrayContentProvider());
		groupViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Group) {
					return ((Group) element).getName();
				}
				return super.getText(element);
			}
		});

		checkStart = toolkit.createButton(composite, Messages.ItemHistoView_3, SWT.CHECK);
		checkStart.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				updateUi(dateStart, source.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		dateStart = new DateTime(composite, SWT.DATE);
		dateStart.setEnabled(false);

		checkEnd = toolkit.createButton(composite, Messages.ItemHistoView_4, SWT.CHECK);
		checkEnd.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				updateUi(dateEnd, source.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		dateEnd = new DateTime(composite, SWT.DATE);
		dateEnd.setEnabled(false);

		Button btnFilter = toolkit.createButton(composite, Messages.ItemHistoView_5, SWT.PUSH);
		btnFilter.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, true, false, 2, 1));
		btnFilter.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateInput();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void updateUi(DateTime dateTime, boolean enable) {
		dateTime.setEnabled(enable);
	}

	private Date getDate(DateTime dateTime) {
		int year = dateTime.getYear();
		int month = dateTime.getMonth();
		int day = dateTime.getDay();

		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, 1, 0, 0);
		return cal.getTime();
	}

	public void refresh() {
		checkStart.setSelection(false);
		dateStart.setEnabled(false);

		checkEnd.setSelection(false);
		dateEnd.setEnabled(false);

		filterType.setSelection(null);
		groupViewer.setSelection(null);

		loadInput(null, null, null, null);
	}

	private void loadGroups() {
		IVanillaAPI vanillaApi = Activator.getDefault().getVanillaApi();
		try {
			List<Group> groups = vanillaApi.getVanillaSecurityManager().getGroups();
			if (groups == null) {
				groups = new ArrayList<Group>();
			}
			groupViewer.setInput(groups);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.ItemHistoView_10, Messages.ItemHistoView_11);
		}
	}

	private void updateInput() {
		Integer itemType = getTypeItem();
		Group group = ((IStructuredSelection) groupViewer.getSelection()).getFirstElement() != null ? (Group) ((IStructuredSelection) groupViewer.getSelection()).getFirstElement() : null;
		Integer groupId = group != null ? group.getId() : null;
		Date startDate = checkStart.getSelection() ? getDate(dateStart) : null;
		Date endDate = checkEnd.getSelection() ? getDate(dateEnd) : null;

		loadInput(itemType, startDate, endDate, groupId);
	}

	public void loadInput(Integer itemType, Date startDate, Date endDate, Integer groupId) {
		this.topTens = null;

		try {
			IRepositoryApi repositoryApi = getRepositoryApi();
			if (repositoryApi != null) {
				this.topTens = repositoryApi.getAdminService().getTopTenItemConsumer(itemType, startDate, endDate, groupId);

				List<RepositoryItem> items = new ArrayList<RepositoryItem>();
				if (topTens != null) {
					for (RepositoryItem item : topTens.keySet()) {
						items.add(item);
					}
				}
				topTenViewer.setInput(items);
			}
			else {
				topTenViewer.setInput(new ArrayList<RepositoryItem>());
			}
		} catch (Exception e) {
			e.printStackTrace();
			topTenViewer.setInput(new ArrayList<RepositoryItem>());
		}
	}

	private IRepositoryApi getRepositoryApi() {
		IVanillaContext ctx = Activator.getDefault().getVanillaApi().getVanillaContext();
		Repository rep = Activator.getDefault().getSelectedRepository();

		Group grp = new Group();
		grp.setId(-1);

		if (ctx != null && rep != null) {
			return new RemoteRepositoryApi(new BaseRepositoryContext(ctx, grp, rep));
		}
		return null;
	}

	private void createTableViewer(FormToolkit toolkit, Composite parent) {
		topTenViewer = new TableViewer(toolkit.createTable(parent, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL));
		topTenViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		topTenViewer.getTable().setHeaderVisible(true);
		topTenViewer.getTable().setLinesVisible(true);
		topTenViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<RepositoryItem> items = (List<RepositoryItem>) inputElement;
				return items.toArray(new RepositoryItem[items.size()]);
			}
		});

		TableViewerColumn image = new TableViewerColumn(topTenViewer, SWT.NONE);
		image.getColumn().setWidth(30);
		image.setLabelProvider(new RepositoryColumnImageProvider());

		TableViewerColumn name = new TableViewerColumn(topTenViewer, SWT.NONE);
		name.getColumn().setText(Messages.TopTenComposite_1);
		name.getColumn().setWidth(200);
		name.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((RepositoryItem) element).getName();
			}
		});

		TableViewerColumn durationAverage = new TableViewerColumn(topTenViewer, SWT.NONE);
		durationAverage.getColumn().setText(Messages.TopTenComposite_2);
		durationAverage.getColumn().setWidth(100);
		durationAverage.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				Double duration = null;
				if (topTens != null) {
					duration = topTens.get(element);
				}
				return getElapsedTime(duration);
			}
		});
	}

	public TableViewer getTopTenViewer() {
		return topTenViewer;
	}

	public String getElapsedTime(Double time) {
		if (time == null) {
			return " - - "; //$NON-NLS-1$
		}
		else {
			Double mill = time.doubleValue() % 1000;

			double secTmp = Math.floor(time.doubleValue() / 1000);
			Double sec = secTmp % 60;

			double minTmp = Math.floor(secTmp / 60);
			Double min = minTmp % 60;

			double hTmp = Math.floor(minTmp / 60);
			Double h = hTmp % 24;

			StringBuffer buf = new StringBuffer();
			if (h.intValue() > 0) {
				buf.append(h.intValue() + Messages.ServerPreviousContent_4);
			}
			if (min.intValue() > 0) {
				buf.append(min.intValue() + Messages.ServerPreviousContent_5);
			}
			if (sec.intValue() > 0) {
				buf.append(sec.intValue() + Messages.ServerPreviousContent_6);
			}
			buf.append(mill.intValue() + Messages.ServerPreviousContent_7);

			return buf.toString();
		}
	}

	public Integer getTypeItem() {
		Integer type = null;
		switch (filterType.getCCombo().getSelectionIndex()) {
		case 0:
			type = IRepositoryApi.CUST_TYPE;
			break;
		case 1:
			type = IRepositoryApi.GTW_TYPE;
			break;
		case 2:
			type = IRepositoryApi.BIW_TYPE;
			break;
		}

		return type;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener iSelectionChangedListener) {
		if (topTenViewer != null) {
			topTenViewer.removeSelectionChangedListener(iSelectionChangedListener);
		}
	}

	public void setSelection(ISelection selection) {
		if (topTenViewer != null) {
			topTenViewer.setSelection(selection);
		}
	}

	public void setSelection(ISelection selection, boolean reveal) {
		if (topTenViewer != null) {
			topTenViewer.setSelection(selection, reveal);
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener iSelectionChangedListener) {
		if (topTenViewer != null) {
			topTenViewer.addSelectionChangedListener(iSelectionChangedListener);
		}
	}
}

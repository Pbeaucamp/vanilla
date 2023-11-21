package org.fasd.views.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.fasd.inport.DigesterFasd;
import org.fasd.inport.converter.MondrianToUOlapConverter;
import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;
import org.fasd.preferences.PreferenceConstants;
import org.freeolap.FreemetricsPlugin;

import bpm.vanilla.repository.ui.versionning.VersionningManager;

public class ActionOpen extends Action {
	private String path = null;
	private boolean recent = false;

	public ActionOpen() {
		super(LanguageText.ActionOpen_Open_File___);
	}

	public ActionOpen(String path) {

		this.path = path;
		recent = true;
	}

	public void run() {
		new ActionNewSchema(FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), false).run();
		IWorkbenchWindow window = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (!recent) {
			FileDialog dd = new FileDialog(window.getShell());
			dd.setFilterExtensions(new String[] { "*.fasd" }); //$NON-NLS-1$
			dd.setText(LanguageText.ActionOpen_Open_Exsisting);
			path = dd.open();
		}

		FreemetricsPlugin.getDefault().setDocOpened(true);
		FreemetricsPlugin.getDefault().setMondrianImport(false);
		if (path != null) {
			try {
				DigesterFasd dig = new DigesterFasd(path);

				FAModel mondrianModel = dig.getFAModel();

				FAModel uolapMod = null;
				if (mondrianModel.getDataSources().get(0) instanceof DatasourceOda) {
					uolapMod = mondrianModel;
				} else {
					System.out.println(mondrianModel.getDataSources());
					uolapMod = MondrianToUOlapConverter.convertFromMondrianToUOlap(mondrianModel);
				}

				FreemetricsPlugin.getDefault().setFAModel(uolapMod);
				FreemetricsPlugin.getDefault().getSessionSourceProvider().setDirectoryItemId(null);
				FreemetricsPlugin.getDefault().getSessionSourceProvider().setModelOpened(true);
				System.out.println("doc " + dig.getFAModel().getDocumentProperties()); //$NON-NLS-1$

				FreemetricsPlugin.getDefault().getSessionSourceProvider().setCheckedIn(VersionningManager.getInstance().getCheckoutInfos(path) != null);

				for (ICube c : dig.getFAModel().getSchema().getICubes()) {
					System.out.println(c.getName());
					System.out.println("provider :" + c.getFAProvider()); //$NON-NLS-1$
					System.out.println("type :" + c.getFAType()); //$NON-NLS-1$
				}

				FreemetricsPlugin.getDefault().setPath(path);
				addFileToList(path);
				window.getShell().setText("Free Analysis Schema Designer - " + path); //$NON-NLS-1$
				FreemetricsPlugin.getDefault().setMondrianImport(false);

				List<DataSource> list = new ArrayList<DataSource>();
				for (DataSource ds : dig.getFAModel().getDataSources()) {
					if (ds.getDriver().getRepositoryDataSourceId() != null) {
						list.add(ds);
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openError(window.getShell(), LanguageText.ActionOpen_Error, LanguageText.ActionOpen_Failed_Read + ex.getMessage());
			}
		}
	}

	private void addFileToList(String path) {
		IPreferenceStore store = FreemetricsPlugin.getDefault().getPreferenceStore();
		String[] list = { store.getString(PreferenceConstants.P_RECENTFILE1), store.getString(PreferenceConstants.P_RECENTFILE2), store.getString(PreferenceConstants.P_RECENTFILE3), store.getString(PreferenceConstants.P_RECENTFILE4), store.getString(PreferenceConstants.P_RECENTFILE5) };

		boolean isEverListed = false;
		for (int i = 0; i < list.length; i++) {
			if (list[i].equals(path))
				isEverListed = true;
		}

		if (!isEverListed) {
			list[4] = list[3];
			list[3] = list[2];
			list[2] = list[1];
			list[1] = list[0];
			list[0] = path;
		}

		store.setValue(PreferenceConstants.P_RECENTFILE1, list[0]);
		store.setValue(PreferenceConstants.P_RECENTFILE2, list[1]);
		store.setValue(PreferenceConstants.P_RECENTFILE3, list[2]);
		store.setValue(PreferenceConstants.P_RECENTFILE4, list[3]);
		store.setValue(PreferenceConstants.P_RECENTFILE5, list[4]);

	}

}

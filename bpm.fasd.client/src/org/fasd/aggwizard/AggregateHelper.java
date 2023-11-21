package org.fasd.aggwizard;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.freeolap.FreemetricsPlugin;

import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

/**
 * Helper class to create the BIGModel and store it
 * 
 * @author ludo
 * 
 */
public class AggregateHelper {

	/**
	 * generate and store the BIG model
	 * 
	 * @param tableName
	 * @param fillScript
	 * @param creationScript
	 * @param fasdCon
	 * @throws Exception
	 */
	public static void generate(String tableName, String fillScript, String creationScript, DataSourceConnection fasdCon) throws Exception {
		Shell shell = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		DialogBigAggregate d = new DialogBigAggregate(FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
		if (d.open() != DialogBigAggregate.OK) {
			return;
		}

		Properties p = d.getProperties();
		RepositoryDirectory directory = d.getDirectory();

		Properties con = new Properties();
		con.setProperty(AggregateTransformationGenerator.URL, fasdCon.getUrl());
		con.setProperty(AggregateTransformationGenerator.PASSWORD, fasdCon.getPass());
		con.setProperty(AggregateTransformationGenerator.LOGIN, fasdCon.getUser());

		String driver = fasdCon.getDriver();
		/*
		 * look for jdbcdriver Key
		 */
		ListDriver driverList = null;
		try {
			driverList = ListDriver.getInstance(Platform.getInstallLocation().getURL().getPath() + "/resources/driverjdbc.xml"); //$NON-NLS-1$
			for (String key : driverList.getDriversName()) {
				DriverInfo inf = driverList.getInfo(key);

				if (inf.getClassName().equals(driver) || fasdCon.getUrl().startsWith(inf.getUrlPrefix())) {
					con.setProperty(AggregateTransformationGenerator.DRIVER_CLASS, inf.getClassName());
					con.setProperty(AggregateTransformationGenerator.DRIVER_NAME, key);
					break;
				}
			}

			if (con.getProperty(AggregateTransformationGenerator.DRIVER_CLASS) == null) {
				throw new Exception(LanguageText.AggregateHelper_1);
			}
		} catch (Exception ex) {
			throw new Exception(LanguageText.AggregateHelper_2 + ex.getMessage(), ex);
		}

		AggregateTransformationGenerator gen = new AggregateTransformationGenerator(p.getProperty(DialogBigAggregate.P_TRANSFO_NAME), tableName, creationScript, fillScript, con);

		if (directory == null) {
			FileOutputStream fos = null;

			try {
				fos = new FileOutputStream(p.getProperty(DialogBigAggregate.P_FILE_NAME));
			} catch (Exception ex) {
				throw new Exception(LanguageText.AggregateHelper_3 + ex.getMessage(), ex);
			}

			try {
				gen.generate(fos);
				MessageDialog.openInformation(shell, LanguageText.AggregateHelper_4, LanguageText.AggregateHelper_5);
			} catch (Exception ex) {
				throw new Exception(LanguageText.AggregateHelper_6 + ex.getMessage(), ex);
			}
		} else {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				gen.generate(os);

				FreemetricsPlugin.getDefault().getRepositoryConnection().getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.GTW_TYPE, -1, directory, p.getProperty(DialogBigAggregate.P_TRANSFO_NAME), "", "", "", os.toString(), true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

				MessageDialog.openInformation(shell, LanguageText.AggregateHelper_11, LanguageText.AggregateHelper_12);
			} catch (Exception ex) {
				throw new Exception(LanguageText.AggregateHelper_13 + ex.getMessage(), ex);
			}

		}

	}
}

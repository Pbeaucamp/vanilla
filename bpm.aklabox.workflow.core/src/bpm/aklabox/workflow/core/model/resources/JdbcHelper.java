package bpm.aklabox.workflow.core.model.resources;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.DriverShim;
import bpm.studio.jdbc.management.model.ListDriver;

public class JdbcHelper {

	@SuppressWarnings("deprecation")
	public static void registerDriver(String className, String driverFile) throws ClassNotFoundException {

		try {
			Class.forName(className);
			return;
		} catch (Exception ex) {

		}

		URL u;
		try {
			File f = new File(IConstants.getJdbcJarFolder() + "/" + driverFile);

			try {
				u = new URL("jar:file:/" + f.getAbsolutePath() + "!/");
				URLClassLoader ucl = new URLClassLoader(new URL[] { u });
				@SuppressWarnings("rawtypes")
				Class c = Class.forName(className, true, ucl);
				Driver d = (Driver) c.newInstance();
				DriverManager.registerDriver(new DriverShim(d));
			} catch (Exception ex) {
				u = f.toURL();
				URLClassLoader ucl = new URLClassLoader(new URL[] { u });
				@SuppressWarnings("rawtypes")
				Class c = Class.forName(className, true, ucl);
				Driver d = (Driver) c.newInstance();
				DriverManager.registerDriver(new DriverShim(d));
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection(String url, String user, String pass) throws Exception {
		try {
			return DriverManager.getConnection(url, user, pass);
		} catch (Exception ex) {
			ex.printStackTrace();

			if (url.startsWith("jdbc:oracle") && url.contains("/")) {
				return DriverManager.getConnection(url.replace("/", ":"), user, pass);
			}
			throw ex;
		}
	}

	public static String getJdbcUrl(DriverInfo driverInfo, String dataBaseName, String host, String portNumber) throws Exception {
		String url = "";
		if (driverInfo.getName().equals(ListDriver.MS_ACCESS) || driverInfo.getName().equals(ListDriver.MS_XLS)) {
			url = driverInfo.getUrlPrefix() + dataBaseName;
			if (!url.trim().endsWith(";")) {
				url = url.trim() + ";";
			}
		}
		else {
			String urlPrefix = driverInfo.getUrlPrefix();
			if (portNumber == null || "".equals(portNumber)) {
				url = urlPrefix + host + "/" + dataBaseName;
			}
			else {
				url = urlPrefix + host + ":" + portNumber + "/" + dataBaseName;
			}

			if (ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(driverInfo.getName()).getClassName().contains("oracle")) {
				url = url.replace("/", ":");
			}

		}

		return url;
	}

}

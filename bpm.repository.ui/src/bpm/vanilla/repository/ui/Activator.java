package bpm.vanilla.repository.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import bpm.vanilla.designer.ui.common.ICheckoutReleaser;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.icons.IconsRegistry;
import bpm.vanilla.repository.ui.versionning.VersionningManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.vanilla.repository.ui"; //$NON-NLS-1$

	public static final String versionningFileName = "versionning.xml"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	// private int repositoryItemType;

	private HashMap<String, Integer> designerPluginsIdType = new HashMap<String, Integer>();

	private IDesignerActivator<?> designerActivatorInstance;

	private PropertyChangeListener listener;

	/**
	 * The constructor
	 */
	public Activator() {
		designerPluginsIdType.put("bpm.metadata.client", IRepositoryApi.FMDT_TYPE); //$NON-NLS-1$
		designerPluginsIdType.put("bpm.gateway.ui", IRepositoryApi.GTW_TYPE); //$NON-NLS-1$
		designerPluginsIdType.put("bpm.fasd.client", IRepositoryApi.FASD_TYPE); //$NON-NLS-1$
		designerPluginsIdType.put("bpm.fd.repository.ui", IRepositoryApi.FD_TYPE); //$NON-NLS-1$
		designerPluginsIdType.put("bpm.workflow.ui", IRepositoryApi.BIW_TYPE); //$NON-NLS-1$
		designerPluginsIdType.put("bpm.hypervision.ui", IRepositoryApi.TASK_LIST); //$NON-NLS-1$
		designerPluginsIdType.put("bpm.sqldesigner.ui", IRepositoryApi.DWH_VIEW_TYPE); //$NON-NLS-1$

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		String fileName = Platform.getInstallLocation().getURL().getPath() + "/resources/" + versionningFileName; //$NON-NLS-1$
		VersionningManager.getInstance().load(fileName);

		if(getDesignerActivator() != null) {
			listener = new PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {

					String filePath = evt.getPropertyName();

					if(VersionningManager.getInstance().getCheckoutInfos(filePath) != null) {

						boolean release = MessageDialog.openQuestion(getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.Activator_10, Messages.Activator_11);

						if(release) {

							if(getDesignerActivator() instanceof ICheckoutReleaser) {
								try {
									((ICheckoutReleaser) getDesignerActivator()).checkin(evt.getNewValue());
								} catch(Exception ex) {
									ex.printStackTrace();
									MessageDialog.openError(getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.Activator_12, Messages.Activator_13 + ex.getMessage());
								}
							}
							else {
								checkin(getWorkbench().getActiveWorkbenchWindow().getShell(), filePath);
							}

						}

					}

				}

			};

			getDesignerActivator().addLoadModelListener(listener);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		for(Field f : IconsRegistry.class.getFields()) {

			try {
				reg.put((String) f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null))); //$NON-NLS-1$
			} catch(IllegalArgumentException e) {
				e.printStackTrace();
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}

		}

	}

	public IDesignerActivator<?> getDesignerActivator() {

		for(String s : designerPluginsIdType.keySet()) {
			Bundle bundle = Platform.getBundle(s);
			if(bundle == null) {
				continue;
			}
			String activatorClassName = (String) bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);

			try {
				Class activatorClass = bundle.loadClass(activatorClassName);
				Method method = activatorClass.getMethod("getDefault"); //$NON-NLS-1$
				Object activatorInstance = method.invoke(null);

				if(activatorInstance != null && activatorInstance instanceof IDesignerActivator<?>) {
					designerActivatorInstance = (IDesignerActivator<?>) activatorInstance;
					break;
				}
			} catch(Throwable ex) {
				ex.printStackTrace();
			}

		}
		return designerActivatorInstance;
	}

	private void checkin(Shell shell, String fileName) {

		Properties p = VersionningManager.getInstance().getCheckoutInfos(fileName);

		if(p == null) {
			MessageDialog.openInformation(shell, Messages.Activator_16, Messages.Activator_17);

			VersionningManager.getInstance().performCheckin(fileName);
			return;
		}
		String directoryItemId = p.getProperty("directoryItemId"); //$NON-NLS-1$

		IRepositoryApi sock = new RemoteRepositoryApi(getDesignerActivator().getRepositoryContext());


		RepositoryItem dirIt = null;

		try {
			dirIt = sock.getRepositoryService().getDirectoryItem(Integer.parseInt(directoryItemId));
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		InputStream modelStream = null;
		try {
			String xml = getDesignerActivator().getCurrentModelXml();
			if(xml == null) {
				try {
					modelStream = new FileInputStream(fileName);
				} catch(Exception ex) {
					throw ex;
				}
			}
			else {
				modelStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			}
			sock.getVersioningService().checkIn(dirIt, "", modelStream); //$NON-NLS-1$

			MessageDialog.openInformation(shell, Messages.Activator_26, Messages.Activator_27);
			VersionningManager.getInstance().performCheckin(fileName);
		} catch(Exception e) {
			e.printStackTrace();
			MessageDialog.openError(shell, Messages.Activator_28, e.getMessage());
			VersionningManager.getInstance().saveChekout(fileName, sock, dirIt);
		}
	}
}

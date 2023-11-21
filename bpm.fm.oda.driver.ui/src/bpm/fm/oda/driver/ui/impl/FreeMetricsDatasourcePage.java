package bpm.fm.oda.driver.ui.impl;

import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.Theme;
import bpm.fm.oda.driver.impl.Connection;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class FreeMetricsDatasourcePage extends DataSourceWizardPage {

	public FreeMetricsDatasourcePage(String pageName) {
		super(pageName);
	}

	private Text url;
	private Text login;
	private Text password;
	private ComboViewer groupNames;
	private ComboViewer themes;
	
	private IFreeMetricsManager manager;
	private RemoteVanillaPlatform vanillaApi;
	private int groupId;
	private int themeId;
	
	@Override
	public Properties collectCustomProperties() {
		Properties props = new Properties();
		
		props.setProperty(Connection.PROP_FM_GROUP_ID, groupId+"");
		props.setProperty(Connection.PROP_FM_LOGIN, login.getText());
		props.setProperty(Connection.PROP_FM_PASSWORD, password.getText());
		props.setProperty(Connection.PROP_FM_THEME_ID, themeId+"");
		props.setProperty(Connection.PROP_FM_URL, url.getText());
		
		return props;
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		parent.setLayout(new GridLayout(3,false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText("Url"); //$NON-NLS-1$
		
		url = new Text(parent, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1 ));
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Login"); //$NON-NLS-1$
		
		login = new Text(parent, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1 ));

		
		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText("Password"); //$NON-NLS-1$
		
		password = new Text(parent, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1 ));
		
		Button conRep = new Button(parent, SWT.PUSH);
		conRep.setText("Load group and themes"); //$NON-NLS-1$
		conRep.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		conRep.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				connect(-1, -1);

				
			}
			
		});
		
		Label l7 = new Label(parent, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText("Group");  //$NON-NLS-1$
	
		groupNames = new ComboViewer(parent, SWT.READ_ONLY);
		groupNames.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		groupNames.setContentProvider(new ArrayContentProvider());
		groupNames.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		groupNames.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Group group = (Group) ((IStructuredSelection)event.getSelection()).getFirstElement();
				groupId = group.getId();
				try {
					List<Theme> ths = manager.getThemesByGroup(groupId);
					themes.setInput(ths);
					if(themeId > 0) {
						for(Theme th : ths) {
							if(th.getId() == themeId) {
								themes.setSelection(new StructuredSelection(th));
								break;
							}
						}
					}
					else {
						themeId = ths.get(0).getId();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
		
		
		
		
		l7 = new Label(parent, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText("Theme"); //$NON-NLS-1$
		
		
		themes = new ComboViewer(parent, SWT.READ_ONLY);
		themes.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		themes.setContentProvider(new ArrayContentProvider());
		themes.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Theme)element).getName();
			}
		});
		themes.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Theme group = (Theme) ((IStructuredSelection)event.getSelection()).getFirstElement();
				themeId = group.getId();
			}
		});
	}

	@Override
	public void setInitialProperties(Properties props) {
		
		String propUrl = props.getProperty(Connection.PROP_FM_URL);
		String propLogin = props.getProperty(Connection.PROP_FM_LOGIN);
		String propPass = props.getProperty(Connection.PROP_FM_PASSWORD);
		int propGrp = Integer.parseInt(props.getProperty(Connection.PROP_FM_GROUP_ID));
		int propTheme = Integer.parseInt(props.getProperty(Connection.PROP_FM_THEME_ID));
		
		url.setText(propUrl);
		login.setText(propLogin);
		password.setText(propPass);
		
		if(propUrl != null && !propUrl.isEmpty() && propLogin != null && !propLogin.isEmpty() && propPass != null && !propPass.isEmpty()) {
			connect(propGrp, propTheme);
		}
		
		
	}

	private void connect(int propGrp, int propTheme) {
		manager = new RemoteFreeMetricsManager(url.getText(), login.getText(), password.getText());
		vanillaApi = new RemoteVanillaPlatform(url.getText(), login.getText(), password.getText());
		
		User user = null;
		try {
			user = vanillaApi.getVanillaSecurityManager().authentify("", login.getText(), password.getText(), false);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		List<Group> groups = null;
		try {
			groups = vanillaApi.getVanillaSecurityManager().getGroups(user);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		groupNames.setInput(groups);
		
		if(propGrp > -1) {
			if(propTheme > -1) {
				themeId = propTheme;
			}
			groupId = propGrp;
			for(Group g : groups) {
				if(g.getId() == propGrp) {
					groupNames.setSelection(new StructuredSelection(g));
					break;
				}
			}
			
		}
		else {
			groupId = groups.get(0).getId();
		}
	}
	
}

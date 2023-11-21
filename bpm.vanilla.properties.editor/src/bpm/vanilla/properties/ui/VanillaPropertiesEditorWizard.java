package bpm.vanilla.properties.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class VanillaPropertiesEditorWizard {

	private Shell shell;
	private String configFile;

	private TabFolder mainTabFolder;

	private HashMap<String, Widget> properties = new HashMap<String, Widget>();

	public static void main(String[] args) {
		try {
			VanillaPropertiesEditorWizard window = new VanillaPropertiesEditorWizard();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createContents() {
		shell = new Shell();
		shell.setSize(926, 484);
		shell.setText(Messages.VANILLA_CONFIGURATOR);
		shell.setLayout(new GridLayout());

		Menu menu = buildMenu(shell);
		shell.setMenuBar(menu);

		mainTabFolder = new TabFolder(shell, SWT.NONE);
		mainTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private Menu buildMenu(final Shell shell) {
		Menu menu = new Menu(shell, SWT.BAR);
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("&File");

		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		mntmFile.setMenu(fileMenu);

		MenuItem fileLoadFile = new MenuItem(fileMenu, SWT.PUSH);
		fileLoadFile.setText("&Open File");
		fileLoadFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dial = new FileDialog(shell);
				configFile = dial.open();

				if (configFile != null && !configFile.isEmpty()) {
					FileInputStream fis;
					try {
						fis = new FileInputStream(configFile);
						Properties configProperties = new Properties();
						configProperties.load(fis);

						String folderPath = getVanillaConfigurationFolder(configFile);
						if (folderPath != null && !folderPath.isEmpty()) {
							File xmlDefinitionFile = new File(folderPath + File.separator + "vanilla_definition.xml");
							if (xmlDefinitionFile.exists()) {
								buildContent(xmlDefinitionFile, configProperties);
								return;
							}
						}

						throw new Exception("Unable to find the XML configuration file (vanilla_definition.xml) for this version of vanilla.properties.");
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(shell, "Problem", e1.getMessage());
					}
				}
			}
		});

		MenuItem fileSaveItem = new MenuItem(fileMenu, SWT.PUSH);
		fileSaveItem.setText("&Save");
		fileSaveItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (configFile != null) {
					try {
						saveVanillaProperties(configFile);
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(shell, "Problem", e1.getMessage());
					}
				}
			}
		});

		// MenuItem fileSaveAsItem = new MenuItem(fileMenu, SWT.PUSH);
		// fileSaveAsItem.setText("&Save As...");
		// fileSaveAsItem.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		// FileDialog dial = new FileDialog(shell);
		// String saveAs = dial.open();
		// if(saveAs != null && !saveAs.isEmpty()) {
		// if (!saveAs.contains(".properties")) {
		// saveAs = saveAs + ".properties";
		// }
		// try {
		// saveVanillaProperties(saveAs);
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// MessageDialog.openError(shell, "Problem", e1.getMessage());
		// }
		// }
		// }
		//
		// });

		MenuItem fileExit = new MenuItem(fileMenu, SWT.PUSH);
		fileExit.setText("&Exit");
		fileExit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.exit(0);
			}

		});

		MenuItem mntmAbout = new MenuItem(menu, SWT.CASCADE);
		mntmAbout.setText("&About");

		Menu fileAbout = new Menu(shell, SWT.DROP_DOWN);
		mntmAbout.setMenu(fileAbout);

		MenuItem fileAboutDial = new MenuItem(fileAbout, SWT.PUSH);
		fileAboutDial.setText(Messages.About_Vanilla_Conf);
		fileAboutDial.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AboutVanillaConfigurator about = new AboutVanillaConfigurator(shell, SWT.NONE);
				about.open();
			}

		});

		return menu;
	}

	private String getVanillaConfigurationFolder(String configFilePath) throws Exception {
		int lastIndex = configFilePath.lastIndexOf(File.separator);
		if (lastIndex > 0) {
			return configFilePath.substring(0, lastIndex);
		}

		throw new Exception("Unable to find the XML configuration file (vanilla_definition.xml) for this version of vanilla.properties.");
	}

	private void buildContent(File xmlDefinitionFile, Properties configProperties) throws DocumentException, IOException {

		if(mainTabFolder != null && !mainTabFolder.isDisposed()) {
			mainTabFolder.dispose();
		}
		mainTabFolder = new TabFolder(shell, SWT.NONE);
		mainTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		shell.layout(true);
		
		FileInputStream definitionFileIs = new FileInputStream(xmlDefinitionFile);
		String result = IOUtils.toString(definitionFileIs, "UTF-8");
		definitionFileIs.close();

		Document doc = DocumentHelper.parseText(result);
		Element element = doc.getRootElement();

		List<?> tabElements = element.elements("tab");
		if (tabElements != null) {
			for (Object tabElement : tabElements) {
				if (tabElement instanceof Element) {
					createTab(mainTabFolder, (Element) tabElement, configProperties);
				}
			}
		}
	}

	private void createTab(TabFolder folderParent, Element tabElement, Properties configProperties) {
		String tabName = tabElement.attributeValue("title") != null ? tabElement.attributeValue("title") : "";

		List<?> subTabElements = tabElement.elements("section");
		if (subTabElements != null && !subTabElements.isEmpty()) {
			TabFolder tabFolderProp = new TabFolder(folderParent, SWT.NONE);
			tabFolderProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			TabItem tabItem = new TabItem(folderParent, SWT.NONE);
			tabItem.setText(tabName);
			tabItem.setControl(tabFolderProp);

			for (Object subTap : subTabElements) {
				if (subTap instanceof Element) {
					createTab(tabFolderProp, (Element) subTap, configProperties);
				}
			}
		}
		else {
			List<?> propElements = tabElement.elements("prop");
			if (propElements != null) {
				ScrolledComposite scrollComp = new ScrolledComposite(folderParent, SWT.V_SCROLL | SWT.H_SCROLL);
				scrollComp.setAlwaysShowScrollBars(false);
				scrollComp.setExpandHorizontal(true);
				scrollComp.setExpandVertical(true);

				Composite propertiesComposite = new Composite(scrollComp, SWT.NONE);
				propertiesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				propertiesComposite.setLayout(new GridLayout(2, false));
				
				scrollComp.setContent(propertiesComposite);
				scrollComp.setMinSize(propertiesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

				for (Object property : propElements) {
					if (property instanceof Element) {
						createProperty(propertiesComposite, (Element) property, configProperties);
					}
				}

				TabItem tabItem = new TabItem(folderParent, SWT.NONE);
				tabItem.setText(tabName);
				tabItem.setControl(scrollComp);
			}
		}
	}

	private void createProperty(Composite sectionComposite, Element section, Properties configProperties) {
		String title = section.attributeValue("title") != null ? section.attributeValue("title") : "";
		String description = section.attributeValue("description");
		int type = section.attributeValue("type") != null ? Integer.parseInt(section.attributeValue("type")) : 0;

		String name = section.attributeValue("name") != null ? section.attributeValue("name") : "";
		String propertyValue = configProperties.getProperty(name) != null ? configProperties.getProperty(name) : "";

		int columnSpan = 1;

		Label label = new Label(sectionComposite, SWT.NONE);

		Font boldFont = new Font(label.getDisplay(), new FontData("Arial", 10, SWT.BOLD));
		label.setFont(boldFont);
		
		if (description != null) {
			columnSpan = 2;
			
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, columnSpan, 1));

			Label lblDescription = new Label(sectionComposite, SWT.NONE);
			lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, columnSpan, 1));
			lblDescription.setText(description);
		}
		label.setText(title);

		Widget widget = null;
		if (type == 0) {
			Text text = new Text(sectionComposite, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, columnSpan, 1));
			text.setText(propertyValue);

			widget = text;
		}
		else if (type == 1) {
			List<String> values = new ArrayList<String>();

			List<?> valueElements = section.elements("value");
			if (valueElements != null) {
				for (Object valueElement : valueElements) {
					if (valueElement instanceof Element) {
						String value = ((Element) valueElement).getText();
						values.add(value != null ? value : "");
					}
				}
			}

			if (values != null && !values.isEmpty()) {
				ComboViewer combo = new ComboViewer(sectionComposite, SWT.NONE);
				combo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, columnSpan, 1));
				combo.setContentProvider(new ArrayContentProvider());
				combo.setInput(values);

				int index = -1;
				for (int i = 0; i < values.size(); i++) {
					if (values.get(i).equalsIgnoreCase(propertyValue)) {
						index = i;
						break;
					}
				}

				if (index != -1) {
					combo.getCombo().select(index);
				}

				widget = combo.getCombo();
			}
			else {
				Text text = new Text(sectionComposite, SWT.BORDER);
				text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, columnSpan, 1));
				text.setText(propertyValue);

				widget = text;
			}
		}
		else if (type == 2) {
			boolean checked = propertyValue.equalsIgnoreCase("true");

			Button check = new Button(sectionComposite, SWT.CHECK);
			check.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, columnSpan, 1));
			check.setSelection(checked);

			widget = check;
		}

		properties.put(name, widget);
	}

	private void saveVanillaProperties(String saveConfigFile) throws ConfigurationException {
		PropertiesConfiguration config = new PropertiesConfiguration(configFile);
		for (String property : properties.keySet()) {
			Widget widg = properties.get(property);
			String value = "";
			if (widg instanceof Text) {
				value = ((Text) widg).getText();
			}
			else if (widg instanceof Combo) {
				value = ((Combo) widg).getText();
			}
			else if (widg instanceof Button) {
				value = String.valueOf(((Button) widg).getSelection());
			}

			config.setProperty(property, value);
		}
		config.save();

		configFile = saveConfigFile;
	}
}

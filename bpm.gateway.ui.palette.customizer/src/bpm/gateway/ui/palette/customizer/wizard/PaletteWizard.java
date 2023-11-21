package bpm.gateway.ui.palette.customizer.wizard;

import java.io.File;
import java.io.FileOutputStream;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import bpm.gateway.ui.palette.customizer.Activator;
import bpm.gateway.ui.palette.customizer.utils.PaletteXmlGenerator;

public class PaletteWizard extends Wizard{

	private GeneralPage generalPage;
	private PaletteContentPage contentPage;
	
	private String paletteName;
	
	@Override
	public void addPages() {
		generalPage = new GeneralPage("GeneralPage", "Palette Description", Activator.getDefault().getImageRegistry().getDescriptor("palette"));
		addPage(generalPage);
		
		contentPage = new PaletteContentPage("Palette Definition Page");
		contentPage.setDescription("Define the Palette Layout by defining some Groups and drag in them the steps you want.");
		addPage(contentPage);
	}
	@Override
	public boolean performFinish() {

		Document doc = PaletteXmlGenerator.generateXmlDocument(generalPage.getPaletteName(), contentPage.getMap());
		
		String paletteFolder = new String(Activator.PALETTE_FOLDER);
		
		File folder = new File(paletteFolder);
		if (!folder.exists()){
			folder.mkdirs();
		}
		paletteName = generalPage.getPaletteName();
		File f = new File(paletteFolder + "/" + paletteName+ ".gtwpal");
		if (f.exists()){
			if (!MessageDialog.openQuestion(getShell(), "Palette Save", "A palette with the same name already exists. Do you want to override it?")){
				return false;
			}
		}
		
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new FileOutputStream(f), OutputFormat.createPrettyPrint());
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Problem", "Unable to create the file : "  + e.getMessage());
			writer = null;
			return false;
		} 
		try {
			writer.write(doc);
			writer.close();
		}catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Problem", "Unable to write the file : "  + e.getMessage());
			return false;
		}
			
			
		
		return true;
	}
	public String getPaletteName() {
		return paletteName;
	}

}

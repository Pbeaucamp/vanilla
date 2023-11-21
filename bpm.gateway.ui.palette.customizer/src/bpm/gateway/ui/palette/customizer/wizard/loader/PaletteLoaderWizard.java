package bpm.gateway.ui.palette.customizer.wizard.loader;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import bpm.gateway.ui.palette.customizer.utils.PaletteEntry;

public class PaletteLoaderWizard extends Wizard implements IImportWizard{
	private PaletteListPage page;
	
	private HashMap<String, List<PaletteEntry>> palette;
	@Override
	public void addPages() {
		page = new PaletteListPage("palettes");
		page.setDescription("Select the Palette to use");
		addPage(page);
	}
	
	
	@Override
	public boolean performFinish() {
		palette = page.getPalette();
		return palette != null;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}

	public HashMap<String, List<PaletteEntry>> getPalette(){
		return palette;
	}
}

package bpm.fd.design.ui.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.design.ui.Activator;

public class ExportFdPackageWizard extends Wizard implements IExportWizard{

	private ExportFdPackagePage page;
	
	public ExportFdPackageWizard() {
		super();
	}
	
	@Override
	public void addPages() {
		page = new ExportFdPackagePage("Export as a Fd package");
		page.setTitle("Export as a Fd package");
		page.setDescription("Export the project as a freedashboard package");
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		String path = page.getPath();
		
		File project = Activator.getDefault().getResourceProject().getLocation().toFile();
		
		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(path));

			int len = project.getAbsolutePath().lastIndexOf(File.separator);
			String baseName = project.getAbsolutePath().substring(0,len+1);
			
			addFolderToZip(project, out, baseName);
			
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	private void addFolderToZip(File folder, ZipOutputStream zip, String baseName) throws IOException {
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				addFolderToZip(file, zip, baseName);
			} else {
				String name = file.getAbsolutePath().substring(baseName.length());
				ZipEntry zipEntry = new ZipEntry(name);
				zip.putNextEntry(zipEntry);
				IOUtils.copy(new FileInputStream(file), zip);
				zip.closeEntry();
			}
		}
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setNeedsProgressMonitor(true);
	}

}

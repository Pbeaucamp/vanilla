package bpm.es.datasource.analyzer.ui.remapper;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import adminbirep.Activator;
import bpm.es.datasource.analyzer.remapper.ModelRemapper;
import bpm.es.datasource.analyzer.ui.Messages;

public class UpdateRemappedModelsWithProgress implements IRunnableWithProgress {

	private List<ModelRemapper> remappers;
	
	private List<Exception> errors = new ArrayList<Exception>();
	
	public UpdateRemappedModelsWithProgress(List<ModelRemapper> remappers){
		this.remappers = remappers;
	}
	
	public void run(IProgressMonitor monitor) throws InvocationTargetException, 	InterruptedException {

		SubMonitor progress = SubMonitor.convert(monitor, Messages.UpdateRemappedModelsWithProgress_0, remappers.size());
		
		progress.beginTask(Messages.UpdateRemappedModelsWithProgress_1, remappers.size());
		for(ModelRemapper remap : remappers){
			progress.subTask(Messages.UpdateRemappedModelsWithProgress_2 + remap.getItemName());
			OutputFormat f = OutputFormat.createPrettyPrint();
			f.setEncoding("UTF-8"); //$NON-NLS-1$
			f.setTrimText(false);
			f.setNewlines(false);
			 
			Thread.sleep(2000); 
			 
			 try{
				 ByteArrayOutputStream bos = new ByteArrayOutputStream();
    			 XMLWriter writer = new XMLWriter(bos, f);
    			 writer.write(remap.getModelXmlDocument().getRootElement());
    			 writer.close();
    			 progress.subTask(Messages.UpdateRemappedModelsWithProgress_4 + remap.getItemName() + Messages.UpdateRemappedModelsWithProgress_5);
    			 
    			 Activator.getDefault().getRepositoryApi().getRepositoryService().updateModel(remap.getItem(), bos.toString("UTF-8").trim()); //$NON-NLS-1$

    			 progress.worked(1);
    			 
			 }catch(Exception ex){
				 ex.printStackTrace();
				 errors.add(new Exception("Update " + remap.getItemName() + " failed : " + ex.getMessage(), ex)); //$NON-NLS-1$ //$NON-NLS-2$
			 }
			 Thread.sleep(1000); 
		}

		

	}
	
	
	

	
	public List<Exception> getErrors(){
		return errors;
	}
}

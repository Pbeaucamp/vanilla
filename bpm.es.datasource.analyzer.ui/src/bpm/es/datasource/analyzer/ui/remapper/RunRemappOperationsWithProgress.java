package bpm.es.datasource.analyzer.ui.remapper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import bpm.es.datasource.analyzer.remapper.IRemapperPerformer;
import bpm.es.datasource.analyzer.remapper.ModelRemapper;
import bpm.es.datasource.analyzer.ui.Messages;

public class RunRemappOperationsWithProgress implements IRunnableWithProgress {

	private List<ModelRemapper> remappers;
	
	private List<Exception> errors = new ArrayList<Exception>();
	
	public RunRemappOperationsWithProgress(List<ModelRemapper> remappers){
		this.remappers = remappers;
	}
	
	public void run(IProgressMonitor monitor) throws InvocationTargetException, 	InterruptedException {

		SubMonitor progress = SubMonitor.convert(monitor, Messages.RunRemappOperationsWithProgress_0, remappers.size());
		
		//		monitor.beginTask("Perform Remappings", remappers.size());
//		
		for(ModelRemapper remap : remappers){
			Thread.sleep(2000);
			runMapper(progress.newChild(1), remap);
//			monitor.subTask("Replacing references in model Xml " + remap.getItemName());
//			for(IRemapperPerformer p : remap.getPerformers()){
//				
//			}
//			monitor.worked(1);
		}

		

	}
	
	
	private void runMapper(IProgressMonitor monitor, ModelRemapper mapper){
		SubMonitor progress = SubMonitor.convert(monitor, mapper.getPerformers().size());
		progress.setTaskName(Messages.RunRemappOperationsWithProgress_1 + mapper.getItemName());
		for(IRemapperPerformer p : mapper.getPerformers()){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			runPerformer(progress.newChild(1), p);
		}
	}
	
	private void runPerformer(IProgressMonitor monitor, IRemapperPerformer performer){
		
		monitor.beginTask(performer.getTaskPerfomed(), 1);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			performer.performModification();
		} catch (Exception e) {
			e.printStackTrace();
			errors.add(new Exception(Messages.RunRemappOperationsWithProgress_2 + performer.getTaskPerfomed() + " : " + e.getMessage(), e));//$NON-NLS-1$
		}
		monitor.done();
	}

	
	public List<Exception> getErrors(){
		return errors;
	}
}

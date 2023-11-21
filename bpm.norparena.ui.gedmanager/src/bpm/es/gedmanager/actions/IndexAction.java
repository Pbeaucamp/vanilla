package bpm.es.gedmanager.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import bpm.es.gedmanager.Messages;
import bpm.es.gedmanager.api.GedIndex;
import bpm.es.gedmanager.api.GedModel;
import bpm.norparena.ui.menu.Activator;
import bpm.vanilla.platform.core.beans.ged.ComProperties;
import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.IComProperties;

public class IndexAction implements IRunnableWithProgress {
	private GedIndex toIndex;
	private GedModel model;
	
	private String category;
	
	private String version;
	private String description;
	
	public IndexAction(GedIndex toIndex, GedModel model, String cat, String version, String description) {
		this.toIndex = toIndex;
		this.model = model;
		this.category = cat;
		this.version = version;
		this.description = description;
	}
	
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {	
		monitor.setTaskName(Messages.IndexAction_0);
		monitor.beginTask(Messages.IndexAction_1, toIndex.getSelectedElements().size() + 2);
		
		try {
			
			monitor.subTask(Messages.IndexAction_2);
			monitor.worked(1);
			
			for (GedDocument doc : toIndex.getSelectedElements()) {
//				Properties props = new Properties();
//				props.setProperty("name", doc.getName());
//				props.setProperty("format", doc.getFormat());
//				props.setProperty("group", "System");
				IComProperties props = setupProps(doc);
				monitor.subTask("Indexing " + doc.getName() + " on server"); //$NON-NLS-1$ //$NON-NLS-2$
				model.indexPendingDocument(doc, props);
				monitor.worked(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvocationTargetException(e);
		}
	}

	/*
	author
	title
	path
	content
	publicationdate
	version
	previousversion
	category
	group
	docid
	summary
	custom1
			 */
	private IComProperties setupProps(GedDocument f) throws Exception {
		String user = Activator.getDefault().getRepositoryContext().getVanillaContext().getLogin();
		
		ComProperties props = new ComProperties();
		
		for (Definition def : model.getFieldDefinitions()) {
			//System.out.println(def.getName());
			if (def.getName().equals("author")) { //$NON-NLS-1$
				props.setProperty(def, user);
			}
			else if (def.getName().equals("title")) { //$NON-NLS-1$
				props.setProperty(def, f.getName());
			}
			else if (def.getName().equals("path")) { //$NON-NLS-1$
//				props.setProperty(def, f.getRelativPath());
			}
//			else if (def.getName().equals("content")) {
//				props.setProperty(def, "");
//			}
//			else if (def.getName().equals("publicationdate")) {
//				props.setProperty(def, "");
//			}
			else if (def.getName().equals("version")) { //$NON-NLS-1$
				props.setProperty(def, version);
			}
//			else if (def.getName().equals("previousversion")) {
//				props.setProperty(def, "");
//			}
			else if (def.getName().equals("category")) { //$NON-NLS-1$
				props.setProperty(def, category);
			}
			else if (def.getName().equals("group")) { //$NON-NLS-1$
				props.setProperty(def, "" + 0); //$NON-NLS-1$
			}
//			else if (def.getName().equals("docid")) {
//				props.setProperty(def, );
//			}
			else if (def.getName().equals("summary")) { //$NON-NLS-1$
				props.setProperty(def, description);
			}
			else if (def.getName().equals("custom1")) { //$NON-NLS-1$
				props.setProperty(def, ""); //$NON-NLS-1$
			}
		}
		
		return props;
	}
}

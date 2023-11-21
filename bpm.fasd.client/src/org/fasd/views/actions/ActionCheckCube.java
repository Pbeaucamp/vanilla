package org.fasd.views.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.fasd.datasource.DataObject;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.utils.ActionGetPath;
import org.fasd.utils.Path;
import org.fasd.views.dialogs.DialogCheck;
import org.freeolap.FreemetricsPlugin;

public class ActionCheckCube extends Action {
	private OLAPCube cube;

	private List<String> log = new ArrayList<String>();

	public ActionCheckCube() {
		super(LanguageText.ActionCheckCube_Check_Cube);

	}

	public void setCube(OLAPCube cube) {
		this.cube = cube;
	}

	public void run() {

		// check measure
		DataObject fact = cube.getFactDataObject();

		for (OLAPMeasure m : cube.getMes()) {
			// physical measure
			if (m.getType().equals("physical")) { //$NON-NLS-1$
				if (m.getOrigin() == null) {
					log.add("Measure " + m.getName() + " : no Origin defined"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (!fact.getColumns().contains(m.getOrigin())) {
					log.add("Measure " + m.getName() + " : no matching Column found in fact Table"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			// calculated Measure
			else if (m.getType().equals("calculated")) { //$NON-NLS-1$
				String[] split = m.getFormula().split("]"); //$NON-NLS-1$
				for (int i = 1; i < split.length; i += 2) {
					try {
						OLAPMeasure mes = cube.getParent().findMeasureNamed(split[i].substring(2));
						if (!cube.getMes().contains(mes)) {
							log.add("Cube " + cube.getName() + " doesn't contains all measures needed to compute the CalculatedMeasure " + m.getName()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}

		if (cube.getDims().size() < 2)
			log.add("The cube has less than 2 dimensions."); //$NON-NLS-1$

		for (OLAPDimension d : cube.getDims()) {
			for (OLAPHierarchy h : d.getHierarchies()) {
				checkDimension(h);
			}
		}

		if (log.size() > 0) {
			DialogCheck dial = new DialogCheck(LanguageText.ActionCheckCube_Error_in_Cube + cube.getName(), FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), log);
			dial.setBlockOnOpen(false);
			dial.open();
		} else {
			MessageDialog.openInformation(FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), LanguageText.ActionCheckCube_Information, LanguageText.ActionCheckCube_No_Error);
		}

	}

	private void checkDimension(OLAPHierarchy hiera) {
		List<DataObject> tables = new ArrayList<DataObject>();

		// check levels
		for (OLAPLevel l : hiera.getLevels()) {
			if (l.getItem() == null) {
				log.add("Hierarchy " + hiera.getName() + " : Level " + l.getName() + " column not defined"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else if (l.getItem().getParent().isView()) {
				log.add("Hierarchy " + hiera.getName() + " : Level " + l.getName() + " column based on sql query, only supported on FactTable"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				if (!tables.contains(l.getItem().getParent())) {
					tables.add(l.getItem().getParent());
				}
				// parent child hierarchy
				if (l.isClosureNeeded()) {
					if (l.getClosureChildCol() == null) {
						log.add("Hierarchy " + hiera.getName() + " : Level " + l.getName() + " child column not defined"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					if (l.getClosureParentCol() == null) {
						log.add("Hierarchy " + hiera.getName() + " : Level " + l.getName() + " parent column not defined"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
			}

		}

		// hierarchy : check the relation between fact and dims
		if (!tables.contains(cube.getFactDataObject()))
			tables.add(cube.getFactDataObject());

		ActionGetPath action = new ActionGetPath(FreemetricsPlugin.getDefault().getFAModel(), tables.toArray(new DataObject[tables.size()]));
		action.run();
		Path p = action.getPath();
		if (p == null && !hiera.getParent().isDegenerated(cube.getFactDataObject())) {
			String s = "Relation Missing between tables : "; //$NON-NLS-1$
			boolean first = true;
			for (DataObject o : tables) {
				if (first) {
					first = false;
					s += o.getName();
				} else {
					s += ", " + o.getName(); //$NON-NLS-1$
				}

			}
			log.add(s);
		} else if (p != null) {
			boolean factFound = false;
			boolean lvlFound = true;
			for (DataObject o : p.getUsedTables()) {
				if (o == cube.getFactDataObject()) {
					factFound = true;
				}

				if (!tables.contains(o)) {
					lvlFound = false;
				}

			}
			if (!lvlFound || !factFound) {
				log.add("Relations Missing between Dimension " + hiera.getParent().getName() + LanguageText.ActionCheckCube_0 + cube.getName() + LanguageText.ActionCheckCube_1); //$NON-NLS-1$
			}
		}

	}

	public List<String> getErrors() {
		return log;
	}
}

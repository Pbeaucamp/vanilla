package org.fasd.wizard.aggregate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.aggregate.AggLevel;
import org.fasd.olap.aggregate.AggMeasure;
import org.fasd.olap.aggregate.AggregateTable;

public class AggWizard extends Wizard implements INewWizard {
	private PageRoot rootPage;
	private PageDimension dimPage;
	private PageMeasure mesPage;
	private PageForeignKey fkPage;
	
	protected OLAPCube cube;
	protected DataObject aggregateTable;
	protected List<AggMeasure> aggMes = new ArrayList<AggMeasure>();
	
	//dimensions on aggregates
	protected List<AggLevel> aggLvl = new ArrayList<AggLevel>();
	
	protected List<OLAPRelation> relations = new ArrayList<OLAPRelation>();
	protected DataObjectItem countCol;
	
	public AggWizard(OLAPCube cube) {
		this.cube = cube;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.setWindowTitle(LanguageText.AggWizard_0);

	}

	@Override
	public void addPages() {
		rootPage = new PageRoot(LanguageText.AggWizard_1);
		rootPage.setTitle(LanguageText.AggWizard_2);
		rootPage.setDescription(""); //$NON-NLS-1$
		addPage(rootPage);
		
		mesPage = new PageMeasure(LanguageText.AggWizard_4);
		mesPage.setTitle(LanguageText.AggWizard_5);
		mesPage.setDescription(""); //$NON-NLS-1$
		addPage(mesPage);
		
		dimPage = new PageDimension(LanguageText.AggWizard_7);
		dimPage.setTitle(LanguageText.AggWizard_8);
		dimPage.setDescription(""); //$NON-NLS-1$
		addPage(dimPage);

		fkPage = new PageForeignKey(LanguageText.AggWizard_10);
		fkPage.setTitle(LanguageText.AggWizard_11);
		fkPage.setDescription(""); //$NON-NLS-1$
		addPage(fkPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page == rootPage){
			mesPage.fillData();
			return mesPage;
		}
		else if (page == mesPage){
			dimPage.fillData();
			return dimPage;
		}
		else if (page == dimPage){
			fkPage.fillData();
			return fkPage;
		}
			
		return page;
	}
	
	public AggregateTable getAggTable(){
		AggregateTable t = new AggregateTable();
		for(AggMeasure m : aggMes)
			t.addAggMeasure(m);
		for(AggLevel l : aggLvl)
			t.addAggLevel(l);
		t.setTable(aggregateTable);
		t.setFactCountColumn(countCol);
		
		return t;
	}
	
	public List<OLAPRelation> getRelation(){
		return relations;
		
	}

	@Override
	public boolean canFinish() {
		return fkPage.isCurrentPage();
	}
	
	
}

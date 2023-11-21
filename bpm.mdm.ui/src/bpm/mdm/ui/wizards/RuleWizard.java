package bpm.mdm.ui.wizards;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Rule;
import bpm.mdm.model.rules.RulesFactory;
import bpm.mdm.model.rules.RulesPackage;
import bpm.mdm.ui.i18n.Messages;
import bpm.mdm.ui.wizards.pages.IRulePage;
import bpm.mdm.ui.wizards.pages.LinkagePage;
import bpm.mdm.ui.wizards.pages.PatternPage;
import bpm.mdm.ui.wizards.pages.RangePage;
import bpm.mdm.ui.wizards.pages.RulePage;
import bpm.mdm.ui.wizards.pages.SetPage;

public class RuleWizard extends Wizard{

	private RulePage type;
	private PatternPage pattern;
	private RangePage range;
	private SetPage set;
	private LinkagePage link;
	
	private Attribute attribute;
	private Rule editedrule;
	
	public RuleWizard(Attribute att){
		this.attribute = att;
	}
	public RuleWizard(Attribute att, Rule rule){
		this.attribute = att;
		this.editedrule = rule;
		
	}
	
	@Override
	public void addPages() {
		
		
		if (editedrule != null){
			type = new RulePage("main", editedrule, attribute.getDataType()); //$NON-NLS-1$
			type.setTitle(Messages.RuleWizard_1);
			addPage(type);
			
			if (RulesPackage.eINSTANCE.getPatternRule().isSuperTypeOf(editedrule.eClass())){
				pattern = new PatternPage("pattern",editedrule); //$NON-NLS-1$
				pattern.setTitle(Messages.RuleWizard_3);
				addPage(pattern);
			}
			if (RulesPackage.eINSTANCE.getRangeRule().isSuperTypeOf(editedrule.eClass())){
				range = new RangePage("Range",editedrule); //$NON-NLS-1$
				range.setTitle(Messages.RuleWizard_5);
				addPage(range);
			}
			if (RulesPackage.eINSTANCE.getSetValueRule().isSuperTypeOf(editedrule.eClass())){
				set = new SetPage("set", editedrule); //$NON-NLS-1$
				set.setTitle(Messages.RuleWizard_7);
				addPage(set);
			}
			if (RulesPackage.eINSTANCE.getEntityLinkRule().isSuperTypeOf(editedrule.eClass())){
				link = new LinkagePage("link", editedrule); //$NON-NLS-1$
				link.setTitle(Messages.RuleWizard_0);
				addPage(link);
			}
			
			
		}
		else{
			type = new RulePage("main", attribute.getDataType()); //$NON-NLS-1$
			type.setTitle(Messages.RuleWizard_9);
			addPage(type);
			
			pattern = new PatternPage("pattern"); //$NON-NLS-1$
			pattern.setTitle(Messages.RuleWizard_11);
			addPage(pattern);
			
			range = new RangePage("Range"); //$NON-NLS-1$
			range.setTitle(Messages.RuleWizard_13);
			addPage(range);
			
			set = new SetPage("set"); //$NON-NLS-1$
			set.setTitle(Messages.RuleWizard_15);
			addPage(set);
			
			link = new LinkagePage("link"); //$NON-NLS-1$
			link.setTitle(Messages.RuleWizard_2);
			addPage(link);
		}
		
		
	
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == type){
			if (RulesPackage.eINSTANCE.getPatternRule().isSuperTypeOf(type.getRuleClass())){
				return pattern;
			}
			if (RulesPackage.eINSTANCE.getRangeRule().isSuperTypeOf(type.getRuleClass())){
				return range;
			}
			if (RulesPackage.eINSTANCE.getSetValueRule().isSuperTypeOf(type.getRuleClass())){
				return set;
			}
			if (RulesPackage.eINSTANCE.getEntityLinkRule().isSuperTypeOf(type.getRuleClass())){
				return link;
			}
		}
		return null;
	}
	
	@Override
	public boolean performFinish() {
		try{
			Rule rule = null;
			
			if (editedrule == null){
			
				rule = (Rule)RulesFactory.eINSTANCE.create(type.getRuleClass());
				rule.setName(type.getRuleClass().getName());
				
				type.setRule(rule);
				((IRulePage)getNextPage(type)).setRule(rule);
				
				
				attribute.getRules().add(rule);
			}
			else{
				type.setRule(editedrule);
				((IRulePage)getNextPage(type)).setRule(editedrule);
			}
			
			
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.RuleWizard_16, Messages.RuleWizard_17 + ex.getMessage());
			return false;
		}
		
	}

	@Override
	public boolean canFinish() {
		try{
			return getContainer().getCurrentPage() != null && getContainer().getCurrentPage() != type && getContainer().getCurrentPage().isPageComplete();
		}catch(Exception ex){
			return false;
		}
		
	}
}

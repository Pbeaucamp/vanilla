package bpm.smart.web.client.wizards;

import java.util.Date;

import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;

public class RecodeWizard extends GwtWizard {
	
	public enum Recode {
		RECODE("Recoding"), DATETOAGE("Date to Age Transformation"), AGETORANGE("Age to Range Classification"), 
		CLASS("Classification"), FILTER("Filtering"), MAP("Mapping"), CALCULATED("Calculated Column Creation");
		
		private String label = "";
		   
		  //Constructeur
		  Recode(String label){
		    this.label = label;
		  }
		   
		  public String getLabel(){
		    return label;
		  }
	}
	
	private IGwtPage previousPage;
	private IGwtPage actualPage;
	
	private RecodeDatasetPage datasetPage;
	
	private RecodeRecodePage recodePage;
	private RecodeDateToAgePage datetoAge;
	private RecodeAgeToRange agetoRange;
	private RecodeCalculatedPage calculatedPage;
	private RecodeClassificationPage classificationPage;
	private RecodeFilterPage filterPage;
	
	private RecodeResultPage resultPage;
	
	private Dataset dataset;
	private Recode recodeType;
	private String generatedCode;
	
	private boolean confirm = false;
	
	public RecodeWizard(Recode recodeType) {
		super(recodeType.getLabel());
		this.recodeType = recodeType;
		datasetPage = new RecodeDatasetPage(this);
		setCurrentPage(datasetPage);
	}

	@Override
	public boolean canFinish() {
		return resultPage != null && resultPage.isComplete();
	}

	@Override
	public void updateBtn() {
		setBtnBackState(actualPage.canGoBack() ? true : false);
		setBtnNextState(actualPage.canGoFurther() ? true : false);
		setBtnFinishState(canFinish() ? true : false);
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		previousPage = actualPage;
		actualPage = page;
		setContentPanel((Composite)actualPage);
		updateBtn();
	}

	@Override
	protected void onClickFinish() {
		generatedCode = resultPage.getCode();
		confirm = true;
		RecodeWizard.this.hide();
	}

	@Override
	protected void onNextClick() {
		switch(recodeType){
		case AGETORANGE: //pas de page intermediaire
			if(actualPage.getIndex() == 0){ 
				agetoRange = new RecodeAgeToRange(this, datasetPage.getDatasetName(), datasetPage.getColumnName(), datasetPage.getNewColumnName());
				Timer timerLoadRange = new Timer() {
					public void run() {
						if(agetoRange.isbLoadRange()){
							resultPage = new RecodeResultPage(RecodeWizard.this, agetoRange.getGeneratedCode());
							setCurrentPage(resultPage);
						}
						
				    }
				};	
				timerLoadRange.scheduleRepeating(500);
			}
			break;
		case CALCULATED:
			if(actualPage.getIndex() == 0){ 
				calculatedPage = new RecodeCalculatedPage(this, datasetPage.getDatasetName(), datasetPage.getColumnName(), datasetPage.getNewColumnName());
				setCurrentPage(calculatedPage);
			} else {
				resultPage = new RecodeResultPage(this, calculatedPage.getGeneratedCode());
				setCurrentPage(resultPage);
			}
			break;
		case CLASS:
			if(actualPage.getIndex() == 0){ 
				classificationPage = new RecodeClassificationPage(this, datasetPage.getDatasetName(), datasetPage.getColumnName(), datasetPage.getNewColumnName());
				setCurrentPage(classificationPage);
			} else {
				resultPage = new RecodeResultPage(this, classificationPage.getGeneratedCode());
				setCurrentPage(resultPage);
			}
			break;
		case DATETOAGE:
			if(actualPage.getIndex() == 0){ 
				datetoAge = new RecodeDateToAgePage(this, datasetPage.getDatasetName(), datasetPage.getColumnName(), datasetPage.getNewColumnName());
				setCurrentPage(datetoAge);
			} else {
				resultPage = new RecodeResultPage(this, datetoAge.getGeneratedCode());
				setCurrentPage(resultPage);
			}
			
			break;
		case FILTER:
			if(actualPage.getIndex() == 0){ 
				filterPage = new RecodeFilterPage(this, datasetPage.getDatasetName(), datasetPage.getColumnName(), datasetPage.getNewColumnName());
				setCurrentPage(filterPage);
			} else {
				resultPage = new RecodeResultPage(this, filterPage.getGeneratedCode());
				setCurrentPage(resultPage);
			}
			break;
		case MAP:
			break;
		case RECODE:
			if(actualPage.getIndex() == 0){ 
				recodePage = new RecodeRecodePage(this, datasetPage.getDatasetName(), datasetPage.getColumnName(), datasetPage.getNewColumnName());
				setCurrentPage(recodePage);
			} else {
				resultPage = new RecodeResultPage(this, recodePage.getGeneratedCode());
				setCurrentPage(resultPage);
			}
			break;
			
		}
		
	}

	@Override
	protected void onBackClick() {
		setCurrentPage(previousPage);
	}

	
	
	public Dataset getDataset(){
		return dataset;
	}
	
	public boolean isConfirm(){
		return confirm;
	}

	public String getGeneratedCode() {
		DateTimeFormat df = DateTimeFormat.getFormat("dd/MM/yyyy hh:mm"); 
		StringBuffer buf = new StringBuffer();
		buf.append("\n### ").append(recodeType.getLabel()).append(" on ").append(datasetPage.getDatasetName()+"$"+datasetPage.getColumnName());
		buf.append(" - ").append(df.format(new Date())).append(" - ").append("\n");
		buf.append(generatedCode);
		buf.append("\n###\n");
		
		return buf.toString();
	}

	public Recode getRecodeType() {
		return recodeType;
	}
	
	
}

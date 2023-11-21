package bpm.fwr.client.wizard.chart;

import java.util.List;

import bpm.fwr.api.beans.components.ChartType;
import bpm.fwr.api.beans.components.IChart;
import bpm.fwr.api.beans.components.OptionsFusionChart;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;

public class ChartCreationWizard extends GwtWizard {

	private IGwtPage currentPage;
	private ChartDefinitionPage chartDefinitionPage;
	private ChartSelectionColumnPage chartSelectionColumnPage;
	private ChartOptionsPage chartOptionsPage;

	private ReportSheet reportSheetParent;
	private IChart chartComponent;
	private List<DataSet> dsAvailable;

	public ChartCreationWizard(ReportSheet reportSheetParent, IChart chartComponent, List<DataSet> dsAvailable) {
		super(Bpm_fwr.LBLW.ChartTitle());
		
		this.reportSheetParent = reportSheetParent;
		this.dsAvailable = dsAvailable;
		this.chartComponent = chartComponent;

		chartDefinitionPage = new ChartDefinitionPage(this, 0, chartComponent);
		setCurrentPage(chartDefinitionPage);
	}

	@Override
	public boolean canFinish() {
		return chartSelectionColumnPage == null || (chartDefinitionPage.isComplete() && chartSelectionColumnPage.isComplete());
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		if (page instanceof ChartDefinitionPage)
			setContentPanel((ChartDefinitionPage) page);
		else if (page instanceof ChartSelectionColumnPage)
			setContentPanel((ChartSelectionColumnPage) page);
		else if (page instanceof ChartOptionsPage)
			setContentPanel((ChartOptionsPage) page);
		currentPage = page;
		updateBtn();
	}

	@Override
	public void updateBtn() {
		setBtnBackState(currentPage.canGoBack() ? true : false);
		setBtnNextState(currentPage.canGoFurther() ? true : false);
		setBtnFinishState(canFinish() ? true : false);
	}

	@Override
	protected void onClickFinish() {
		if (chartSelectionColumnPage != null && chartSelectionColumnPage.isComplete()) {
			IChart chartComp = chartSelectionColumnPage.generateChart(false);
			finish(chartComp, ChartCreationWizard.this, null);
			ChartCreationWizard.this.hide();
		}
	}

	@Override
	protected void onBackClick() {
		if (currentPage instanceof ChartSelectionColumnPage) {
			setCurrentPage(chartDefinitionPage);
		}
		else if (currentPage instanceof ChartOptionsPage) {
			setCurrentPage(chartSelectionColumnPage);
		}
	}

	@Override
	protected void onNextClick() {
		if (currentPage instanceof ChartDefinitionPage) {
//			ChartOperations chartOperation = chartDefinitionPage.getSelectedChartOperation();
			ChartType chartType = chartDefinitionPage.getSelectedChartType();
			OptionsFusionChart chartOptions = chartDefinitionPage.getChartOptions();
			String chartTitle = chartDefinitionPage.getChartTitle();

			if (chartSelectionColumnPage == null) {
				List<FwrMetadata> metadatas = reportSheetParent.getPanelParent().getPanelparent().getMetadatas();
				chartSelectionColumnPage = new ChartSelectionColumnPage(ChartCreationWizard.this, 1, chartTitle, chartType, chartOptions, metadatas, dsAvailable, chartComponent);
			}
			else {
				chartSelectionColumnPage.setChartTitle(chartTitle);
				chartSelectionColumnPage.setChartType(chartType);
//				chartSelectionColumnPage.setChartOperation(chartOperation);
				chartSelectionColumnPage.setChartOptions(chartOptions);
				chartSelectionColumnPage.initPage();
			}
			setCurrentPage(chartSelectionColumnPage);
		}
		else if (currentPage instanceof ChartSelectionColumnPage) {
			if (chartOptionsPage == null) {
				chartOptionsPage = new ChartOptionsPage(chartComponent);
			}
			setCurrentPage(chartOptionsPage);
		}
	}

	public boolean isVanillaChart() {
		return true;
	}

}

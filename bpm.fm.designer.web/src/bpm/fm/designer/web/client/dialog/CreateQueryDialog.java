package bpm.fm.designer.web.client.dialog;

import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableObjectives;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.utils.AxisQueries;
import bpm.fm.api.model.utils.MetricSqlQueries;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.fm.designer.web.client.utils.QueryPanel;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class CreateQueryDialog extends AbstractDialogBox {

	private static CreateQueryDialogUiBinder uiBinder = GWT.create(CreateQueryDialogUiBinder.class);
	
	interface MyStyle extends CssResource {
		String queryNotExists();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;

	private MetricSqlQueries queries;

	interface CreateQueryDialogUiBinder extends UiBinder<Widget, CreateQueryDialog> {
	}

	public CreateQueryDialog(MetricSqlQueries queries) {
		super(Messages.lbl.createTables(), true, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		this.queries = queries;
		
		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
		
		createQueryPanels();
		
		if(contentPanel.getWidgetCount() == 0) {
			
			HTML html = new HTML(Messages.lbl.allTableExists());
			
			html.addStyleName(style.queryNotExists());
			
			contentPanel.add(html);
		}
	
	}

	private void createQueryPanels() {
		QueryPanel factQuery = new QueryPanel(Messages.lbl.factQueryTitle(), Messages.lbl.factQueryCheck(), queries.getFactQuery(), queries.getMetric().getFactTable());
		
		if(queries.getFactQuery() != null && !queries.getFactQuery().isEmpty()) {
			contentPanel.add(factQuery);
		}
		
		if(queries.getObjectiveQuery() != null && !queries.getObjectiveQuery().isEmpty()) {
			QueryPanel objQuery = new QueryPanel(Messages.lbl.ObjQueryTitle(), Messages.lbl.ObjQueryCheck(), queries.getObjectiveQuery(), ((FactTable)queries.getMetric().getFactTable()).getObjectives());
			
			contentPanel.add(objQuery);
		}
		
		for(AxisQueries axisQuery : queries.getAxisQueries()) {
			
			for(List<Level> levels : axisQuery.getLevelQueries().keySet()) {
				String levelsString = " (for levels";
				for(Level lvl : levels) {
					levelsString += " " + lvl.getName();
				}
				levelsString += ") ";
				
				String title = Messages.lbl.AxisQueryTitle1() + " " + axisQuery.getAxis().getName() + levelsString + Messages.lbl.AxisQueryTitle2();
				QueryPanel axisPanel = new QueryPanel(title, Messages.lbl.AxisQueryCheck(), axisQuery.getLevelQueries().get(levels), levels);
				
				contentPanel.add(axisPanel);
			}
			

		}
		
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			MetricSqlQueries queriesToExecute = new MetricSqlQueries(queries.getMetric());
			
			for(int i = 0 ; i < contentPanel.getWidgetCount() ; i++) {
				Widget w = contentPanel.getWidget(i);
				if(w instanceof QueryPanel) {
					QueryPanel qPanel = (QueryPanel) w;
					
					if(qPanel.execute()) {
						String query = qPanel.getQuery();
						
						Object reference = qPanel.getReference();
						if(reference instanceof FactTable) {
							queriesToExecute.setFactQuery(query);
						}
						else if(reference instanceof FactTableObjectives) {
							queriesToExecute.setObjectiveQuery(query);
						}
						else {
							List<Level> levels = (List<Level>) reference;
							
							LOOK:for(AxisQueries aq : queries.getAxisQueries()) {
								for(List<Level> lvls : aq.getLevelQueries().keySet()) {
									if(levels.equals(lvls)) {
										AxisQueries a = new AxisQueries(aq.getAxis());
										a.addLevelQuery(levels, query);
										queriesToExecute.getAxisQueries().add(a);
										break LOOK;
									}
								}
							}
						}
					}
					
				}
			}
			
			MetricService.Connection.getInstance().executeQueries(queriesToExecute, new AsyncCallback<HashMap<String, Exception>>() {
				
				@Override
				public void onSuccess(HashMap<String, Exception> result) {
					if(result != null && !result.isEmpty()) {
						
						QueryExceptionDialog dial = new QueryExceptionDialog(result);
						dial.center();
						
					}
					else {
						InformationsDialog dial = new InformationsDialog(Messages.lbl.Success(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.executeQueriesSuccess(), false);
						dial.center();
						
						CreateQueryDialog.this.hide();
					}
					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.executeQueriesProblem(), caught.getMessage(), caught);
					dial.center();
				}
			});
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			CreateQueryDialog.this.hide();
		}
	};
}

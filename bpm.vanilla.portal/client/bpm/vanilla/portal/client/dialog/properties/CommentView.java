package bpm.vanilla.portal.client.dialog.properties;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class CommentView extends Composite {

	private static CommentViewUiBinder uiBinder = GWT.create(CommentViewUiBinder.class);

	interface CommentViewUiBinder extends UiBinder<Widget, CommentView> {
	}
	
	interface MyStyle extends CssResource {
		String pager();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel panelPager, panelContent;
	
	private static final String DATE_FORMAT = "HH:mm - dd/MM/yyyy";
	
	private ListDataProvider<Comment> dataProvider;
	private ListHandler<Comment> sortHandler;
	private List<Comment> comments;
	
	private HashMap<Comment, Integer> commentLevels = new HashMap<Comment, Integer>();

	public CommentView(PortailRepositoryItem item) {
		initWidget(uiBinder.createAndBindUi(this));
		
		panelContent.setWidget(createGridData());
		
		fillNotes(item);
	}

	public void refreshResult(){
		if(comments != null){
			this.dataProvider.setList(comments);
			sortHandler.setList(dataProvider.getList());
		}
	}
	
	private void loadComment(List<Comment> comments){
		commentLevels.clear();
		List<Comment> flatComments = flatCommentList(comments, 0);
		this.comments = flatComments;
		
		refreshResult();
	}
	
	private DataGrid<Comment> createGridData() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);
		
		TextCell cell = new TextCell();
		Column<Comment, String> commentColumn = new Column<Comment, String>(cell) {

			@Override
			public String getValue(Comment object) {
				return object.getComment();
			}
		};
		commentColumn.setSortable(true);
	    
		Column<Comment, String> beginDateColumn = new Column<Comment, String>(cell) {

			@Override
			public String getValue(Comment object) {
				Date date = object.getBeginDate(); 
				if (date == null || date.equals(new Date(0))) {
					return ToolsGWT.lblCnst.Never();
				}
				return dateFormatter.format(date);
			}

		};
		beginDateColumn.setSortable(true);
	    
		Column<Comment, String> endDateColumn = new Column<Comment, String>(cell) {

			@Override
			public String getValue(Comment object) {
				Date date = object.getEndDate(); 
				if (date == null || date.equals(new Date(0))) {
					return ToolsGWT.lblCnst.Never();
				}
				return dateFormatter.format(date);
			}
		};
		endDateColumn.setSortable(true);

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Comment> dataGrid = new DataGrid<Comment>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(commentColumn, ToolsGWT.lblCnst.Comment());
		dataGrid.addColumn(beginDateColumn, ToolsGWT.lblCnst.BeginDate());
		dataGrid.addColumn(endDateColumn, ToolsGWT.lblCnst.EndDate());
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoComment()));

	    dataProvider = new ListDataProvider<Comment>();
	    dataProvider.addDataDisplay(dataGrid);
		
	    sortHandler = new ListHandler<Comment>(new ArrayList<Comment>());
		sortHandler.setComparator(commentColumn, new Comparator<Comment>() {
			
			@Override
			public int compare(Comment o1, Comment o2) {
				return o1.getComment().compareTo(o2.getComment());
			}
		});
		sortHandler.setComparator(beginDateColumn, new Comparator<Comment>() {
			
			@Override
			public int compare(Comment o1, Comment o2) {
				if(o1.getBeginDate() == null) {
					return -1;
				}
				else if(o2.getBeginDate() == null) {
					return 1;
				}
				
				return o2.getBeginDate().before(o1.getBeginDate()) ? -1 : o2.getBeginDate().after(o1.getBeginDate()) ? 1 : 0;
			}
		});
		sortHandler.setComparator(endDateColumn, new Comparator<Comment>() {
			
			@Override
			public int compare(Comment o1, Comment o2) {
				if(o1.getEndDate() == null) {
					return -1;
				}
				else if(o2.getEndDate() == null) {
					return 1;
				}
				
				return o2.getEndDate().before(o1.getEndDate()) ? -1 : o2.getEndDate().after(o1.getEndDate()) ? 1 : 0;
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);
		
		// Create a Pager to control the table.
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.addStyleName(style.pager());
	    pager.setDisplay(dataGrid);

	    panelPager.setWidget(pager);
	    
	    return dataGrid;
	}
	
	public void fillNotes(PortailRepositoryItem dto) {
		
		BiPortalService.Connect.getInstance().getComments(dto.getId(), Comment.ITEM, new AsyncCallback<List<Comment>>() {
			
			@Override
			public void onSuccess(List<Comment> result) {
				if(result != null){
					loadComment(result);
				}
				else {
					loadComment(new ArrayList<Comment>());
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				loadComment(new ArrayList<Comment>());
				
				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}
		});
	}
	
	private List<Comment> flatCommentList(List<Comment> comments, int level) {
		List<Comment> coms = new ArrayList<Comment>();
		for(Comment c : comments) {
			coms.add(c);
			commentLevels.put(c, level);
			if(c.getChilds() != null && !c.getChilds().isEmpty()) {
				coms.addAll(flatCommentList(c.getChilds(), level + 1));
			}
		}
		return coms;
	}
}

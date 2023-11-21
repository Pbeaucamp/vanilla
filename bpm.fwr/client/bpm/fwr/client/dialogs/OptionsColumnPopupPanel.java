package bpm.fwr.client.dialogs;

import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.Column.TextAlignType;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.draggable.widgets.DraggableHTMLPanel;
import bpm.fwr.client.widgets.ListWidget;
import bpm.gwt.commons.client.listeners.FinishListener;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class OptionsColumnPopupPanel extends PopupPanel {
	private static final String ASCENDING = "asc";
	private static final String DESCENDING = "desc";
	private static final String IMG_CHECK = "images/check10.png";
	
	private DraggableHTMLPanel widget;
	
	private List<Column> groups;
	private String defaultLanguage;
	
	private MenuItem ascending, descending, none;
	private MenuItem itemItalic, itemBold, itemUnderline;
	private MenuItem alignLeft,  alignCenter, alignRight;
	private MenuItem showHeader, hideHeader;
	
	public OptionsColumnPopupPanel(boolean autohide, boolean modal, DraggableHTMLPanel widget, boolean isGroup) {
		super(autohide, modal);
		this.widget = widget;
		
		groups = ((ListWidget)widget.getColumn().getReportWidgetParent()).getGroupColumns();
		String defaultLanguage = 
			((ListWidget)widget.getColumn().getReportWidgetParent()).
				getReportSheetParent().getPanelParent().getPanelparent().
				getLanguageDefaultForMetadataWithId();
		
		MenuBar popupMenuBar = new MenuBar(true);
		popupMenuBar.setAutoOpen(true);
		popupMenuBar.setAnimationEnabled(true);
		
		if(widget.getColumn().getColumn().isSorted() && widget.getColumn().getColumn().getSortType().equals(ASCENDING)){
			ascending = new MenuItem("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.Ascending(), true, ascendingCommand);
			descending = new MenuItem(Bpm_fwr.LBLW.Descending(), true, descendingCommand);
			none = new MenuItem(Bpm_fwr.LBLW.None(), true, noneSortCommand);
		}
		else if(widget.getColumn().getColumn().isSorted() && widget.getColumn().getColumn().getSortType().equals(DESCENDING)){
			ascending = new MenuItem(Bpm_fwr.LBLW.Ascending(), true, ascendingCommand);
			descending = new MenuItem("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.Descending(), true, descendingCommand);
			none = new MenuItem(Bpm_fwr.LBLW.None(), true, noneSortCommand);
		}
		else {
			ascending = new MenuItem(Bpm_fwr.LBLW.Ascending(), true, ascendingCommand);
			descending = new MenuItem(Bpm_fwr.LBLW.Descending(), true, descendingCommand);
			none = new MenuItem("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.None(), true, noneSortCommand);
		}
		
		MenuBar popupSortMenuBar = new MenuBar(true);
		popupSortMenuBar.setAnimationEnabled(true);
		popupSortMenuBar.addItem(ascending);
		popupSortMenuBar.addItem(descending);
		popupSortMenuBar.addItem(none);
		
		MenuItem sortItem = new MenuItem(Bpm_fwr.LBLW.Sort(), popupSortMenuBar);
		popupMenuBar.addItem(sortItem);
		
		MenuItem itemSum = new MenuItem(Bpm_fwr.LBLW.ManageAggregation(), true, sumCommand);
		
		MenuBar popupAggregateColumn = new MenuBar(true);
		popupAggregateColumn.setAnimationEnabled(true);
		popupAggregateColumn.addItem(itemSum);
		
		MenuItem aggItem = new MenuItem(Bpm_fwr.LBLW.Aggregate(), true, popupAggregateColumn); 
		aggItem.addStyleName("popup-item");
		popupMenuBar.addItem(aggItem);
	
		
		if(widget.getColumn().getColumn().isBold()){
			itemBold = new MenuItem("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.Bold(), true, boldCommand);
		}
		else {
			itemBold = new MenuItem(Bpm_fwr.LBLW.Bold(), true, boldCommand);
		}

		if(widget.getColumn().getColumn().isItalic()){
			itemItalic = new MenuItem("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.Italic(), true, italicCommand);
		}
		else {
			itemItalic = new MenuItem(Bpm_fwr.LBLW.Italic(), true, italicCommand);
		}

		if(widget.getColumn().getColumn().isUnderline()){
			itemUnderline = new MenuItem("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.Underline(), true, underlineCommand);
		}
		else {
			itemUnderline = new MenuItem(Bpm_fwr.LBLW.Underline(), true, underlineCommand);
		}
		
		MenuBar menuStyleLabelColumn = new MenuBar(true);
		menuStyleLabelColumn.setAnimationEnabled(true);
		menuStyleLabelColumn.addItem(itemBold);
		menuStyleLabelColumn.addItem(itemItalic);
		menuStyleLabelColumn.addItem(itemUnderline);
		

		MenuItem styleLabelItem = new MenuItem(Bpm_fwr.LBLW.StyleColumnName(), menuStyleLabelColumn);
		popupMenuBar.addItem(styleLabelItem);
		
		
		if(widget.getColumn().getColumn().getTextAlign().equals("left")){
			alignLeft = new MenuItem("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.left(), true, leftCommand);
			alignCenter = new MenuItem(Bpm_fwr.LBLW.Center(), true, centerCommand);
			alignRight = new MenuItem(Bpm_fwr.LBLW.right(), true, rightCommand);
		}
		else if(widget.getColumn().getColumn().getTextAlign().equals("center")){
			alignLeft = new MenuItem(Bpm_fwr.LBLW.left(), true, leftCommand);
			alignCenter = new MenuItem("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.Center(), true, centerCommand);
			alignRight = new MenuItem(Bpm_fwr.LBLW.right(), true, rightCommand);
		}
		else {
			alignLeft = new MenuItem(Bpm_fwr.LBLW.left(), true, leftCommand);
			alignCenter = new MenuItem(Bpm_fwr.LBLW.Center(), true, centerCommand);
			alignRight = new MenuItem("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.right(), true, rightCommand);
		}

		
		MenuBar menuTextAlign = new MenuBar(true);
		menuTextAlign.setAnimationEnabled(true);
		menuTextAlign.addItem(alignLeft);
		menuTextAlign.addItem(alignCenter);
		menuTextAlign.addItem(alignRight);
		
		MenuItem textAlignItem = new MenuItem(Bpm_fwr.LBLW.TextAlign(), menuTextAlign);
		popupMenuBar.addItem(textAlignItem);
		
		
		if(widget.getColumn().getReportWidgetParent().showHeader()){
			showHeader = new MenuItem("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.ShowHeader(), true, showHeaderCommand);
			hideHeader = new MenuItem(Bpm_fwr.LBLW.HideHeader(), true, hideHeaderCommand);
		}
		else {
			showHeader = new MenuItem(Bpm_fwr.LBLW.ShowHeader(), true, showHeaderCommand);
			hideHeader = new MenuItem("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.HideHeader(), true, hideHeaderCommand);
		}

		
		MenuBar menuListShowHeader = new MenuBar(true);
		menuListShowHeader.setAnimationEnabled(true);
		menuListShowHeader.addItem(showHeader);
		menuListShowHeader.addItem(hideHeader);
		
		MenuItem listShowHeaderItem = new MenuItem(Bpm_fwr.LBLW.DisplayHeader(), menuListShowHeader);
		popupMenuBar.addItem(listShowHeaderItem);
		
		//Set style
		this.setStyleName("popup");
		
		sortItem.addStyleName("popupMenu");
		ascending.addStyleName("popup-item");
		descending.addStyleName("popup-item");
		none.addStyleName("popup-item");
		
		styleLabelItem.addStyleName("popupMenu");
		itemBold.addStyleName("popup-item");
		itemItalic.addStyleName("popup-item");
		itemUnderline.addStyleName("popup-item");
		
		textAlignItem.addStyleName("popupMenu");
		alignCenter.addStyleName("popup-item");
		alignLeft.addStyleName("popup-item");
		alignRight.addStyleName("popup-item");
		
		listShowHeaderItem.addStyleName("popupMenu");
		showHeader.addStyleName("popup-item");
		hideHeader.addStyleName("popup-item");
		
		this.add(popupMenuBar);
	}
	
	Command leftCommand = new Command() {
		public void execute() {
			widget.getColumn().getColumn().setTextAlign(TextAlignType.LEFT);

			alignLeft.setHTML("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.left());
			alignCenter.setHTML(Bpm_fwr.LBLW.Center());
			alignRight.setHTML(Bpm_fwr.LBLW.right());
			
			((ListWidget)widget.getColumn().getReportWidgetParent()).updateColumnContent(true);
		}
	};
	
	Command rightCommand = new Command() {
		public void execute() {
			widget.getColumn().getColumn().setTextAlign(TextAlignType.RIGHT);
			

			alignLeft.setHTML(Bpm_fwr.LBLW.left());
			alignCenter.setHTML(Bpm_fwr.LBLW.Center());
			alignRight.setHTML("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.right());
			
			((ListWidget)widget.getColumn().getReportWidgetParent()).updateColumnContent(true);
		}
	};
	
	Command centerCommand = new Command() {
		public void execute() {
			widget.getColumn().getColumn().setTextAlign(TextAlignType.CENTER);
			
			alignLeft.setHTML(Bpm_fwr.LBLW.left());
			alignCenter.setHTML("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.Center());
			alignRight.setHTML(Bpm_fwr.LBLW.right());
			
			((ListWidget)widget.getColumn().getReportWidgetParent()).updateColumnContent(true);
		}
	};
	
	Command italicCommand = new Command() {
		public void execute() {
			boolean isItalic = widget.getColumn().getColumn().isItalic();
			widget.getColumn().getColumn().setItalic(!isItalic);

			if(!isItalic){
				itemItalic.setHTML("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
						Bpm_fwr.LBLW.Italic());
			}
			else {
				itemItalic.setHTML(Bpm_fwr.LBLW.Italic());
			}

			((ListWidget)widget.getColumn().getReportWidgetParent()).updateColumnContent(true);
		}
	};
	
	Command boldCommand = new Command() {
		public void execute() {
			boolean isBold = widget.getColumn().getColumn().isBold();
			widget.getColumn().getColumn().setBold(!isBold);

			if(!isBold){
				itemBold.setHTML("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
						Bpm_fwr.LBLW.Bold());
			}
			else {
				itemBold.setHTML(Bpm_fwr.LBLW.Bold());
			}

			((ListWidget)widget.getColumn().getReportWidgetParent()).updateColumnContent(true);
		}
	};
	
	Command underlineCommand = new Command() {
		public void execute() {
			boolean isUnderline = widget.getColumn().getColumn().isUnderline();
			widget.getColumn().getColumn().setUnderline(!isUnderline);
			
			if(!isUnderline){
				itemUnderline.setHTML("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
						Bpm_fwr.LBLW.Underline());
			}
			else {
				itemUnderline.setHTML(Bpm_fwr.LBLW.Underline());
			}

			((ListWidget)widget.getColumn().getReportWidgetParent()).updateColumnContent(true);
		}
	};
	
	Command ascendingCommand = new Command() {
		public void execute() {
			widget.getColumn().getColumn().setSorted(true);
			widget.getColumn().getColumn().setSortType(ASCENDING);

			ascending.setHTML("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.Ascending());
			descending.setHTML(Bpm_fwr.LBLW.Descending());
			none.setHTML(Bpm_fwr.LBLW.None());

			((ListWidget)widget.getColumn().getReportWidgetParent()).updateColumnContent(false);
		}
	};
		 
	Command descendingCommand = new Command() {
		public void execute() {
			widget.getColumn().getColumn().setSorted(true);
			widget.getColumn().getColumn().setSortType(DESCENDING);
			
			ascending.setHTML(Bpm_fwr.LBLW.Ascending());
			descending.setHTML("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.Descending());
			none.setHTML(Bpm_fwr.LBLW.None());

			((ListWidget)widget.getColumn().getReportWidgetParent()).updateColumnContent(false);
		}
	};
	
	Command noneSortCommand = new Command() {
		public void execute() {
			widget.getColumn().getColumn().setSorted(false);
			
			ascending.setHTML(Bpm_fwr.LBLW.Ascending());
			descending.setHTML(Bpm_fwr.LBLW.Descending());
			none.setHTML("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.None());
			
			((ListWidget)widget.getColumn().getReportWidgetParent()).updateColumnContent(false);
		}
	};
	
	Command sumCommand = new Command() {
		public void execute() {
			OptionsColumnPopupPanel.this.hide();
			
			AggregationDialogBox dial = new AggregationDialogBox(groups, widget.getColumn().getColumn(), defaultLanguage);
			dial.addFinishListener(finishListener);
			dial.center();
		}
	};
	
	Command showHeaderCommand = new Command() {
		public void execute() {
			widget.getColumn().getReportWidgetParent().setShowHeader(true);
			
			showHeader.setHTML("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.ShowHeader());
			hideHeader.setHTML(Bpm_fwr.LBLW.HideHeader());
		}
	};
	
	Command hideHeaderCommand = new Command() {
		public void execute() {
			widget.getColumn().getReportWidgetParent().setShowHeader(false);

			showHeader.setHTML(Bpm_fwr.LBLW.ShowHeader());
			hideHeader.setHTML("<img src=" + IMG_CHECK + " style=\"margin-right: 5px;\"/>" + 
					Bpm_fwr.LBLW.HideHeader());
		}
	};
	
	private FinishListener finishListener = new FinishListener() {
		
		@Override
		public void onFinish(Object result, Object source, String result1) {
			((ListWidget)widget.getColumn().getReportWidgetParent()).updateColumnContent(false);
			
		}
	};
}

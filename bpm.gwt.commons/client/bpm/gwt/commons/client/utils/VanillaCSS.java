package bpm.gwt.commons.client.utils;

import bpm.gwt.commons.client.I18N.LabelsConstants;

import com.google.gwt.resources.client.CssResource;

/**
 * This Interface reference all the Vanilla Theme
 * 
 * If you want to add a Theme, you just need to add the internazionalize name and the file name
 * in the CSS_FILE_NAMES array and then add the file in the war/css folder
 * 
 * To add a css class, add a static String and then call it wherever you want with VanillaCSS.STRING
 * and then add the css class in each Vanilla Theme File in the war/css folder
 * 
 * @author SVI
 *
 */

public interface VanillaCSS extends CssResource {
	public static final String[][] CSS_FILE_NAMES = {
		{LabelsConstants.lblCnst.DefaultTheme(), "vanilla_theme_default"}, 
		{LabelsConstants.lblCnst.GreenTheme(), "vanilla_theme_green"},
		{"Theme Perse", "perse_theme"}
	};

	/*
	 * All the body
	 * (use it especially for background)
	 */
	public static final String BODY_BACKGROUND = "bodyBackground";
	
	/*
	 * Head menu
	 * (use it especially for background)
	 */
	public static final String MENU_HEAD = "headMenu";
	
	/*
	 * LOGO
	 */
	public static final String LOGO = "logo";
	
	/*
	 * Head of a non selected Tab
	 * (use it especially for background and border left, top and right)
	 */
	public static final String TAB_HEAD_NO_SELECTED = "headTabNoSelected";
	
	/*
	 * Head of a selected Tab
	 * (use it especially for background and border left, top and right)
	 */
	public static final String TAB_HEAD_SELECTED = "headTabSelected";

	/*
	 * Button that represent a tab
	 * (use it especially for background color in hover)
	 */
	public static final String TAB_BTN = "btnTab";
	
	/*
	 * Button that represent a selected tab
	 * (use it especially for background color)
	 */
	public static final String TAB_BTN_SELECTED = "btnSelectedTab";
	
	/*
	 * Content of a tab with the toolbar included
	 * (use it especially for border color)
	 */
	public static final String TAB_PANEL_CONTENT_CONTAINER = "panelContentContainer";
	
	/*
	 * Content of a tab with the toolbar included, at a lower level of class panelContentContainer above
	 * (use it especially for border color)
	 */
	public static final String TAB = "tab";
	
	/*
	 * Toolbar of a tab
	 * (use it especially for background color)
	 */
	public static final String TAB_TOOLBAR = "tabToolbar";
	
	/*
	 * Content of a tab without the toolbar
	 * (use it especially for background)
	 */
	public static final String TAB_CONTENT = "tabContent";
	
	/*
	 * Row non selected
	 * (use it especially for background)
	 */
	public static final String DATA_GRID_ODD_ROW = "dataGridOddRow";
	
	/*
	 * Row Cell non selected
	 * (use it especially for background)
	 */
	public static final String DATA_GRID_ODD_ROW_CELL = "dataGridOddRowCell";
	
	/*
	 * Selected row for a DataGrid
	 * (use it especially for background and text color)
	 */
	public static final String DATA_GRID_SELECTED_ROW = "dataGridSelectedRow";
	
	/*
	 * Selected row cell for a DataGrid
	 * (use it especially for border. for more consistency use the same color as dataGridSelectedRow)
	 */
	public static final String DATA_GRID_SELECTED_ROW_CELL = "dataGridSelectedRowCell";
	
	/*
	 * Datagrid header
	 * (used for white-space to put header on multiple lines)
	 */
	public static final String DATA_GRID_HEADER = "dataGridHeader";
	
	/*
	 * Selected item for Table
	 * (use it especially for background color)
	 */
	public static final String ITEM_TABLE_FOCUS = "focusItemTableBackground";
	
	/*
	 * Selected tree item
	 * (use it especially for background color)
	 */
	public static final String TREE_ITEM_SELECTED = "treeItemSelected";
	
	/*
	 * Dialog text border used in the About and the Thanks Dialog
	 * (use it especially for border color for the text)
	 */
	public static final String ABOUT_DIALOG_TEXT_BORDER = "aboutDialogTextBorder";
	
	/*
	 * Welcome title from the Welcome Tab
	 */
	public static final String WELCOME_TITLE = "welcomeTitle";

	/*
	 * Right toolbar
	 * (use it especially for background)
	 */
	public static final String RIGHT_TOOLBAR = "rightToolbar";
	

	/*
	 * Icon Toolbar
	 * (use it especially for managing cursor type)
	 */
	public static final String ICON_CURSOR = "iconCursor";
	
	/*
	 * Bottom Panel
	 * (use it especially for managing background color)
	 */
	public static final String BOTTOM = "bottom";

	/*
	 * Popup Panel
	 * (use it especially for managing background color)
	 */
	public static final String POPUP_PANEL = "popupPanel";

	/*
	 * Navigation Panel
	 * (use it especially for managing background color)
	 */
	public static final String NAVIGATION_TOOLBAR = "navigationToolbar";

	/*
	 * Panel User
	 * (use it especially for managing text color)
	 */
	public static final String PANEL_USER = "panelUser";
	
	/*
	 * Panel User Bottom
	 * (use it especially for managing background color)
	 */
	public static final String PANEL_USER_BOTTOM = "panelUserBottom";

	/*
	 * Comment Border
	 * (use it especially for managing border color)
	 */
	public static final String COMMENT_BORDER = "commentBorder";


	/*
	 * Button selected
	 * (use it especially for managing bouton selected background color)
	 */
	public static final String BTN_SELECTED_BACKGROUND = "btnSelectedBackground";


	/*
	 * Button that represent a tab
	 * (use it especially for background color in hover)
	 */
	public static final String TAB_BTN_MAIN = "btnTabMain";
	
	/*
	 * Dialog
	 * (use it especially for border)
	 */
	public static final String DIALOG = "customDialog";
	
	/*
	 * Dialog
	 * (use it especially for top background)
	 */
	public static final String DIALOG_TOP = "customDialogTop";
	
	/*
	 * Dialog
	 * (use it especially for button hover)
	 */
	public static final String DIALOG_TOP_BUTTON = "customDialogTopButton";
	
	/*
	 * Properties for drag and drop (Parameters and Variables)
	 */
	public static final String TAB_SELECTED = "tabSelected";
	
	/*
	 * Properties for toolbar with a light background
	 */
	public static final String TOOLBAR_LIGHT = "toolbarLight";
	
	public static final String COMMONS_TEXTBOX = "commons-textbox";
	public static final String COMMONS_TEXTAREA = "commons-txtArea";
	public static final String COMMONS_LISTBOX = "commons-listbox";
	public static final String COMMONS_BUTTON = "commons-button";
	
	public static final String LEVEL_1 = "levelOne";
	public static final String LEVEL_2 = "levelTwo";
	public static final String LEVEL_3 = "levelThree";
	public static final String LEVEL_4 = "levelFour";
	
	public static final String IMG_BUTTON_GRID = "imgButtonGrid";
}
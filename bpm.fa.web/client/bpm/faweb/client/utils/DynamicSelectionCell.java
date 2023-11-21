package bpm.faweb.client.utils;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import bpm.faweb.client.dialog.SortingDialog;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class DynamicSelectionCell extends AbstractInputCell<String, String> {

    public TreeMap<Integer, List<String>> optionsMap = new TreeMap<Integer, List<String>>();
    interface Template extends SafeHtmlTemplates {
        @Template("<option value=\"{0}\">{0}</option>")
        SafeHtml deselected(String option);

        @Template("<option value=\"{0}\" selected=\"selected\">{0}</option>")
        SafeHtml selected(String option);
    }

    private static Template template;

    private TreeMap<Integer, HashMap<String, Integer>> indexForOption = new TreeMap<Integer, HashMap<String, Integer>>();

	private SortingDialog dialog;

    /**
     * Construct a new {@link SelectionCell} with the specified options.
     *
     * @param options the options in the cell
     */
    public DynamicSelectionCell(SortingDialog parent) {
        super("change");
        if (template == null) {
            template = GWT.create(Template.class);
        }
        this.dialog = parent;
    }
    
    public void clearOptions() {
    	optionsMap.clear();
    }

    public void addOption(List<String> newOps, int key){
        optionsMap.put(key, newOps);
        HashMap<String, Integer> localIndexForOption = new HashMap<String, Integer>();
        indexForOption.put(key, localIndexForOption);
        refreshIndexes();
    }

    public void removeOption(int index){        
        optionsMap.remove(index);
        refreshIndexes();
    }

    private void refreshIndexes(){
        int ind=0;
        for (List<String> options : optionsMap.values()){
            HashMap<String, Integer> localIndexForOption = new HashMap<String, Integer>();
            indexForOption.put(ind, localIndexForOption);
            int index = 0;
            for (String option : options) {
                localIndexForOption.put(option, index++);
            }
            ind++;
        }
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, String value,
            NativeEvent event, ValueUpdater<String> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        String type = event.getType();
        if ("change".equals(type)) {
            Object key = context.getKey();
            SelectElement select = parent.getFirstChild().cast();
            String newValue = optionsMap.get(context.getIndex()).get(select.getSelectedIndex());
            setViewData(key, newValue);
            finishEditing(parent, newValue, key, valueUpdater);
            if (valueUpdater != null) {
                valueUpdater.update(newValue);
            }
            
//            dialog.handleOptions();
        }
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        // Get the view data.
        Object key = context.getKey();
        String viewData = getViewData(key);
        if (viewData != null && viewData.equals(value)) {
            clearViewData(key);
            viewData = null;
        }

        int selectedIndex = getSelectedIndex(viewData == null ? value : viewData, context.getIndex());
        sb.appendHtmlConstant("<select tabindex=\"-1\">");
        int index = 0;
        try{
        for (String option : optionsMap.get(context.getIndex())) {
            if (index++ == selectedIndex) {
                sb.append(template.selected(option));
            } else {
                sb.append(template.deselected(option));
            }
        }
        }catch(Exception e){
            System.out.println("error");
        }
        sb.appendHtmlConstant("</select>");
    }

    private int getSelectedIndex(String value, int ind) {
        Integer index = indexForOption.get(ind).get(value);
        if (index == null) {
            return -1;
        }
        return index.intValue();
    }
} 

package bpm.fwr.client.widgets;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

public class LabelCell extends AbstractSafeHtmlCell<String> {

	public LabelCell() {
		this(SimpleSafeHtmlRenderer.getInstance());
	}

	public LabelCell(SafeHtmlRenderer<String> renderer) {
	  super(renderer, "click", "keydown");
	}
	
	@Override
	public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
			ValueUpdater<String> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
	    if ("click".equals(event.getType())) {
	        onEnterKeyDown(context, parent, value, event, valueUpdater);
	    }
	}

	@Override
	protected void onEnterKeyDown(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
	    if (valueUpdater != null) {
	    	valueUpdater.update(value);
	    }
	}

	@Override
	protected void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
	    if (value != null) {
	    	sb.appendHtmlConstant("<div style=\"background-color: #" + value.asString() + "\">");
	    	sb.append(value);
	    	sb.appendHtmlConstant("</div>");
	    }
	}
}
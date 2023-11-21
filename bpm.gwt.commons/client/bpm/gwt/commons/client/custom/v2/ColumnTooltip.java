package bpm.gwt.commons.client.custom.v2;

import bpm.gwt.commons.client.utils.GlobalCSS;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;

/**
 * Datagrid column use to display a tooltip when your mouse is over a cell
 *
 * @param <T>
 * @param <C>
 */
public abstract class ColumnTooltip<T, C> extends Column<T, C> {

	private ITooltipManager<T> tooltipManager;
	
	public ColumnTooltip(Cell<C> cell) {
		super(cell);
	}
	
	public ColumnTooltip(Cell<C> cell, ITooltipManager<T> tooltipManager) {
		super(cell);
		this.tooltipManager = tooltipManager;
	}
	
	@Override
	public void render(Context context, T object, SafeHtmlBuilder sb) {
		if (tooltipManager != null) {
			sb.append(TEMPLATES.startToolTip(tooltipManager.buildTooltip(object)));
			super.render(context, object, sb);
		    sb.append(TEMPLATES.endToolTip());
		}
		else {
			super.render(context, object, sb);
		}
	}

	@Override
	public abstract C getValue(T object);

	private static final Templates TEMPLATES = GWT.create(Templates.class);
	interface Templates extends SafeHtmlTemplates {

		@Template("<div title=\"{0}\" class=\"" + GlobalCSS.TOOLTIP_CELL_DIV + "\">")
		SafeHtml startToolTip(String toolTipText);

		@Template("</div>")
		SafeHtml endToolTip();

	}
	
	@Override
	public void onBrowserEvent(Context context, Element elem, T object, NativeEvent event) {
		// TODO Auto-generated method stub
		super.onBrowserEvent(context, elem, object, event);
	}
	
	public interface ITooltipManager<T> {
		
		public String buildTooltip(T object);
	}
}

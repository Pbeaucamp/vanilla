package bpm.vanillahub.web.client.dialogs;

import java.util.HashMap;
import java.util.Map;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.vanillahub.core.beans.activities.attributes.DataGouv;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.properties.creation.OpenDataActivityProperties;
import bpm.vanillahub.web.client.properties.creation.attribute.DataGouvProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class BuildDataGouvDialog extends AbstractDialogBox {

	public static final String PAGE_SIZE = "page_size";
	public static final String QUERY = "q";
	public static final String FORMAT = "format";
	public static final String SORT = "sort";
	public static final int PAGE_SIZE_DEFAULT = 20;

	private static DisplayReferenceDialogUiBinder uiBinder = GWT.create(DisplayReferenceDialogUiBinder.class);

	interface DisplayReferenceDialogUiBinder extends UiBinder<Widget, BuildDataGouvDialog> {
	}

	public enum Sort {
		NONE(0, "none"), VIEWS(1, "views"), CREATED(2, "created"), LAST_MODIFIED(3, "last_modified"), FOLLOWERS(4, "followers"), REUSES(5, "reuses"), TITLE(6, "title"), M_VIEWS(7, "-views"), M_CREATED(8, "-created"), M_LAST_MODIFIED(9, "-last_modified"), M_FOLLOWERS(10, "-followers"), M_REUSES(11, "-reuses"), M_TITLE(12, "-title");

		private static Map<Integer, Sort> map = new HashMap<Integer, Sort>();
		static {
			for (Sort actionType : Sort.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private int type;
		private String name;

		private Sort(int type, String name) {
			this.type = type;
			this.name = name;
		}

		public int getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		public static Sort valueOf(int actionType) {
			return map.get(actionType);
		}
	}

	@UiField
	TextHolderBox txtQuery, txtFormat, txtPageSize;

	@UiField
	ListBox lstSort;

	@UiField
	Button btnLoadDatasets;

	@UiField
	Label lblUrl;

	@UiField
	SimplePanel panelDatasets;

	private OpenDataActivityProperties panelParent;
	private DataGouvProperties dataGouvPanel;

	public BuildDataGouvDialog(OpenDataActivityProperties panelParent, DataGouv dataGouv, String openDataUrl) {
		super(Labels.lblCnst.DataGouvDefinition(), false, true);
		this.panelParent = panelParent;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsCommon.lblCnst.Confirmation(), confirmHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);

		String pageSize = null;
		String query = null;
		String format = null;
		String sortProp = null;
		if (openDataUrl != null && !openDataUrl.isEmpty()) {
			HashMap<String, String> props = ToolsGWT.parseUrl(openDataUrl);
			pageSize = props.get(PAGE_SIZE);
			query = props.get(QUERY);
			format = props.get(FORMAT);
			sortProp = props.get(SORT);
		}

		txtQuery.setText(query != null && !query.isEmpty() ? query : "");
		txtFormat.setText(format != null && !format.isEmpty() ? format : "");
		txtPageSize.setText(pageSize != null && !pageSize.isEmpty() ? pageSize : PAGE_SIZE_DEFAULT + "");

		int selectedIndex = 0;
		int i = 0;
		for (Sort sort : Sort.values()) {
			lstSort.addItem(sort.getName(), sort.getType() + "");
			if (sortProp != null && sort.getName().equals(sortProp)) {
				selectedIndex = i;
			}
			i++;
		}
		lstSort.setSelectedIndex(selectedIndex);

		this.dataGouvPanel = new DataGouvProperties(this, dataGouv, openDataUrl);
		panelDatasets.setWidget(dataGouvPanel);
	}

	@UiHandler("btnLoadDatasets")
	public void onLoadReference(ClickEvent event) {
		String openDataUrl = buildDataUrl();
		dataGouvPanel.refresh(openDataUrl, null, true);
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String openDataUrl = buildDataUrl();
			DataGouv dataGouv = dataGouvPanel.getDataGouv();
			panelParent.refreshGeneratedUrl(openDataUrl, dataGouv);
			hide();
		}
	};

	private Sort getSort() {
		if (lstSort.getSelectedIndex() >= 0) {
			int type = Integer.parseInt(lstSort.getValue(lstSort.getSelectedIndex()));
			return Sort.valueOf(type);
		}
		return null;
	}

	private int getPageSize() {
		String pageSize = txtPageSize.getText();
		try {
			return Integer.parseInt(pageSize);
		} catch (Exception e) {
			txtPageSize.setText(PAGE_SIZE_DEFAULT + "");
			return PAGE_SIZE_DEFAULT;
		}
	}

	private String getQuery() {
		String query = txtQuery.getText();
		return query != null && !query.isEmpty() ? query : null;
	}

	private String getFormat() {
		String format = txtFormat.getText();
		return format != null && !format.isEmpty() ? format : null;
	}

	public String buildDataUrl() {
		String query = getQuery();
		String format = getFormat();
		int pageSize = getPageSize();
		Sort sort = getSort();

		StringBuilder builder = new StringBuilder(DataGouv.DATA_GOUV_API_URL);
		builder.append("?" + PAGE_SIZE + "=" + pageSize);
		if (query != null) {
			builder.append("&" + QUERY + "=" + query);
		}
		if (format != null) {
			builder.append("&" + FORMAT + "=" + format);
		}
		if (sort != null && sort != Sort.NONE) {
			builder.append("&" + SORT + "=" + sort.getName());
		}
		return builder.toString();
	}
}

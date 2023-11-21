package bpm.vanilla.api.runtime.dto;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fa.api.olap.OLAPChart;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.fa.api.utils.parse.DigesterCubeView;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ViewItem extends RepositoryComponent {
	private String xml;
	private String base64img;
	private OLAPChart chart;

	public ViewItem(RepositoryItem it, String favxml) throws Exception {
		super(it.getId(), it.getName(), false,"fav");
		loadView(favxml);
	}

	public String getXml() {
		return xml;
	}

	public String getBase64img() {
		return base64img;
	}

	public OLAPChart getChart() {
		return chart;
	}

	public void loadView(String favxml) throws Exception {
		Document document = DocumentHelper.parseText(favxml);
		Element root = document.getRootElement();
		String viewxml = root.element("view").asXML();

		DigesterCubeView dig = new DigesterCubeView(viewxml);
		RepositoryCubeView repcubeview = dig.getCubeView();
		base64img = repcubeview.getBase64img();
		chart = repcubeview.getChart();
		
		Document docView = DocumentHelper.parseText(viewxml);
		Element rootView = docView.getRootElement();
		rootView.remove(rootView.element("base64img"));
		rootView.remove(rootView.element("chart"));
		xml = rootView.asXML();
	}

}

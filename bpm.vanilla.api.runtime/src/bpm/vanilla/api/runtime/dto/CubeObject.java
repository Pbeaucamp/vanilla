package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPResult;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class CubeObject {

	private String cubeName;
	private FasdItem currentFASD;
	private ViewItem currentView;
	private List<ViewItem> views;
	private CubeMDX mdx;
	private List<CubeTreeComponent> treeview;
	private List<List<CubeCell>> olapRawResult;
	private CubeResult olapResult;
	private List<CubeParameter> parameters;
	private boolean active;
	private boolean showEmpty;
	private String xml;

	public CubeObject(RepositoryItem fasdItem, String fasdxml, String cubeName, HashMap<RepositoryItem, String> viewsxml, RepositoryItem viewItem, String favxml, OLAPCube cube) throws Exception {
		this.cubeName = cubeName;
		currentFASD = new FasdItem(fasdItem, fasdxml);

		if(viewItem != null) {
			currentView = new ViewItem(viewItem, favxml);
		}
		else {
			currentView = null;
		}

		if(viewsxml != null) {
			loadViewItems(viewsxml);
		}
		else {
			views = null;
		}
		

		mdx = new CubeMDX(cube);

		if (cube.getView() != null) {
			xml = cube.getView().getXML();
		}
		else {
			xml = null;
		}

		loadCubeParameters(cube);
		loadCubeTreeview(cube);

		loadCubeRawResult(cube);
		olapResult = new CubeResult(olapRawResult);

		active = cube.getStateActive();
		showEmpty = cube.getShowEmpty();

	}

	public String getCubeName() {
		return cubeName;
	}

	public FasdItem getCurrentFASD() {
		return currentFASD;
	}

	public ViewItem getCurrentView() {
		return currentView;
	}

	public List<ViewItem> getViews() {
		return views;
	}

	public CubeMDX getMdx() {
		return mdx;
	}

	public List<CubeTreeComponent> getTreeview() {
		return treeview;
	}

	public List<List<CubeCell>> getOlapRawResult() {
		return olapRawResult;
	}

	public CubeResult getOlapResult() {
		return olapResult;
	}

	public List<CubeParameter> getParameters() {
		return parameters;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isShowEmpty() {
		return showEmpty;
	}

	public String getXml() {
		return xml;
	}

	public void loadViewItems(HashMap<RepositoryItem, String> viewsxml) throws Exception {
		this.views = new ArrayList<>();

		for (RepositoryItem viewItem : viewsxml.keySet()) {
			this.views.add(new ViewItem(viewItem, viewsxml.get(viewItem)));
		}
		this.views.sort((v1, v2) -> Integer.compare(v1.getId(), v2.getId()));

	}
	

	public void loadCubeParameters(OLAPCube cube) {
		parameters = new ArrayList<>();

		for (Parameter p : cube.getParameters()) {
			parameters.add(new CubeParameter(cube, p));
		}

	}

	public void loadCubeTreeview(OLAPCube cube) {
		treeview = new ArrayList<>();

		treeview.add(new CubeDimensionTree(cube));
		treeview.add(new CubeMeasureTree(cube));
	}

	public void loadCubeRawResult(OLAPCube cube) throws Exception {
		olapRawResult = new ArrayList<>();

		OLAPResult res = cube.getLastResult();
		if (res != null) {
			ArrayList<ArrayList<Item>> rawContent = res.getRaw();

			for (ArrayList<Item> row : rawContent) {
				List<CubeCell> cellArray = new ArrayList<>();

				for (Item it : row) {
					if (it instanceof ItemElement) {
						int x = rawContent.indexOf(row);
						int y = row.indexOf(it);
						boolean hasMember = checkChildMembers(rawContent,x,y);
						cellArray.add(new CubeElementCell(cube, (ItemElement) it, hasMember));
					}
					else if (it instanceof ItemValue) {
						cellArray.add(new CubeValueCell((ItemValue) it));
					}
					else {
						cellArray.add(new CubeNullCell(it));
					}
				}

				olapRawResult.add(cellArray);
			}
		}

	}
	
	private boolean checkChildMembers(ArrayList<ArrayList<Item>> rawContent,int posx,int posy) {
		if(rawContent.get(posx).get(posy) instanceof ItemElement) {
			ItemElement itElem = (ItemElement) rawContent.get(posx).get(posy);
			if(itElem.isDrilled()) {
				if((itElem.isCol()) && (rawContent.get(posx+1).get(posy) instanceof ItemNull)) {
					int childposy = posy + 1;
					while(childposy < rawContent.get(posx + 1).size()) {
						if(rawContent.get(posx+1).get(childposy) instanceof ItemElement) {
							return true;
						}
						childposy++;
					}
				}
				else if((itElem.isRow()) && (rawContent.get(posx).get(posy+1) instanceof ItemNull)) {
					int childposx = posx + 1;
					while(childposx < rawContent.size()) {
						if(rawContent.get(childposx).get(posy+1) instanceof ItemElement) {
							return true;
						}
						childposx++;
					}
				}
			}
		}
		
		return false;
	}
	

}

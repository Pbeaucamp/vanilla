package bpm.faweb.shared.infoscube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GridCube implements IsSerializable {

	public Set setOfStrings;
	public Map mapOfStringToString;

	private List<ArrayList<ItemCube>> items;
	private List<ItemCube> colonne = new ArrayList<ItemCube>();

	private boolean zero;
	private boolean properties;

	private int nbMeasures;

	private List<Calcul> calculs = new ArrayList<Calcul>();
	private List<Topx> topx = new ArrayList<Topx>();

	private HashMap<String, String> personalNames = new HashMap<String, String>();
	private HashMap<String, Boolean> percentMeasures = new HashMap<String, Boolean>();

	private List<String> queryRows = new ArrayList<String>();
	private List<String> queryCols = new ArrayList<String>();
	private boolean dateFuntionCalculated;

	public GridCube() {
		super();
	}

	public boolean isZero() {
		return zero;
	}

	public void setZero(boolean zero) {
		this.zero = zero;
	}

	public boolean isProperties() {
		return properties;
	}

	public void setProperties(boolean properties) {
		this.properties = properties;
	}

	public List<ArrayList<ItemCube>> getItems() {
		return items;
	}

	public void setItems(List<ArrayList<ItemCube>> items) {
		this.items = items;
	}

	public ArrayList<ItemCube> getLigne(int i) {
		return items.get(i);
	}

	public List<ItemCube> getCol(int j) {
		colonne = new ArrayList<ItemCube>();

		for (int i = 0; i < this.items.size(); i++) {
			List<ItemCube> currLigne = this.items.get(i);
			this.colonne.add(currLigne.get(j));
		}
		return colonne;
	}

	public int getNbOfCol() {
		return this.getLigne(0).size();
	}

	public int getNbOfRow() {
		return this.getItems().size();
	}

	public ItemCube getIJ(int i, int j) {
		return items.get(i).get(j);
	}

	public List<Calcul> getCalculs() {
		return calculs;
	}

	public void setCalculs(List<Calcul> calculs) {
		this.calculs = calculs;
	}

	public void setNbMeasures(int nbMeasures) {
		this.nbMeasures = nbMeasures;
	}

	public int getNbMeasures() {
		return nbMeasures;
	}

	public void setTopx(List<Topx> topx) {
		this.topx = topx;
	}

	public List<Topx> getTopx() {
		return topx;
	}

	public void setPersonalNames(HashMap<String, String> personalNames) {
		this.personalNames = personalNames;
	}

	public HashMap<String, String> getPersonalNames() {
		return personalNames;
	}

	public void setPercentMeasures(HashMap<String, Boolean> percentMeasures) {
		this.percentMeasures = percentMeasures;
	}

	public HashMap<String, Boolean> getPercentMeasures() {
		return percentMeasures;
	}

	public List<String> getQueryRows() {
		return queryRows;
	}

	public void setQueryRows(List<String> queryRows) {
		this.queryRows = queryRows;
	}

	public List<String> getQueryCols() {
		return queryCols;
	}

	public void setQueryCols(List<String> queryCols) {
		this.queryCols = queryCols;
	}

	public void setDateFunctionCalculated(boolean b) {
		this.dateFuntionCalculated = b;
	}

	public boolean isDateFuntionCalculated() {
		return dateFuntionCalculated;
	}

}

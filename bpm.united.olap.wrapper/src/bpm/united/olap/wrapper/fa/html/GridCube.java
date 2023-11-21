package bpm.united.olap.wrapper.fa.html;

import java.util.ArrayList;
import java.util.List;

public class GridCube {

		private List items;
		private List ligne = new ArrayList();
		private List colonne = new ArrayList();
			
		private boolean zero;
		private boolean properties;  
		
		private int nbMeasures;
		
		private int iFirst;
		private int jFirst;
	
		public GridCube(){
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


		public List getItems() {
			return items;
		}
	
		public void setItems(List items) {
			this.items = items;
		}
		
		public List getLigne(int i){
			return (List) items.get(i);
		}
		
		public List getCol(int j){
			colonne = new ArrayList();
			
			for(int i = 0; i < this.items.size(); i++){
				List currLigne = (List) this.items.get(i);
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
		
		public Object getIJ(int i, int j) {
			return ((List) items.get(i)).get(j);
		}


		public void setNbMeasures(int nbMeasures) {
			this.nbMeasures = nbMeasures;
		}


		public int getNbMeasures() {
			return nbMeasures;
		}


		public void setiFirst(int iFirst) {
			this.iFirst = iFirst;
		}


		public int getIFirst() {
			return iFirst;
		}


		public void setjFirst(int jFirst) {
			this.jFirst = jFirst;
		}


		public int getJFirst() {
			return jFirst;
		}
}

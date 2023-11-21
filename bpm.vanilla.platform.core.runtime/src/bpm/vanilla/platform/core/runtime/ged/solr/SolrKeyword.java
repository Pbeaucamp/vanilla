package bpm.vanilla.platform.core.runtime.ged.solr;


public class SolrKeyword {

	private String name;
	private int tf;
	private int df;
	private double idf, tfn, proba, proban;
	

	public SolrKeyword(String name, int tf, int df, long totalDocs) {
		super();
		this.name = name;
		this.tf = tf;
		this.df = df;
		this.idf = Math.log((double)(totalDocs+1)/(double)(df+1));
		this.proba = tf*idf;
		this.tfn = 0.0;
		this.proban = 0.0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTf() {
		return tf;
	}

	public void setTf(int tf) {
		this.tf = tf;
	}

	public int getDf() {
		return df;
	}

	public void setDf(int df) {
		this.df = df;
	}

	public double getProba() {
		return proba;
	}

	public void setProba(double proba) {
		this.proba = proba;
	}
	
	public double getIdf() {
		return idf;
	}

	public void setIdf(double idf) {
		this.idf = idf;
	}

	public double getProban() {
		return proban;
	}

	public void setProban(double proban) {
		this.proban = proban;
	}

	public double getTfn() {
		return tfn;
	}

	public void setTfn(double tfn) {
		this.tfn = tfn;
	}
	
	public void normalizeTf(int totalWords){
		tfn = (double)tf/totalWords;
		proban = idf * tfn;
	}
}

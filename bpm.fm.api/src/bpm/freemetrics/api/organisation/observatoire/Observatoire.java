package bpm.freemetrics.api.organisation.observatoire;

public class Observatoire implements Comparable<Observatoire> {

	int id;
	String name;
	
	public Observatoire() {
		super();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(Observatoire o) {
		return this.name.compareTo(o.getName());
	}

	
	
}

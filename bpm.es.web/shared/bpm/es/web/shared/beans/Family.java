package bpm.es.web.shared.beans;

import java.util.List;

public class Family {

	private String id;
	private String name;

	private Person father;
	private Person mother;

	private Person accompanist;

	private List<Person> childs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Person getFather() {
		return father;
	}

	public void setFather(Person father) {
		this.father = father;
	}

	public Person getMother() {
		return mother;
	}

	public void setMother(Person mother) {
		this.mother = mother;
	}

	public Person getAccompanist() {
		return accompanist;
	}

	public void setAccompanist(Person accompanist) {
		this.accompanist = accompanist;
	}

	public List<Person> getChilds() {
		return childs;
	}

	public void setChilds(List<Person> childs) {
		this.childs = childs;
	}
}

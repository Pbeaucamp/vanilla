package bpm.smart.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "users_projects_shares")
public class UsersProjectsShares implements Serializable{


	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "idUser")
	private int idUser;
	
	@Column (name = "idProject")
	private int idProject;
	
	
	public UsersProjectsShares() {
		super();
	}
	
	public UsersProjectsShares(int idUser, int idProject) {
		super();
		this.idUser = idUser;
		this.idProject = idProject;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public int getIdProject() {
		return idProject;
	}

	public void setIdProject(int idProject) {
		this.idProject = idProject;
	}

		
}

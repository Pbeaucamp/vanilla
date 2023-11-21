package bpm.freemetrics.api.security;

import java.util.Date;

import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.utils.Tools;


public class FmUser implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields    
    private int    id;
    private String login;
    private String name;
    private String password;
    private String surname;
    private String function;
    private String telephone;
    private String cellular,address;
    private String skypeName;
    private String skypeNumber;
    private String businessMail;
    private String privateMail;
    private Date creationDate;
    private String image;
    private String custom1;
    
    private String comment;
    private String typeOrganisation;
    private int organisationId;
    
    /**
     * ONLY for specify a temporary group when user can be only in one group
     */
    private Group dummyGroup;


   /** default constructor */
   public FmUser() {
	   super();
   }


	public int getId() {
		return id;
	}
	
	
	public void setId(int id) {
		this.id = id;
	}
		
	public String getName() {
		return Tools.isValid(name) ? name : login;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getPassword() {
		return password;
	}
	
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public String getSurname() {
		return surname;
	}
	
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	
	public String getFunction() {
		return function;
	}
	
	
	public void setFunction(String function) {
		this.function = function;
	}
	
	
	public String getTelephone() {
		return telephone;
	}
	
	
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	
	public String getCellular() {
		return cellular;
	}
	
	
	public void setCellular(String cellular) {
		this.cellular = cellular;
	}
	
	
	public String getSkypeName() {
		return skypeName;
	}
	
	
	public void setSkypeName(String skypeName) {
		this.skypeName = skypeName;
	}
	
	
	public String getSkypeNumber() {
		return skypeNumber;
	}
	
	
	public void setSkypeNumber(String skypeNumber) {
		this.skypeNumber = skypeNumber;
	}
	
	
	public String getBusinessMail() {
		return businessMail;
	}
	
	
	public void setBusinessMail(String businessMail) {
		this.businessMail = businessMail;
	}
	
	
	public String getPrivateMail() {
		return privateMail;
	}
	
	
	public void setPrivateMail(String privateMail) {
		this.privateMail = privateMail;
	}
	
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	
	public String getImage() {
		return image;
	}
	
	
	public void setImage(String image) {
		this.image = image;
	}
	
	
	public String getCustom1() {
		return custom1;
	}
	
	
	public void setCustom1(String custom1) {
		this.custom1 = custom1;
	}
	
	
	public String getComment() {
		return comment;
	}
	
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
	
	public String getTypeOrganisation() {
		return typeOrganisation;
	}
	
	
	public void setTypeOrganisation(String typeOrganisation) {
		this.typeOrganisation = typeOrganisation;
	}


	public int getOrganisationId() {
		return organisationId;
	}


	public void setOrganisationId(int organisationId) {
		this.organisationId = organisationId;
	}


	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}


	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}


	/**
	 * @return the login
	 */
	public String getLogin() {
		return Tools.isValid(login) ? login : name;
	}


	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}


	public Group getDummyGroup() {
		return dummyGroup;
	}


	public void setDummyGroup(Group dummyGroup) {
		this.dummyGroup = dummyGroup;
	}



}

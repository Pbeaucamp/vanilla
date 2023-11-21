package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

	private static final long serialVersionUID = 5972746089990422673L;

	private Integer id = 0;
	private String login = "";
	private String name = "";
	private String password = "";
	private String surname = "";
	private String function = "";
	private String telephone = "";
	private String cellular = "";
	private String skypeName = "";
	private String skypeNumber = "";
	private String businessMail = "";
	private String privateMail = "";
	private Date creation = new Date();
	private String image = "";
	private Integer fmUserId = -1;

	private Integer passwordReset = 0;
	private Integer passwordChange = 0;

	/**
	 * Use to indicate if the password have to be encrypted before updating the
	 * DB
	 */
	private boolean passwordEncrypted = false;

	/*
	 * SCD Fields
	 */
	private Integer version;
	private Date dateStartActif;
	private Date dateStopActif;
	private Integer actifField;

	private Boolean superUser = false;
	private String vanillaTheme;

//	private String codeBackupPassword;
	
	private String aklaboxLogin;
	private String aklaboxPassword;
	
	private Date datePasswordModification;
	private int nbDayPasswordValidity;
	
	private String locale;
	
	private boolean widget;

	public User() {
	}
	
	public Integer getFmUserId() {
		return fmUserId;
	}

	public void setFmUserId(Integer fmUserId) {
		this.fmUserId = fmUserId;
	}

	public void setFmUserId(String fmUserId) {
		try {
			this.fmUserId = Integer.parseInt(fmUserId);
		} catch (NumberFormatException e) {

		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setId(String id) {
		try {
			this.id = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return the name of the user NOT THE LOGIN !
	 * If you use this as login I might come at your place at night
	 * and kill you in your sleep.
	 * I'm watching you !!!
	 */
	public String getName() {
		return name;
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

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public void setCreation(String date) {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// if (date != null) {
		// try {
		// this.creation = sdf.parse(date);
		// } catch (ParseException e) {
		//
		// }
		// }
		this.creation = new Date();
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Boolean isSuperUser() {
		return superUser;
	}

	public void setSuperUser(Boolean superUser) {
		this.superUser = superUser;
	}

	public void setSuperUser(String superUser) {
		this.superUser = Boolean.parseBoolean(superUser);
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getDateStartActif() {
		return dateStartActif;
	}

	public void setDateStartActif(Date dateStartActif) {
		this.dateStartActif = dateStartActif;
	}

	public Date getDateStopActif() {
		return dateStopActif;
	}

	public void setDateStopActif(Date dateStopActif) {
		this.dateStopActif = dateStopActif;
	}

	public Integer getActifField() {
		return actifField;
	}

	public void setActifField(Integer actifField) {
		this.actifField = actifField;
	}

	public boolean isPasswordEncrypted() {
		return passwordEncrypted;
	}

	public void setPasswordEncrypted(boolean passwordEncrypted) {
		this.passwordEncrypted = passwordEncrypted;
	}

	public void setPasswordEncrypted(String passwordEncrypted) {
		this.passwordEncrypted = new Boolean(passwordEncrypted);
	}

	public int getPasswordChange() {
		return passwordChange;
	}

	public void setPasswordChangeString(String passwordChange) {
		this.passwordChange = new Integer(passwordChange);
	}

	public int getPasswordReset() {
		return passwordReset;
	}

	public void setPasswordResetString(String passwordReset) {
		try {
			this.passwordReset = new Integer(passwordReset);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPasswordReset(Integer passwordReset) {
		this.passwordReset = passwordReset;
	}

	public void setPasswordChange(Integer passwordChange) {
		this.passwordChange = passwordChange;
	}

	public void setVanillaTheme(String vanillaTheme) {
		this.vanillaTheme = vanillaTheme;
	}

	public String getVanillaTheme() {
		return vanillaTheme;
	}

//	public void setCodeBackupPassword(String codeBackupPassword) {
//		this.codeBackupPassword = codeBackupPassword;
//	}
//
//	public String getCodeBackupPassword() {
//		return codeBackupPassword;
//	}

	public String getAklaboxLogin() {
		return aklaboxLogin;
	}

	public void setAklaboxLogin(String aklaboxLogin) {
		this.aklaboxLogin = aklaboxLogin;
	}

	public String getAklaboxPassword() {
		return aklaboxPassword;
	}

	public void setAklaboxPassword(String aklaboxPassword) {
		this.aklaboxPassword = aklaboxPassword;
	}

	public Date getDatePasswordModification() {
		return datePasswordModification;
	}

	public void setDatePasswordModification(Date datePasswordModification) {
		this.datePasswordModification = datePasswordModification;
	}

	public int getNbDayPasswordValidity() {
		return nbDayPasswordValidity;
	}

	public void setNbDayPasswordValidity(int nbDayPasswordValidity) {
		this.nbDayPasswordValidity = nbDayPasswordValidity;
	}
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Override
	public String toString() {
		return name != null && !name.isEmpty() ? name : login;
	}

	public boolean isWidget() {
		return widget;
	}

	public void setWidget(boolean isWidget) {
		this.widget = isWidget;
	}
}

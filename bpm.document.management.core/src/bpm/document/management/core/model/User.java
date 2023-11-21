package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class User implements IAuthor, Serializable {

	private static final long serialVersionUID = 1L;

	private int userId = 0;
	private String verificationCode;
	private String firstName = "";
	private String middleName = "";
	private String lastName = "";
	private String company = "";
	private int countryId = 1;
	private int cityId = 1;
	private String email = "";
	private String password = "";
	private String phone = "";
	private String mobile = "";
	private String fax = "";
	private String address = "";
	private boolean superUser = false;
	private String userType = "";
	private boolean userValidator = false;
	private Integer workSize;
	private String profilePic = "webapps/aklabox_files/images/ic_profile_pic.png";
	private int selectedTheme = 0;
	private boolean notifiedByPlatform = true;
	private boolean notifiedByEmail = true;
	private int taskId = 0;
	private boolean noAccess = false;
	private boolean readAccess = false;
	private boolean writeAccess = false;
	private boolean selected = false;
	private String chatStatus = "chatOffline";
	private boolean encrypt = false;
	private String encryptPassword = "";
	private Date creationDate = new Date();
	private String workspaceBackground = "";
	private boolean startUp = true;
	private int storageIncreaseLevel = 0;

	private String currentSessionId;

	private Delegation activeDelegation;

	private Date dateLastNotificationCheck;
	private int notificationCheckingDelay = 0;
	private String ldapId;
	private String cocktailId;
	private String stamp;
	
	private String sessionId;

	public User() {
	};

	public void log(User user) {
	}

	// Registration
	public void setFirstName(String FirstName) {
		this.firstName = FirstName;
	}

	public void setLastName(String LastName) {
		this.lastName = LastName;
	}

	public void setPassword(String Password) {
		this.password = Password;
	}

	public void setPhone(String Phone) {
		this.phone = Phone;
	}

	public void setEmail(String Email) {
		this.email = Email;
	}

	public String getFirstName() {
		if (firstName != null && !firstName.isEmpty()) {
			return firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
		}
		return firstName;
	}

	public String getLastName() {
		try {
			if(lastName != null && lastName.isEmpty()) {
				return lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
			}
		} catch (Exception e) {
		}
		return lastName;
	}

	public String getEmail() {
		if(email != null) {
			email.toLowerCase();
		}
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getPhone() {
		return phone;
	}

	public boolean getUserValidator() {
		return userValidator;
	}

	public void setUserValidator(boolean userValidator) {
		this.userValidator = userValidator;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setCurrentSessionId(String currentSessionId) {
		this.currentSessionId = currentSessionId;
	}

	public String getCurrentSessionId() {
		return currentSessionId;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean getSuperUser() {
		return superUser;
	}

	public void setSuperUser(boolean superUser) {
		this.superUser = superUser;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public String getProfilePic() {
		//TODO change that if changing the profile pic works
		//I put that here because it makes it impossible to list the user if the pic is null
		if(profilePic == null || profilePic.isEmpty()) {
			return "webapps/aklabox_files/images/ic_profile_pic.png";
		}
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public int getSelectedTheme() {
		return selectedTheme;
	}

	public void setSelectedTheme(int selectedTheme) {
		this.selectedTheme = selectedTheme;
	}

	public boolean isNotifiedByPlatform() {
		return notifiedByPlatform;
	}

	public void setNotifiedByPlatform(boolean notifiedByPlatform) {
		this.notifiedByPlatform = notifiedByPlatform;
	}

	public boolean isNotifiedByEmail() {
		return notifiedByEmail;
	}

	public void setNotifiedByEmail(boolean notifiedByEmail) {
		this.notifiedByEmail = notifiedByEmail;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public boolean isNoAccess() {
		return noAccess;
	}

	public void setNoAccess(boolean noAccess) {
		this.noAccess = noAccess;
	}

	public boolean isReadAccess() {
		return readAccess;
	}

	public void setReadAccess(boolean readAccess) {
		this.readAccess = readAccess;
	}

	public boolean isWriteAccess() {
		return writeAccess;
	}

	public void setWriteAccess(boolean writeAccess) {
		this.writeAccess = writeAccess;
	}

	public String getChatStatus() {
		return chatStatus;
	}

	public void setChatStatus(String chatStatus) {
		this.chatStatus = chatStatus;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	public String getEncryptPassword() {
		return encryptPassword;
	}

	public void setEncryptPassword(String encryptPassword) {
		this.encryptPassword = encryptPassword;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getWorkspaceBackground() {
		return workspaceBackground;
	}

	public void setWorkspaceBackground(String workspaceBackground) {
		this.workspaceBackground = workspaceBackground;
	}

	public Integer getWorkSize() {
		return workSize;
	}

	public void setWorkSize(Integer workSize) {
		this.workSize = workSize;
	}

	public boolean isStartUp() {
		return startUp;
	}

	public void setStartUp(boolean startUp) {
		this.startUp = startUp;
	}

	public int getStorageIncreaseLevel() {
		return storageIncreaseLevel;
	}

	public void setStorageIncreaseLevel(int storageIncreaseLevel) {
		this.storageIncreaseLevel = storageIncreaseLevel;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			return userId == ((User) obj).getUserId();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return userId;
	}

	public void setActiveDelegation(Delegation d) {
		this.activeDelegation = d;
	}

	public Delegation getActiveDelegation() {
		return this.activeDelegation;
	}
	
	public Date getDateLastNotificationCheck() {
		return dateLastNotificationCheck;
	}
	
	public void setDateLastNotificationCheck(Date dateLastNotificationCheck) {
		this.dateLastNotificationCheck = dateLastNotificationCheck;
	}
	
	public int getNotificationCheckingDelay() {
		return notificationCheckingDelay;
	}
	
	public void setNotificationCheckingDelay(int notificationCheckingDelay) {
		this.notificationCheckingDelay = notificationCheckingDelay;
	}
	
	@Override
	public String toString() {
		return email;
	}

	public String getLdapId() {
		return ldapId;
	}

	public void setLdapId(String ldapId) {
		this.ldapId = ldapId;
	}

	public String getCocktailId() {
		return cocktailId;
	}

	public void setCocktailId(String cocktailId) {
		this.cocktailId = cocktailId;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	
}

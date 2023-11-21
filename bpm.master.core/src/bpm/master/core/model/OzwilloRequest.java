package bpm.master.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "vdm_master_ozwillo_request")
public class OzwilloRequest implements Serializable {

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;
	
	@Column(name = "instance_id")
	private String instance_id;
	
	@Column(name = "client_id")
	private String client_id;
	
	@Column(name = "client_secret")
	private String client_secret;
	
	@Column(name = "instance_registration_uri")
	private String instance_registration_uri;
	
	@Column(name = "aklabox_instance_id")
	private Integer aklaboxInstanceId;
	
	@Transient
	private User user;
	
	@Transient
	private Organization organization;
	
	@Transient
	private AuthorizationGrant authorization_grant;

	@Transient
	private InstanceIdentifier aklaboxInstance;
	
	public InstanceIdentifier getAklaboxInstance() {
		return aklaboxInstance;
	}

	public void setAklaboxInstance(InstanceIdentifier aklaboxInstance) {
		this.aklaboxInstance = aklaboxInstance;
	}

	

	public Integer getAklaboxInstanceId() {
		return aklaboxInstanceId;
	}

	public void setAklaboxInstanceId(Integer aklaboxInstanceId) {
		this.aklaboxInstanceId = aklaboxInstanceId;
	}

	public String getInstance_id() {
		return instance_id;
	}

	public void setInstance_id(String instance_id) {
		this.instance_id = instance_id;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}

	public String getInstance_registration_uri() {
		return instance_registration_uri;
	}

	public void setInstance_registration_uri(String instance_registration_uri) {
		this.instance_registration_uri = instance_registration_uri;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public AuthorizationGrant getAuthorization_grant() {
		return authorization_grant;
	}

	public void setAuthorization_grant(AuthorizationGrant authorization_grant) {
		this.authorization_grant = authorization_grant;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Entity
	@Table(name = "vdm_master_ozwillo_user")
	public static class User implements Serializable{
		@Column(name = "requestId")
		private int requestId;
		@Id
		@GeneratedValue(generator = "native")
	    @GenericGenerator(name = "native", strategy = "native")
		@Column(name = "user_id")
		private int user_id;
		@Column(name = "id")
		private String id;
		@Column(name = "user_name")
		private String name;

		public int getUser_id() {
			return user_id;
		}

		public void setUser_id(int user_id) {
			this.user_id = user_id;
		}

		public int getRequestId() {
			return requestId;
		}

		public void setRequestId(int requestId) {
			this.requestId = requestId;
		}

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

	}

	@Entity
	@Table(name = "vdm_master_ozwillo_organization")
	public static class Organization implements Serializable{
		@Column(name = "requestId")
		private int requestId;
		@Id
		@GeneratedValue(generator = "native")
	    @GenericGenerator(name = "native", strategy = "native")
		@Column(name = "org_id")
		private int org_id;
		@Column(name = "id")
		private String id;
		@Column(name = "org_name")
		private String name;
		@Column(name = "org_type")
		private String type;
		@Column(name = "dc_id")
		private String dc_id;
		
		public int getOrg_id() {
			return org_id;
		}

		public void setOrg_id(int org_id) {
			this.org_id = org_id;
		}

		public int getRequestId() {
			return requestId;
		}

		public void setRequestId(int requestId) {
			this.requestId = requestId;
		}

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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getDc_id() {
			return dc_id;
		}

		public void setDc_id(String dc_id) {
			this.dc_id = dc_id;
		}

	}

	@Entity
	@Table(name = "vdm_master_ozwillo_grant")
	public static class AuthorizationGrant implements Serializable{
		@Column(name = "requestId")
		private int requestId;
		@Id
		@GeneratedValue(generator = "native")
	    @GenericGenerator(name = "native", strategy = "native")
		@Column(name = "id")
		private int id;
		@Column(name = "grant_type")
		private String grant_type;
		@Column(name = "assertion")
		private String assertion;
		@Column(name = "scope")
		private String scope;

		public int getRequestId() {
			return requestId;
		}

		public void setRequestId(int requestId) {
			this.requestId = requestId;
		}
		
		public String getGrant_type() {
			return grant_type;
		}

		public void setGrant_type(String grant_type) {
			this.grant_type = grant_type;
		}

		public String getAssertion() {
			return assertion;
		}

		public void setAssertion(String assertion) {
			this.assertion = assertion;
		}

		public String getScope() {
			return scope;
		}

		public void setScope(String scope) {
			this.scope = scope;
		}

	}
}

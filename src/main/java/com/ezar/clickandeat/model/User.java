package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

@Document(collection="users")
public class User extends BaseObject implements UserDetails {

	private static final long serialVersionUID = 1L;

	private String username;

	private String password;

	private List<String> roles;

	public User() {
	}
	
	@PersistenceConstructor
	public User(String username, String password, List<String> roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}


	/**
	 * @return returns the granted authorities assigned to the user
	 */

	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if( roles != null ) {
			for( String role: roles) {
				authorities.add(new GrantedAuthorityImpl(role));
			}
		}
		return authorities;
	}

	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}

	public boolean isEnabled() {
		return true;
	}
	
	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("User:{");
		sb.append("username: '").append(username).append("',");
		sb.append("password: '").append("********").append("',");
		sb.append("roles:[");
		if( roles != null) {
			String delim = "";
			for( String role: roles) {
				sb.append(delim).append(role);
				delim = ",";
			}
		}
		sb.append("]}");
		return sb.toString();
	}


	public List<String> getRoles() {
		return roles;
	}


	public void setRoles(List<String> roles) {
		this.roles = roles;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
}

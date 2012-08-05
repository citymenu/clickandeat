package com.ezar.clickandeat.model;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.*;

@Document(collection="users")
public class User extends PersistentObject implements UserDetails {

    public static final String ROLE_USER = "USER";
    
	private static final long serialVersionUID = 1L;

    @Indexed(unique=true)
	private String username;

	private String password;

    @Transient
    private String confirmPassword;

    private String salt;
    
    private Person person;

    private Address address;
    
    private Set<String> roles;
    
	public User() {
        roles = new HashSet<String>();
	}

    public String makeSalt() {
        return "" + Math.round((new Date().getTime() * Math.random()));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(roles.size());
        for( String role: roles ) {
            authorities.add(new GrantedAuthorityImpl(role));
        }
        return authorities;
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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    
    public void addRole(String role) {
        this.roles.add(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                "roles=[" + StringUtils.collectionToCommaDelimitedString(roles) + "]" +
                "}";
    }
}

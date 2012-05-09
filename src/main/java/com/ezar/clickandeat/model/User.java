package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

@Document(collection="users")
public class User extends BaseObject implements UserDetails {

	private static final long serialVersionUID = 1L;

    @Indexed(unique=true)
	private String username;

	private String password;

    private String salt;
    
    @DBRef
    private Person person;

    @DBRef
    private Address address;
    
	public User() {
	}

    public static String makeSalt() {
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
        return new ArrayList<GrantedAuthority>();
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

}

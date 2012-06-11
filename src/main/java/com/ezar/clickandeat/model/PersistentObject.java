package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.mapping.Document;

public class PersistentObject {

	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}

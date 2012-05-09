package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="data")
public class BaseObject {

	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}

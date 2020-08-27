package it.sky.mdw.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class XSDSchema {

	private String xsdPath, xsdNameSpace;

	@JsonIgnore
	private byte[] xsdSchema;

	public XSDSchema() {
		this("", "");
	}

	public XSDSchema(String xsdPath, String xsdNameSpace) {
		super();
		this.xsdPath = xsdPath;
		this.xsdNameSpace = xsdNameSpace;
	}

	public String getXsdPath() {
		return xsdPath;
	}

	public void setXsdPath(String xsdPath) {
		this.xsdPath = xsdPath;
	}

	public String getXsdNameSpace() {
		return xsdNameSpace;
	}

	public void setXsdNameSpace(String xsdNameSpace) {
		this.xsdNameSpace = xsdNameSpace;
	}

	public byte[] getXsdSchema() {
		return xsdSchema;
	}

	public void setXsdSchema(byte[] xsdSchema) {
		this.xsdSchema = xsdSchema;
	}


}

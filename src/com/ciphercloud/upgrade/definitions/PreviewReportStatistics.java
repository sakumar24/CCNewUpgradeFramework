package com.ciphercloud.upgrade.definitions;

public class PreviewReportStatistics {

	private String scope;
	private String org;
	private String filePath;
	private String property;
	private Object previousValue;
	private Object newValue;

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Object getPreviousValue() {
		return previousValue;
	}

	public void setPreviousValue(Object previousValue) {
		this.previousValue = previousValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}
}

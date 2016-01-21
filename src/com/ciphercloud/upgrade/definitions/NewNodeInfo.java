package com.ciphercloud.upgrade.definitions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * This info is required only when action="AddNode".
 * Structure:
 * 
 * <newNodeInfo>
 * 	<parentKey>{xpath of parent}</parentKey>
 * 	<nodeTag>{tag name of the new node}</nodeTag>
 * 	<type>{type for value(text or something else)}</type>
 * 	<nodeValue>{Value of the node}</nodeValue>
 * </newNodeInfo>
 */

@XmlRootElement(name="newNodeInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class NewNodeInfo 
{
    @XmlElement(required = true)
    private String parentKey;
    
    @XmlElement(required = true)
    private String nodeTag;
    
    @XmlElement(required = true)
    private String type;
    
    @XmlElement(required = true)
    private String nodeValue;

	public String getParentKey() {
		return parentKey;
	}

	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	public String getNodeTag() {
		return nodeTag;
	}

	public void setNodeTag(String nodeTag) {
		this.nodeTag = nodeTag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNodeValue() {
		return nodeValue;
	}

	public void setNodeValue(String nodeValue) {
		this.nodeValue = nodeValue;
	}
}

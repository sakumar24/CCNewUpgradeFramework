package com.ciphercloud.upgrade.definitions;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;  
import javax.xml.bind.annotation.XmlElement;  
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;  

/*
 *  Structure of the file :
 *  
 *  <systemDef classification="PFM" resourceContainer="opt/gateway/bin/ccenv" systemDefType="sh" 
 *  									delegateClass="com.ciphercloud.upgrade.delegate.impl.CCEnvDelegator">
 *  <resourceKey reviewed="true">
            <owner>ccg</owner>
            <key>XPATH_to_the_property</key>
    </resourceKey>
 *  </systemDef>
 */

/*@XmlType(name = "SystemDef", propOrder = {
    "resourceKey"
})*/
@XmlRootElement(name="systemChangeDef")
@XmlAccessorType(XmlAccessType.FIELD)

public class SystemChangeDef 
{
    @XmlElement(required = true)
    protected List<ResourceKey> resourceKey;
    @XmlAttribute(name = "classification")
    protected String classification;
    @XmlAttribute(name = "schemaName")
    protected String schemaName;
    @XmlAttribute(name = "resourceContainer")
    protected String resourceContainer;
    @XmlAttribute(name = "systemDefType")
    protected String systemDefType;
    @XmlAttribute(name = "delegateClass")
    protected String delegateClass;
    @XmlAttribute(name = "replaceCustomerFile")
    protected Boolean replaceCustomerFile;

    /**
     * Gets the value of the resourceKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourceKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourceKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourceKey }
     * 
     * 
     */
    public List<ResourceKey> getResourceKey() {
        if (resourceKey == null) {
            resourceKey = new ArrayList<ResourceKey>();
        }
        return this.resourceKey;
    }

    /**
     * Gets the value of the classification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Sets the value of the classification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassification(String value) {
        this.classification = value;
    }

    /**
     * Gets the value of the schemaName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Sets the value of the schemaName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemaName(String value) {
        this.schemaName = value;
    }

    /**
     * Gets the value of the resourceContainer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceContainer() {
        return resourceContainer;
    }

    /**
     * Sets the value of the resourceContainer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceContainer(String value) {
        this.resourceContainer = value;
    }

    /**
     * Gets the value of the systemDefType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemDefType() {
        return systemDefType;
    }

    /**
     * Sets the value of the systemDefType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemDefType(String value) {
        this.systemDefType = value;
    }

    /**
     * Gets the value of the delegateClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDelegateClass() {
        return delegateClass;
    }

    /**
     * Sets the value of the delegateClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelegateClass(String value) {
        this.delegateClass = value;
    }

    /**
     * Gets the value of the replaceCustomerFile property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReplaceCustomerFile() {
        return replaceCustomerFile;
    }

    /**
     * Sets the value of the replaceCustomerFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReplaceCustomerFile(Boolean value) {
        this.replaceCustomerFile = value;
    }

}


//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.9-04/09/2009 09:05 AM(mockbuild)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.09.07 at 02:37:02 PM BST 
//


package org.scribble.protocol.monitor.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Call complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Call">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.scribble.org/monitor}Node">
 *       &lt;attribute name="callIndex" type="{http://www.w3.org/2001/XMLSchema}int" default="-1" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Call")
public class Call
    extends Node
{

    @XmlAttribute
    protected Integer callIndex;

    /**
     * Gets the value of the callIndex property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getCallIndex() {
        if (callIndex == null) {
            return -1;
        } else {
            return callIndex;
        }
    }

    /**
     * Sets the value of the callIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCallIndex(Integer value) {
        this.callIndex = value;
    }

}

/**
 * CommonProductRequireSubmit.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public class CommonProductRequireSubmit  implements java.io.Serializable {
    private java.lang.String strRequestXML;

    public CommonProductRequireSubmit() {
    }

    public CommonProductRequireSubmit(
           java.lang.String strRequestXML) {
           this.strRequestXML = strRequestXML;
    }


    /**
     * Gets the strRequestXML value for this CommonProductRequireSubmit.
     * 
     * @return strRequestXML
     */
    public java.lang.String getStrRequestXML() {
        return strRequestXML;
    }


    /**
     * Sets the strRequestXML value for this CommonProductRequireSubmit.
     * 
     * @param strRequestXML
     */
    public void setStrRequestXML(java.lang.String strRequestXML) {
        this.strRequestXML = strRequestXML;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CommonProductRequireSubmit)) return false;
        CommonProductRequireSubmit other = (CommonProductRequireSubmit) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.strRequestXML==null && other.getStrRequestXML()==null) || 
             (this.strRequestXML!=null &&
              this.strRequestXML.equals(other.getStrRequestXML())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getStrRequestXML() != null) {
            _hashCode += getStrRequestXML().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CommonProductRequireSubmit.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ServiceInterface", ">commonProductRequireSubmit"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("strRequestXML");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

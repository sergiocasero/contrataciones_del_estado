
package es.age.dgpe.placsp.risp.parser.utils.genericode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="Value")
public class Value {
	
	@XmlElement(name = "SimpleValue")
	private String simpleValue;
	
	@XmlAttribute(name="ColumnRef")
	protected String columnRef;
	
	public String getSimpleValue() {
		return simpleValue;
	}
	
	public void setSimpleValue(String simpleValue) {
		this.simpleValue = simpleValue;
	}
	
	public String getColumnRef() {
		return columnRef;
	}
	
	public void setColumnRef(String columnRef) {
		this.columnRef = columnRef;
	}
}

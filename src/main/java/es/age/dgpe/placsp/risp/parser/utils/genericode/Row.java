package es.age.dgpe.placsp.risp.parser.utils.genericode;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="Row")
public class Row {
	
	@XmlElement(name = "Value")
	protected List<Value> values;
	
	public List<Value> getValues() {
		return values;
	}
	
	public void setValues(List<Value> values) {
		this.values = values;
	}
}

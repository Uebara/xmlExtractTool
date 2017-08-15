package meiko.application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cases {
	private final StringProperty id;
	private final StringProperty caseName;
	private final StringProperty caseContent;
	private final StringProperty xmlFileName;

	public Cases(String id, String caseName, String caseContent, String xmlFileName) {
		this.id = new SimpleStringProperty(id);
		this.caseName = new SimpleStringProperty(caseName);
		this.caseContent = new SimpleStringProperty(caseContent);
		this.xmlFileName = new SimpleStringProperty(xmlFileName);
	}

	//////////// ---------id---------/////////////
	public String getid() {
		return id.get();
	}

	public void setid(String id) {
		this.id.set(id);
	}

	public StringProperty idProperty() {
		return id;
	}

	//////////// ---------XmlFileName---------/////////////
	public String getXmlFileName() {
		return xmlFileName.get();
	}

	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName.set(xmlFileName);
	}

	public StringProperty XmlFileNameProperty() {
		return xmlFileName;
	}

	//////////// --------caseContent----------/////////////
	public String getCaseContent() {
		return caseContent.get();
	}

	public void setCaseContent(String caseContent) {
		this.caseContent.set(caseContent);
	}

	public StringProperty caseContentProperty() {
		return caseContent;
	}

	//////////// ---------caseName---------/////////////
	public String getCaseName() {
		return caseName.get();
	}

	public void setCaseName(String caseName) {
		this.caseName.set(caseName);
	}

	public StringProperty CaseNameProperty() {
		return caseName;
	}
}

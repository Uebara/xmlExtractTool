package meiko.application.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javafx.scene.control.TreeItem;

public class TreeItemCreationContentHandler extends DefaultHandler {

	private TreeItem<String> item = new TreeItem<>();
	public static boolean flag = true;

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// finish this node by going back to the parent
		this.item = this.item.getParent();
		this.item.setExpanded(flag);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// start a new node and use it as the current item
		TreeItem<String> item = new TreeItem<>(qName);
		this.item.getChildren().add(item);
		// for (int i = 0; i < attributes.getLength(); i++) {
		// item.getChildren().add(new TreeItem<>("@" + attributes.getQName(i) +
		// " = " + attributes.getValue(i)));
		// }
		this.item = item;
		this.item.setExpanded(flag);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String s = String.valueOf(ch, start, length).trim();
		if (!s.isEmpty()) {
			// add text content as new child
			String temp = "";
			while (s.length() > 60) {
				temp += s.substring(0, 59) + "\n";
				s = s.substring(59);
			}
			s = temp + s;
			this.item.getChildren().add(new TreeItem<>(s));
			// this.item.setExpanded(flag);
		}
	}

	public static TreeItem<String> readData(File file) throws SAXException, ParserConfigurationException, IOException {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		TreeItemCreationContentHandler contentHandler = new TreeItemCreationContentHandler();

		// parse file using the content handler to create a TreeItem
		// representation
		reader.setContentHandler(contentHandler);
		reader.parse(file.toURI().toString());

		// use first child as root (the TreeItem initially created does not
		// contain data from the file)
		TreeItem<String> item = contentHandler.item.getChildren().get(0);
		contentHandler.item.getChildren().clear();

		return item;
	}

	public static TreeItem<String> readDataStr(String content)
			throws IOException, SAXException, ParserConfigurationException {

		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		TreeItemCreationContentHandler contentHandler = new TreeItemCreationContentHandler();

		// parse file using the content handler to create a TreeItem
		// representation
		reader.setContentHandler(contentHandler);
		InputSource is = new InputSource(new ByteArrayInputStream(content.getBytes("UTF-8")));
		reader.parse(is);

		// use first child as root (the TreeItem initially created does not
		// contain data from the file)
		TreeItem<String> item = contentHandler.item.getChildren().get(0);
		contentHandler.item.getChildren().clear();

		return item;
	}

}

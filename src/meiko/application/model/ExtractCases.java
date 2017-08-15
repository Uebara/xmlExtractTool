package meiko.application.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import meiko.application.view.dataBaseController;

/* add by like */
/*
 * edit by M E I K O
 * */
public class ExtractCases {

	private final static ObservableList<Cases> casesData = FXCollections.observableArrayList();

	public static ObservableList<Cases> getCasesData() {
		return casesData;
	}

	public ExtractCases(String filePath, String xpath) {
		casesData.clear();
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File(filePath));
			Element node = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> nodes = node.selectNodes(xpath);
			Element nodess = null;

			for (int i = 0; i < nodes.size(); i++) {
				nodess = nodes.get(i);

				String id = Long.toString(System.currentTimeMillis() + i);
				String CaseName = nodess.element("title").getText();
				String CaseContent = nodess.asXML();
				String xmlFile = (node.selectSingleNode("/*[name()='book']/*[name()='info']/*[name()='bookId']"))
						.getText();
				casesData.add(new Cases(id, CaseName, CaseContent, xmlFile));
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int extratc(ArrayList<String> itemIdDelete) throws Exception {
		List<Cases> caseList = getCasesData();

		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:Cases.db");
		Statement stat = conn.createStatement();
		stat.executeUpdate(
				"create table if not exists cases(id long, case_name varchar(20),content text,xml_filename varchar(20));");

		for (int i = 0; i < caseList.size(); i++) {
			Cases oneCase = caseList.get(i);
			if (itemIdDelete.contains(oneCase.getid())) {
				continue;
			}
			System.out.println(oneCase.getCaseName());
			String sql = "insert into cases values(" + oneCase.getid() + ",'" + oneCase.getCaseName() + "','"
					+ oneCase.getCaseContent() + "','" + oneCase.getXmlFileName() + "');";
			stat.executeUpdate(sql);
		}
		conn.close();

		return caseList.size();
	}

	public static void ExtractCaseDetail(ArrayList<String> caseStr, Map<String, String> map)
			throws SQLException, ClassNotFoundException, DocumentException {
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:Cases.db");
		Statement stat = conn.createStatement();
		stat.executeUpdate(
				"create table if not exists caseDetail(id long, title varchar(20),content text,name varchar(20));");

		for (int i = 0; i < caseStr.size(); i++) {
			Document document = DocumentHelper.parseText(caseStr.get(i));
			Element node = document.getRootElement();

			String title = node.element("title").getText();
			// System.out.println("根节点为：" + node.getName());

			listNodes(node, title, map, stat);
		}
		conn.close();
	}

	private static void listNodes(Element node, String title, Map<String, String> map, Statement stat) {
		if ("title".equals(node.getName())) {
			for (Map.Entry<String, String> entry : map.entrySet()) {

				if ((node.getText()).contains(entry.getKey())) {
					Long id = System.currentTimeMillis();
					String content = node.getParent().asXML();
					String name = entry.getValue();
					String sql = "insert into caseDetail values(" + id + ",'" + title + "','" + content + "','" + name
							+ "');";

					try {
						stat.executeUpdate(sql);

						CaseDetails oneCase = new CaseDetails(Long.toString(id), title, name, content);
						dataBaseController.getCasesDetail().add(oneCase);

//						dataBaseController.geteditedContent().add(title);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		@SuppressWarnings("unchecked")
		Iterator<Element> it = node.elementIterator();
		while (it.hasNext()) {
			Element e = it.next();
			listNodes(e, title, map, stat);
		}
	}

}

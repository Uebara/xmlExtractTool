package meiko.application.database;

import java.util.*;

/**
 * @author Alice
 *
 */
public class SQLiteTest extends Thread {

	public void insertData(String path,String title, String content, String name) {

		SQLiteDataBase db = new SQLiteDataBase(path);

		boolean bl = db.tableExist("original_case");
		if (!bl) {
			db.createTable(
					"create table original_case(case_id varchar2(50), case_name varchar2(50),case_content varchar2(500),origin_xml varchar2(50))");
		}

		Date date = new Date();
		String time = "id"+date.getTime();

		Map<String, Object> fieldMap = new HashMap<String, Object>();
		fieldMap.put("case_id", time);
		fieldMap.put("case_name", title);
		fieldMap.put("case_content", content);
		fieldMap.put("origin_xml", name);
		db.insert("original_case", fieldMap);
	}

	public void run1() {
		SQLiteDataBase db = new SQLiteDataBase("D:/meiko11.db");

		// Map<String, Object> keyMap = new HashMap<String, Object>();
		// keyMap.put("id", 11);
		// keyMap.put("aa", "123");
		boolean bl = db.tableExist("original_case");
		if (!bl) {
			//
			db.createTable(
					"create table original_case(case_id integer, case_name varchar2(50),case_content varchar2(500),origin_xml varchar2(50))");
		}

		// insert
		for (int i = 0; i < 10; i++) {
			Map<String, Object> fieldMap = new HashMap<String, Object>();
			fieldMap.put("case_id", "2012340293749043");
			fieldMap.put("case_name", "广东电网有限责任公司东莞供电局");
			fieldMap.put("case_content",
					"<authorgroup><author><orgname>广东电网有限责任公司东莞供电局</orgname></author></authorgroup>");
			fieldMap.put("origin_xml", "sssssssssssss.xml");
			db.insert("original_case", fieldMap);
		}

		// delete
		// db.delete("original_case", "name", "user3");

		// update
		// Map<String, Object> fieldMap = new HashMap<String, Object>();
		// fieldMap.put("name", "hisunsray");
		// fieldMap.put("age", 100);
		//
		// keyMap = new HashMap<String, Object>();
		// keyMap.put("id", 10);
		// db.update("t_student", keyMap, fieldMap);
		//
		// List<Map<String, String>> resultList = db
		// .queryList("select * from t_student");
		// for (int j = 0; j < resultList.size(); j++) {
		// Map<String, String> map = (Map<String, String>) resultList.get(j);
		// Iterator entries = map.entrySet().iterator();
		// Map.Entry entry;
		// while (entries.hasNext()) {
		// entry = (Map.Entry) entries.next();
		// String name = (String) entry.getKey();
		// String value = entry.getValue().toString();
		// System.out.print(name + "=" + value + " ");
		// }
		// System.out.println();
		// }
	}

	public static void main(String args[]) {
//		for (int i = 0; i < 1; i++) {
//			SQLiteTest test = new SQLiteTest();
//			test.start();
//		}
		String str1 = "78.1 故障情况说明";
		String str2 = "故障情况";
		System.out.println(str1.contains(str2));
	}
}

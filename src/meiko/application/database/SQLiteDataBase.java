package meiko.application.database;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQLite���ݿ������
 *
 * @author Alice
 *
 */
public class SQLiteDataBase {
	public final static Logger log = LoggerFactory
			.getLogger(SQLiteDataBase.class);
	public final static int SUCCESS = 0;

	private SQLiteOpenHelper mOpenHelper = null;

	/**
	 * ���캯��
	 *
	 * @param dbName
	 */
	public SQLiteDataBase(String dbName) {
		this.mOpenHelper = SQLiteOpenHelper.instance(dbName);
	}

	/**
	 * ������/����/�޸�/ɾ���Ȳ������
	 *
	 * @param sql
	 * @return boolean
	 */
	public boolean execSQL(String sql) {
		return execSQL(sql, null);
	}

	/**
	 * ������������/�޸�/ɾ���Ȳ������
	 *
	 * @param sql
	 * @return boolean
	 */
	public boolean execSQL(String sql, Object[] params) {
		return mOpenHelper.execSQL(sql, params) == SUCCESS ? true : false;
	}

	/**
	 * ������
	 *
	 * @param sql
	 * @return
	 */
	public boolean createTable(String sql) {
		return execSQL(sql);
	}

	/**
	 * �жϱ��Ƿ����
	 *
	 * @param tableName
	 * @return
	 */
	public boolean tableExist(String tableName) {
//		boolean result = false;
		if (tableName == null) {
			return false;
		}
		List<Map<String, String>> resultList = queryList("select count(*) from sqlite_master where type='table' and name='"
				+ tableName + "'");
		if (resultList != null && resultList.size() > 0) {
			String count = resultList.get(0).get("COUNT(*)");
			return count.equals("1") ? true : false;
		} else {
			return false;
		}
	}

	/**
	 * ����һ������
	 *
	 * @param table
	 *            :����
	 * @param contentMap
	 *            :һ��Map����
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean insert(String table, Map<String, Object> contentMap) {
		String sql = "insert into " + table + "(";
		Iterator entries = contentMap.entrySet().iterator();
		Map.Entry entry;
		String name = "";
		String value = "";
		int i = 0;
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			name = (String) entry.getKey();
			Object valueObj = entry.getValue();
			if (valueObj == null) {
				value += "";
			} else if (valueObj instanceof String) {
				value += "'" + valueObj.toString() + "'";
			} else {
				value += ((Integer) valueObj).intValue();
			}
			if (i == contentMap.size() - 1) {
				sql += name + ") values(";
				value += ")";
			} else {
				sql += name + ",";
				value += ",";
			}
			i++;
		}
		sql += value;
		return execSQL(sql);
	}

	/**
	 * ɾ��ָ������ָ����ֵ��Ԫ��
	 *
	 * @param table
	 * @param key
	 * @param keyValue
	 * @return
	 */
	public boolean delete(String table, String key, Object keyValue) {
		String sql = "";
		if (keyValue instanceof String) {
			sql = "delete from " + table + " where " + key + "='"
					+ keyValue.toString() + "';";
		} else {
			sql = "delete from " + table + " where " + key + "="
					+ ((Integer) keyValue).intValue();
		}
		return execSQL(sql);
	}

	/**
	 * �޸ı���һ��Ԫ������ݡ�
	 *
	 * @param table
	 * @param keyMap
	 * @param fieldMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean update(String table, Map<String, Object> keyMap,
			Map<String, Object> fieldMap) {
		String sql = "update " + table + " set ";
		Iterator entries = fieldMap.entrySet().iterator();
		Map.Entry entry;
		int i = 0;
		String name = "";
		Object value = null;
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			name = (String) entry.getKey();
			Object valueObj = entry.getValue();
			if (valueObj == null) {
				value = "";
			} else if (valueObj instanceof String) {
				value = "'" + valueObj.toString() + "'";
			} else {
				value = ((Integer) valueObj).intValue();
			}
			if (i == (fieldMap.size() - 1)) {
				sql += (name + "=" + value + " where ");
			} else {
				sql += (name + "=" + value + ",");
			}
			i++;
		}
		entries = keyMap.entrySet().iterator();
		i = 0;
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			name = (String) entry.getKey();
			Object valueObj = entry.getValue();
			if (valueObj == null) {
				value = "";
			} else if (valueObj instanceof String) {
				value = "'" + valueObj.toString() + "'";
			} else {
				value = ((Integer) valueObj).intValue();
			}
			if (i == (keyMap.size() - 1)) {
				sql += (name + "=" + value);
			} else {
				sql += (name + "=" + value + " and ");
			}
			i++;
		}
		System.out.println("sql:" + sql);
		return execSQL(sql);
	}

	/**
	 * ��ѯ�����
	 *
	 * @param sql
	 * @return
	 */
	public List<Map<String, String>> queryList(String sql) {
		return queryList(sql, null);
	}

	/**
	 * ��ҳ��ѯ�����
	 *
	 * @param sql
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, String>> queryPageList(String sql, int pageNum,
			int pageSize) {
		sql += " limit ?,?";
		String[] params = new String[2];
		params[0] = String.valueOf(pageSize * (pageNum - 1) + 1);
		params[1] = String.valueOf(pageSize * pageNum);
		return queryList(sql, params);
	}

	/**
	 * ��������ҳ��ѯ�����
	 *
	 * @param sql
	 * @param params
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, String>> queryPageList(String sql, Object[] params,
			int pageNum, int pageSize) {
		sql += " limit ?,?";
		Object[] newParams = new Object[params.length + 2];
		for (int i = 0; i < params.length; i++) {
			newParams[i] = params[i];
		}
		newParams[params.length] = String.valueOf(pageSize * (pageNum - 1) + 1);
		newParams[params.length + 1] = String.valueOf(pageSize * (pageNum - 1)
				+ 1);
		return queryList(sql, params);
	}

	/**
	 * ��ѯ�����
	 *
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String, String>> queryList(String sql, Object[] params) {
		return mOpenHelper.queryList(sql, params);
	}
}

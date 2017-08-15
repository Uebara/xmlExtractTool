package meiko.application.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * create/open/manager database
 *
 * @author Alice
 *
 */
public class SQLiteOpenHelper {
	public final static Logger log = LoggerFactory
			.getLogger(SQLiteDataBase.class);

	private String dbDriverClass = "org.sqlite.JDBC"; //  ����·��������
	private String dbUrl = "jdbc:sqlite:";// �������ݿ��URL
	private Connection mConn = null; // ���ݿ����Ӷ���
	private String mName = "";
	private static SQLiteOpenHelper mOpenHelper = null;

	/**
	 * �����ʼ��
	 *
	 * @param dbName
	 * @return
	 */
	public synchronized static SQLiteOpenHelper instance(String dbName) {
		if (mOpenHelper == null) {
			mOpenHelper = new SQLiteOpenHelper();
			mOpenHelper.connect(dbName);
		}
		return mOpenHelper;
	}

	/**
	 * �������ݿ�����
	 *
	 * @param dbname
	 */
	public boolean connect(String dbName) {
		/**
		 * ���������dbname��������·��,���ȡ·��
		 */
		if (dbName.indexOf(":") == -1) {
			dbName = this.getClass().getClassLoader().getResource("").getPath()
					+ dbName;
		}
		this.mName = dbName;
		this.dbUrl = "jdbc:sqlite:" + dbName;
		try {
			/**
			 * ����ע��
			 */
			Driver driver = (Driver) Class.forName(dbDriverClass).newInstance();
			DriverManager.registerDriver(driver);
			this.mConn = DriverManager.getConnection(dbUrl);
			log.info("open sqlite database " + mName + " success");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("open sqlite database " + mName + " fail.error msg:"
					+ e.getMessage());
			return false;
		}
	}

	/**
	 * ������������/�޸�/ɾ���Ȳ������
	 *
	 * @param sql
	 * @return boolean
	 */
	public synchronized int execSQL(String sql, Object[] params) {
		int result = -1;
		StringBuffer sb = new StringBuffer();
		try {
			this.mConn.setAutoCommit(false);
			PreparedStatement pstmt = this.mConn.prepareStatement(sql);
			if (params != null) {
				sb.append("Begin ExeSQL:" + sql + ",Get In Parameter Values..."
						+ "\r\n");
//				int nParamCount = params.length;
				int i = 0, tempInt;
				for (i = 0; i < params.length; i++) {
					tempInt = i + 1;
					if (params[i] instanceof String) {
						String s = (String) params[i];
						pstmt.setString(tempInt, s);
						sb.append("<Param> Field[" + i + "] value is: '" + s
								+ "'\r\n");
					} else {
						int a = ((Integer) params[i]).intValue();
						pstmt.setInt(tempInt, a);
						sb.append("<Param> Field[" + i + "] value is: " + a
								+ "\r\n");
					}
				}
			} else {
				sb.append("Begin ExeSQL:" + sql + "\r\n");
			}
			result = pstmt.executeUpdate();
			commit();
			sb.append("success." + "\r\n");
			log.info(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("fail.errMsg->" + e.getLocalizedMessage() + "\r\n");
			log.info(sb.toString());
			rollback();
		}
		return result;
	}

	/**
	 * ��ѯ�����
	 *
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String, String>> queryList(String sql, Object[] params) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		try {
			PreparedStatement pstmt = this.mConn.prepareStatement(sql);
			if (params != null) {
				sb.append("Begin ExeSQL:" + sql + ",Get In Parameter Values..."
						+ "\r\n");
//				int nParamCount = params.length;
				int i = 0, tempInt;
				for (i = 0; i < params.length; i++) {
					tempInt = i + 1;
					if (params[i] instanceof String) {
						String s = (String) params[i];
						pstmt.setString(tempInt, s);
						sb.append("<Param> Field[" + i + "] value is: '" + s
								+ "'\r\n");
					} else {
						int a = ((Integer) params[i]).intValue();
						pstmt.setInt(tempInt, a);
						sb.append("<Param> Field[" + i + "] value is: " + a
								+ "\r\n");
					}
				}
			} else {
				sb.append("Begin ExeSQL:" + sql + "\r\n");
			}
			rs = pstmt.executeQuery();
			int columnCount = 0;
			while (rs.next()) {
				Map<String, String> rowMap = new HashMap<String, String>();
				columnCount = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					rowMap.put(rs.getMetaData().getColumnName(i).toUpperCase(),
							rs.getString(i));
				}
				resultList.add(rowMap);
			}
			sb.append("success." + "����ѯ��" + resultList.size() + "����¼,ÿ����"
					+ columnCount + "���ֶ�" + "\r\n");
			log.info(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("fail.errMsg->" + e.getLocalizedMessage() + "\r\n");
			log.info(sb.toString());
			rollback();
		}
		return resultList;
	}

	/**
	 * ��������ɾ����ʱ���������ύ
	 *
	 * @param conn
	 */
	private void commit() {
		try {
			mConn.commit();
		} catch (SQLException e) {
			rollback();
		}
	}

	/**
	 * �����쳣ʱ���лع�
	 *
	 * @param conn
	 */
	private void rollback() {
		try {
			mConn.rollback();
		} catch (SQLException e) {
			log.error("�쳣ʱ�ع����� :" + e.getLocalizedMessage());
		}
	}

	/**
	 * �ر�����
	 */
	public void closeConnetion() {
		try {
			mConn.close();
			log.info("close sqlite database " + mName + " success");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("close sqlite database " + mName + " fail.error msg:"
					+ e.getMessage());
		}
	}
}

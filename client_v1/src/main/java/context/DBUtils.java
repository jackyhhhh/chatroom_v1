package context;

import java.sql.*;

public class DBUtils {
    private static DBUtils dbUtils;
    private static Statement stmt;

    private DBUtils(String host, int port, String user, String password){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Log.info("数据库驱动加载成功, 正在连接数据库......");
            String url = "jdbc:mysql://"+host+":"+port
                    +"?useSSL=false" +
                    "&allowPublicKeyRetrieval=true" +
                    "&serverTimezone=UTC" +
                    "&useUnicode=true" +
                    "&characterEncoding=UTF-8";

            Connection conn = DriverManager.getConnection(url, user, password);
            Log.info("获得mysql连接:"+conn.toString());
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            Log.error(e);
        }
    }

    public static DBUtils getInstance(String host, int port, String user, String password){
        if(dbUtils == null){
            dbUtils = new DBUtils(host, port, user, password);
        }
        return dbUtils;
    }

    public ResultSet query(String sql){
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
        } catch (SQLException sqlException) {
            Log.error(sqlException);
        }
        return rs;
    }

    public boolean execute(String sql){
        try {
            stmt.execute(sql);
            return true;
        } catch (SQLException sqlException) {
            Log.error(sqlException);
        }
        return false;
    }

    public String getStringInOneResult(ResultSet rs, String columnLabel){
        String value = null;
        try {
            while(rs.next()){
                value = rs.getString(columnLabel);
            }
        } catch (SQLException sqlException) {
            Log.error(sqlException);
        }
        return value;
    }

    public int getCountInOneResult(String checkSql){
        return Integer.parseInt(getStringInOneResult(query(checkSql), "COUNT(*)"));
    }

    public boolean notExistDatabase(String dbName){
        int count = getCountInOneResult("SELECT COUNT(*) FROM information_schema.SCHEMATA where SCHEMA_NAME = '"+dbName+"';");
        return count == 0;
    }

    public boolean notExistTable(String tableName){
        int count = getCountInOneResult("SELECT COUNT(*) FROM information_schema.TABLES where table_name = '"+tableName+"';");
        return count == 0;
    }
}

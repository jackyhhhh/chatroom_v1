package context;

import java.sql.*;

public class DBUtils {
    private static Statement stmt;
    private static ResultSet rs;
    static {
        String driver = Config.serverDB("driver");
        String host = Config.serverDB("host");
        String port = Config.serverDB("port");
        String user = Config.serverDB("user");
        String password = Config.serverDB("password");
        String url = "jdbc:mysql://" + host + ":" + port +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, user, password);
            Log.info("获取到mysql连接:"+conn.toString());
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            Log.error(e.getMessage());
        }
    }

    public static ResultSet query(String sql){
        try {
            rs = stmt.executeQuery(sql);
        } catch (SQLException sqlException) {
            Log.error(sqlException.getMessage());
        }
        return rs;
    }

    public static boolean execute(String sql){
        try {
            stmt.execute(sql);
            return true;
        } catch (SQLException sqlException) {
            Log.error(sqlException.getMessage());
        }
        return false;
    }

    public static String getStringInOneResult(String columnLabel){
        String value = null;
        try {
            while(rs.next()){
                value = rs.getString(columnLabel);
            }
        } catch (SQLException sqlException) {
            Log.error(sqlException.getMessage());
        }
        return value;
    }

    public static int getCountInOneResult(String checkSql){
        query(checkSql);
        String count = getStringInOneResult("COUNT");
        return Integer.parseInt(count);
    }

    public static boolean notExistDatabase(String database){
        String checkSql = "SELECT COUNT(*) AS COUNT FROM information_schema.SCHEMATA where SCHEMA_NAME= '"+database+"';";
        return getCountInOneResult(checkSql) == 0;
    }

    public static boolean notExistTable(String tableName){
        String checkSql = "SELECT count(table_name) as COUNT FROM information_schema.TABLES WHERE table_name = '"+tableName+"';";
        return getCountInOneResult(checkSql) == 0;
    }
}

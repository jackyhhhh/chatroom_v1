package manager;

import context.DBUtils;
import context.Log;
import context.Order;
import core.ServerConnection;

import java.sql.ResultSet;

public class DBManager {
    private static DBUtils dbUtils;

    public static void connect(ServerConnection sc, String line){
        String[] data = line.split("&");
        dbUtils = DBUtils.getInstance(data[0], Integer.parseInt(data[1]), data[2], data[3]);
        if(dbUtils.execute("USE chatroom;")){
            Log.info("连接数据库(chatroom)成功!!!");
            ConnManager.sendOrderToServer(sc, Order.TMP_TABLE);
        }else{
            Log.warn("连接数据库(chatroom)失败!!!");
        }
    }

    public static ResultSet getNew12Msg(ServerConnection sc){
        String tmpMsgTableName = sc.getTmpMsgTableName();
        String sql = "SELECT * FROM "
                + "(SELECT _timestamp, host, port, msg_content FROM "+tmpMsgTableName+" ORDER BY _timestamp DESC LIMIT 12) AS TT "
                + "ORDER BY _timestamp;";
        if(dbUtils!=null){
            return dbUtils.query(sql);
        }
        return null;
    }
}

package manager;

import context.AESOperator;
import context.Config;
import context.Log;
import core.ClientConnection;

import java.util.ArrayList;
import java.util.List;


public class ConnManager {
    private static final List<ClientConnection> allConns = new ArrayList<>();

    public static void add(ClientConnection cc) {
        synchronized (allConns){
            allConns.add(cc);
        }
    }

    public static void remove(ClientConnection cc) {
        synchronized (allConns) {
            allConns.remove(cc);
        }
    }

    public static void sendNoticeToAll(String notice){
        synchronized (allConns){
            for(ClientConnection cc : allConns){
                cc.println("[NOTICE]-"+notice);
            }
        }
    }

    public static void sendOrderToAll(int order){
        synchronized (allConns){
            for(ClientConnection cc : allConns){
                cc.println("[ORDER]-"+order);
            }
        }
    }

    public static void sendTmpMsgTableNameToClient(ClientConnection cc){
        synchronized (cc.getWriter()) {
            cc.println("[TMP_TABLE]-" + cc.getTmpMsgTableName());
        }

        Log.info("已发送临时聊天记录表的表名到用户("+cc.getName()+") !");
    }

    public static void sendDataSourceToClient(ClientConnection cc){
        synchronized (cc.getWriter()){
            String line = Config.clientDB("host")
                    + "&" + Config.clientDB("port")
                    + "&" + Config.clientDB("user")
                    + "&" + Config.clientDB("password");
            String encryptedLine =  AESOperator.getInstance().encrypt(line);
            cc.println("[DB_INFO]-" + encryptedLine);
        }
        Log.info("已发送数据库相关信息到用户("+cc.getName()+") !");
    }

    public static int getSizeOfAllConn(){
        return allConns.size();
    }
}

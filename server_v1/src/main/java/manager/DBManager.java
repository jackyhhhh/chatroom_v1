package manager;

import context.Config;
import context.DBUtils;
import context.Log;
import core.ClientConnection;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static final List<String> allTmpTables = new ArrayList<>();
    private static DBManager dbm = null;
    static {
        checkDatabaseExist();
        checkTableHistoryMsgExist();
//        checkTableEmptyMsgExist();
    }

    private static void checkDatabaseExist() {
        String dbName = Config.serverDB("name");
        if (DBUtils.notExistDatabase(dbName)) {
            Log.info("数据库(" + dbName + ")不存在, 开始建库......");
            String sql = "CREATE DATABASE "+dbName+" DEFAULT CHARACTER SET UTF8 COLLATE utf8_general_ci;";
            if(DBUtils.execute(sql)){
                Log.info("数据库("+dbName+")创建成功!!!");
            }else{
                Log.error("数据库("+dbName+")创建失败!!!");
            }
        }
        if(DBUtils.execute("USE "+dbName+";")){
            Log.info("连接数据库chatroom成功!!");
            DBUtils.execute("DROP TABLE IF EXISTS tmpMsg;");
        }else{
            Log.info("连接数据库chatroom失败!!");
        }
    }

    private static void checkTableHistoryMsgExist(){
        if(DBUtils.notExistTable("history_msg")){
            Log.info("历史消息记录表不存在, 开始建表......");
            String sql = "CREATE TABLE history_msg ("
                    + "id int(6) NOT NULL AUTO_INCREMENT,"
                    + "_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                    + "host varchar(15) NOT NULL,"
                    + "port int(5) NOT NULL,"
                    + "msg_content text,"
                    + "UNIQUE KEY id (id)"
                    + ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            if(DBUtils.execute(sql)){
                Log.info("创建历史消息记录表(history_msg)成功!!!");
            }else{
                Log.error("创建历史消息记录表(history_msg)失败!!!");
            }
        }
    }
//
//    private static void checkTableEmptyMsgExist(){
//        if(DBUtils.notExistTable("empty_msg")){
//            Log.info("空表(empty_msg)不存在, 开始建表......");
//            String sql = "CREATE TABLE empty_msg ("
//                    + "id int(6) NOT NULL AUTO_INCREMENT,"
//                    + "_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
//                    + "host varchar(15) NOT NULL,"
//                    + "port int(5) NOT NULL,"
//                    + "msg_content text,"
//                    + "UNIQUE KEY id (id)"
//                    + ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
//            if(DBUtils.execute(sql)){
//                Log.info("空表(empty_msg)创建成功!!!");
//            }else {
//                Log.error("空表(empty_msg)创建失败!!!");
//            }
//        }
//    }

    private DBManager(){

    }

    public static DBManager getInstance(){
        if(dbm == null){
            dbm = new DBManager();
        }
        return dbm;
    }

    public void createTmpMsgTable(ClientConnection cc){
        String tmpMsgTableName = cc.getTmpMsgTableName();
        String sql = "CREATE TABLE  "+tmpMsgTableName+" ("
                + "id int(6) NOT NULL AUTO_INCREMENT,"
                + "_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "host varchar(15) NOT NULL,"
                + "port int(5) NOT NULL,"
                + "msg_content text,"
                + "UNIQUE KEY id (id)"
                + ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        if(DBUtils.execute(sql)){
            Log.info("临时聊天记录表(tmpMsg)创建成功!!!");
            addTmpTable(tmpMsgTableName);
        }else {
            Log.error("临时聊天记录表(tmpMsg)创建失败!!!");
        }
    }

    public void deleteTmpMsgTable(ClientConnection cc){
        String tmpMsgTableName = cc.getTmpMsgTableName();
        String sql = "DROP TABLE IF EXISTS " + tmpMsgTableName;
        if(DBUtils.execute(sql)){
            Log.info("临时聊天记录表(" + tmpMsgTableName + ")删除成功!!!");
        }else{
            Log.warn("临时聊天记录表(" + tmpMsgTableName + ")删除失败!!!");
        }
        removeTmpTable(tmpMsgTableName);
    }

    public static void saveMsg(ClientConnection cc, String msg){
        String host = cc.getHost();
        int port = cc.getPort();
        String sql1 = "INSERT INTO history_msg (host, port, msg_content) values ('" + host + "'," + port +", '" + msg + "');";
        DBUtils.execute(sql1);
        synchronized (allTmpTables){
            String sql2;
            for(String name : allTmpTables){
                sql2 = "INSERT INTO "+name+" (host, port, msg_content) values ('" + host + "'," + port +", '" + msg + "');";
                DBUtils.execute(sql2);
            }
        }
    }

    public void addTmpTable(String name){
        synchronized (allTmpTables){
            allTmpTables.add(name);
        }
    }

    public void removeTmpTable(String name){
        synchronized (allTmpTables){
            allTmpTables.remove(name);
        }
    }
}

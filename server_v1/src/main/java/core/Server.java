package core;

import context.Config;
import context.Log;
import manager.OrderManager;
import manager.ConnManager;
import manager.DBManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    Server(){
        try {
            Log.info("正在启动服务器......");
            serverSocket = new ServerSocket(Integer.parseInt(Config.server("port")));
            Log.info("服务器启动成功!!!");
        } catch (IOException e) {
            Log.error(e);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    void start(){
        Log.info("正在等待客户端连接......");
        while(true){
            try {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                Thread t = new Thread(handler);
                t.start();
            } catch (IOException e) {
                Log.error(e);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ClientConnection cc = new ClientConnection(socket);
            ConnManager.add(cc);
            String onlineNotice ="用户( " + cc.getName() + " ) 上线了!!!";
            ConnManager.sendNoticeToAll(Log.now()+"  "+onlineNotice);
            Log.info(onlineNotice);

            String line;
            while(true){
                try {
                    line = cc.readLine();
                } catch (Exception e) {
                    cc.close();
                    ConnManager.remove(cc);
                    String offlineNotice = "用户( " + cc.getName() + " ) 断开了连接!!!";
                    ConnManager.sendNoticeToAll(Log.now()+"  "+offlineNotice);
                    Log.info(offlineNotice);
                    break;
                }
                if(!line.equals("")){
                    Log.info("[fr_client("+cc.getName()+")]-"+line);
                    if(line.startsWith("[MSG]-")){
                        line = line.substring(6);
                        DBManager.saveMsg(cc, line);
                        ConnManager.sendOrderToAll(OrderManager.REFRESH);
                    }else if(line.startsWith("[ORDER]-")){
                        int order = Integer.parseInt(line.substring(8));
                        OrderManager.action(order, cc);
                    }
                }
            }
        }
    }

}

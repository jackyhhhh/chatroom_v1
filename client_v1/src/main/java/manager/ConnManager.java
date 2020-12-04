package manager;

import core.ServerConnection;

public class ConnManager {

    public static void sendOrderToServer(ServerConnection sc, int order){
        sc.println("[ORDER]-"+order);
    }

    public static void sendMsgToServer(ServerConnection sc, String msg){
        sc.println("[MSG]-"+msg);
    }
}

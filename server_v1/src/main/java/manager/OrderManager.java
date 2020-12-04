package manager;

import context.Config;
import context.Log;
import core.ClientConnection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OrderManager {
    public static final int REFRESH = 0;
    public static final int TMP_TABLE = 1;
    public static final int DB_INFO = 2;

    public static void sayHi(String name){
        System.out.println("hello "+name);
    }

    public static void action(int order, ClientConnection cc){
        String action = Config.getActionByOrder(order);
        try {
            Class<?> cls = Class.forName("manager.ConnManager");
            Method method = cls.getMethod(action, cc.getClass());
            method.invoke(null, cc);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.error(e.getMessage());
        }
    }
}

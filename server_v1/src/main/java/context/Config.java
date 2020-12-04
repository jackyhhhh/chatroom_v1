package context;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private static final Map<String, String> dbCfgForClient = new HashMap<String, String>();
    private static final Map<String, String> dbCfgForServer = new HashMap<String, String>();
    private static final Map<String, String> serverCfg = new HashMap<String, String>();
    private static final Map<Integer, String> orderMapping = new HashMap<>();
    private static Element root = null;
    static {
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(Config.class.getClassLoader().getResourceAsStream("config.xml"));
            root = doc.getRootElement();
        } catch (DocumentException e) {
            Log.error(e.getMessage());
        }

        init();
    }

    private static void init(){
        initDBConfig();
        initServerConfig();
        initOrderMapping();
    }

    private static void initOrderMapping(){
        List<Element> orders = root.elements("order");
        for(Element e: orders){
            String action = e.elementTextTrim("action");
            int code = Integer.parseInt(e.elementTextTrim("code"));
            orderMapping.put(code, action);
        }
    }

    private static void initDBConfig(){
        Element db = root.element("database");
        Element clientDB = db.element("client");
        dbCfgForClient.put("driver", clientDB.elementTextTrim("driver"));
        dbCfgForClient.put("host", clientDB.elementTextTrim("host"));
        dbCfgForClient.put("port", clientDB.elementTextTrim("port"));
        dbCfgForClient.put("name", clientDB.elementTextTrim("name"));
        dbCfgForClient.put("user", clientDB.elementTextTrim("user"));
        dbCfgForClient.put("password", clientDB.elementTextTrim("password"));

        Element serverDB = db.element("server");
        dbCfgForServer.put("driver", serverDB.elementTextTrim("driver"));
        dbCfgForServer.put("host", serverDB.elementTextTrim("host"));
        dbCfgForServer.put("port", serverDB.elementTextTrim("port"));
        dbCfgForServer.put("name", serverDB.elementTextTrim("name"));
        dbCfgForServer.put("user", serverDB.elementTextTrim("user"));
        dbCfgForServer.put("password", serverDB.elementTextTrim("password"));
    }

    private static void initServerConfig(){
        Element server = root.element("server");
        serverCfg.put("host", server.attributeValue("host"));
        serverCfg.put("port", server.attributeValue("port"));
    }

    public static String clientDB(String argName){
        return dbCfgForClient.get(argName);
    }

    public static String serverDB(String argName){
        return dbCfgForServer.get(argName);
    }

    public static String server(String argName){
        return serverCfg.get(argName);
    }

    public static String getActionByOrder(int order){ return orderMapping.get(order); }
}

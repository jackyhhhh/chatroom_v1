package context;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private static final Map<String, String> serverConfig = new HashMap<>();
    private static final Map<Integer, String> orderMapping = new HashMap<>();
    private static final Element root = getRootFromXMl();
    static {
        init();
    }

    private static void init(){
        initServerConfig();
        initOrderMapping();
    }

    private static void initOrderMapping(){
        List<Element> orders = root.elements("order");
        for(Element e : orders){
            int code = Integer.parseInt(e.elementTextTrim("code"));
            String action = e.elementTextTrim("action");
            orderMapping.put(code, action);
        }
    }

    private static void initServerConfig(){
        Element server = root.element("server");
        serverConfig.put("host", server.attributeValue("host"));
        serverConfig.put("port", server.attributeValue("port"));
    }

    public static String server(String arg){ return serverConfig.get(arg);}

    public static String getActionByOrder(int order){ return orderMapping.get(order); }

    private static Element getRootFromXMl(){
        SAXReader reader = new SAXReader();
        Element root = null;
        try {
            Document doc = reader.read(Config.class.getClassLoader().getResourceAsStream("config.xml"));
            root = doc.getRootElement();
        } catch (DocumentException e) {
            Log.error(e);
        }
        return root;
    }
}

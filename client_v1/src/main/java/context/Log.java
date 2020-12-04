package context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    public static PrintWriter writer;
    static {
        File file = new File("client_"+day_str()+".log");
        try {
            writer = new PrintWriter(new FileWriter(file, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String now(){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date now = new Date();
        return fmt.format(now);
    }
    public static String day_str(){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        return fmt.format(now);
    }

    public static void info(String msg){
        String line = "["+now()+"]-"+"[INFO]-"+msg;
        System.out.println(line);
        writer.println(line);
        writer.flush();
    }

    public static void warn(String msg){
        String line = "["+now()+"]-"+"[WARNING]-"+msg;
        System.out.println(line);
        writer.println(line);
        writer.flush();
    }

    public static void error(String msg){
        String line = "["+now()+"]-"+"[ERROR]-"+msg;
        System.out.println(line);
        writer.println(line);
        writer.flush();
    }

    public static void error(Exception e){
        String line = "["+now()+"]-"+"[ERROR]-"+e.getMessage();
        System.out.println(line);
        writer.println(line);
        writer.flush();
    }
}

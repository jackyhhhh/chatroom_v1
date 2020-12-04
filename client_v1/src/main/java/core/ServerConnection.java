package core;

import context.Config;
import context.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerConnection {
    private String localHost;
    private int localPort;
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private String tmpMsgTableName;

    public ServerConnection(){
        try {
            Log.info("正在连接服务器......");
            socket = new Socket(Config.server("host"), Integer.parseInt(Config.server("port")));
            Log.info("连接服务器成功!");
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            localHost = socket.getLocalAddress().getHostAddress();
            localPort = socket.getLocalPort();
        } catch (IOException e) {
            Log.error(e);
        }
    }

    public String readLine(){
        try {
            return br.readLine();
        } catch (IOException e) {
            Log.error(e);
            throw new RuntimeException();
        }
    }

    public void println(String line){
        pw.println(line);
        pw.flush();
    }

    public String getTmpMsgTableName(){
        return tmpMsgTableName;
    }

    public void setTmpMsgTableName(String name){
        tmpMsgTableName = name;
    }

    public void close(){
        try {
            if(pw != null){
                pw.close();
            }
            if(br != null){
                br.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            Log.error(e);
        }
    }

    public String getName(){
        return localHost + ":" + localPort;
    }
}

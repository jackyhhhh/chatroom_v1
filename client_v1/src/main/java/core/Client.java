package core;

import context.AESOperator;
import context.Log;
import context.Order;
import manager.ConnManager;
import manager.DBManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Client extends JPanel {
    private static final BufferedImage background = loadImage("client.jpg");
    public static final int WIDTH = 544;
    public static final int HEIGHT = 850;
    private final ServerConnection sc;
    private final TypeArea ta;
    private int contentPos;
    private String notice = "";

    private int noticeCount = 1;

    public static BufferedImage loadImage(String fileName){
        try {
            return ImageIO.read(Objects.requireNonNull(Client.class.getClassLoader().getResourceAsStream(fileName)));
        } catch (IOException e) {
            Log.error(e);
        }
        return null;
    }

    Client(){
        sc = new ServerConnection();
        ta = TypeArea.getInstance();
        JTextArea jta = ta.getJTA();
        jta.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()){
                    sendMsgInTypeArea();
                }
            }
        });
        jta.setFocusable(true);
        this.setSize(WIDTH, HEIGHT);
        this.add(ta);
        this.setLayout(new FlowLayout(FlowLayout.LEADING, 15, 670));
    }

    public void sendMsgInTypeArea(){
        String msg = ta.getAreaText();
        if(! "".equals(msg)) {
            if(msg.length() > 30){
                notice = Log.now() + " 对不起, 消息不能超过30个字符 !!!";
                noticeCount = 0;
            }
            ConnManager.sendMsgToServer(sc, msg);
            ta.setAreaText("");
        }else {
            notice = Log.now() + " 对不起, 不能发送空白消息 !!!";
            noticeCount = 0;
        }
        repaint();
    }

    private void paintMsgContent(Graphics g, String msg){
        g.setColor(Color.BLUE);
        int len = msg.length();
        if(len <= 30){
            g.drawString(msg, 30, contentPos);
        }else if(len <= 80) {
            g.drawString(msg.substring(0, 30), 30, contentPos);
            contentPos += 20;
            g.drawString(msg.substring(30), 30, contentPos);
        }else{
            g.drawString(msg.substring(0, 30), 30, contentPos);
            contentPos += 20;
            g.drawString(msg.substring(30, 60), 30, contentPos);
            contentPos += 20;
            g.drawString(msg.substring(60), 30, contentPos);
        }
        g.setColor(Color.BLACK);
    }

    private void paintNew12Msg(Graphics g){
        ResultSet rs = DBManager.getNew12Msg(sc);
        contentPos = 30;
        try {
            if(rs != null){
                while(rs.next()){
                    String timestamp = rs.getString("_timestamp");
                    String msgContent = rs.getString("msg_content");
                    String host = rs.getString("host");
                    int port = rs.getInt("port");

                    String name = host + ":" + port;
                    if(name.equals(sc.getName())){
                        g.drawString("我("+name+")   "+timestamp+" :", 20, contentPos);
                    }else {
                        g.drawString("用户("+name+")   "+timestamp+" :", 20, contentPos);
                    }
                    contentPos += 20;
                    g.setFont(new Font("宋体",Font.BOLD,14));
                    paintMsgContent(g, msgContent);
                    g.setFont(new Font("宋体",Font.PLAIN,13));
                    contentPos += 30;
                }
            }
        } catch (SQLException sqlException) {
            Log.error(sqlException);
        }
    }

    @Override
    public void paint(Graphics g) {
        g.setFont(new Font("宋体",Font.PLAIN,13));
        g.drawImage(background, 0, 0, null);
        paintNew12Msg(g);
        if(notice.contains("断开") || notice.contains("对不起")){
            g.setColor(Color.RED);
        }else {
            g.setColor(Color.BLUE);
        }
        g.drawString(notice, 105,659);
        g.setColor(Color.BLACK);
    }

    public void start() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (x >= 409 && x <= 490 && y >= 775 && y <= 801) {
                    sendMsgInTypeArea();
                }

            }
        });

        JFrame frame = new JFrame("ChatRoom");
        frame.add(this);
        frame.setSize(WIDTH, HEIGHT);
        frame.setMaximizedBounds(new Rectangle(WIDTH, HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);

        String inLine;
        ConnManager.sendOrderToServer(sc, Order.DB_INFO);
        while (true) {
        try {
            inLine = sc.readLine();
        } catch (Exception e) {
            Log.error(e);
            break;
        }
        if (!"".equals(inLine)
                &&
            !("[ORDER]-"+Order.REFRESH).equals(inLine)
        ) {
            Log.info("[fr_server]-" + inLine);
            if (inLine.startsWith("[NOTICE]-")) {
                notice = inLine.substring(9);
                noticeCount = 1;
            }else if (inLine.startsWith("[TMP_TABLE]-")) {
                String name = inLine.substring(12);
                sc.setTmpMsgTableName(name);
            }else if (inLine.startsWith("[DB_INFO]-")) {
                String info = inLine.substring(10);
                String decryptedInfo = AESOperator.getInstance().decrypt(info);
                DBManager.connect(sc, decryptedInfo);
                Log.info("您已进入聊天室 !");
            }
        }
        repaint();
        if (noticeCount++ % 5 == 0) {
            notice = "";
        }
    }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;

public class Main {
    private static boolean run = true;
    private static boolean windowRun = false;
    private static int screenWidth;
    private static int screenHeight;
    private JLabel sbar;

    public static void main(String[] args) throws IOException {
        Main man = new Main();
        man.startWindow();
        man.getScreenSize();
        man.backProcess();


    }

    public String getTextFromClipboard() {
        String data = null;
        try {
            data = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);

        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }

        return data;
    }



    public void backProcess() {
        // might throw a UnsatisfiedLinkError if the native library fails to load or a RuntimeException if hooking fails
        GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true); // use false here to switch to hook instead of raw input

        System.out.println("Global keyboard hook successfully started, press [escape] key to shutdown. Connected keyboards:");
        for(Entry<Long,String> keyboard:GlobalKeyboardHook.listKeyboards().entrySet())
            System.out.format("%d: %s\n", keyboard.getKey(), keyboard.getValue());

        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override public void keyPressed(GlobalKeyEvent event) {
                //System.out.println(event.getVirtualKeyCode());
                if(event.getVirtualKeyCode()==event.VK_OEM_3 && event.isControlPressed() && event.isMenuPressed())
                    run = false;

                //keys to launch translate
                if(event.isShiftPressed() && event.isControlPressed() && event.isMenuPressed()) {
                    System.out.println("ctrl shift alt");
                    int check = '`';
                    System.out.println(check);
                    System.out.println(getTextFromClipboard());
                        createFrame(getTextFromClipboard());

                }
            }

            @Override public void keyReleased(GlobalKeyEvent event) {
                //System.out.println(event.getKeyChar());

            }

        });

        try {
            while(run) Thread.sleep(128);
        } catch(InterruptedException e) { /* nothing to do here */ }
        finally { keyboardHook.shutdownHook(); }

    }

    public void createFrame(String toTranslate) {
        JFrame mainFrame = new JFrame("Pocket translator");
        mainFrame.setLocation(screenWidth - 300, screenHeight - 350);
        mainFrame.setSize(300, 300);
        GroupLayout gl = new GroupLayout(mainFrame.getContentPane());
        GroupLayout.SequentialGroup sg = gl.createSequentialGroup();

        mainFrame.getContentPane().setLayout(gl);
        mainFrame.setLayout(new GridLayout(2,1));
        JLabel textToTranslate = new JLabel("", JLabel.CENTER);
        JLabel translatedText = new JLabel("", JLabel.CENTER);
        textToTranslate.setText("<html>"+toTranslate+"</html>");
        translatedText.setText(googleTranslateApi(toTranslate, "en", "pl"));
        sg.addComponent(textToTranslate).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(translatedText);


        //       mainFrame.pack();
//        JScrollPane scrollPane = new JScrollPane(textToTranslate);
//        JPanel jPanel = new JPanel();
//        jPanel.add(scrollPane);
//
//        mainFrame.add(scrollPane);
//        mainFrame.add(textToTranslate);
//        mainFrame.add(translatedText);

//        jPanel.add(textToTranslate);
//        jPanel.add(translatedText);
//        mainFrame.add(jPanel);


        if (!windowRun){
            windowRun = true;
            mainFrame.setVisible(true);

        try {
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mainFrame.dispose();
            windowRun = false;
        }
    }

    public void getScreenSize(){

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
         screenWidth = gd.getDisplayMode().getWidth();
         screenHeight = gd.getDisplayMode().getHeight();
    }
    
    
    public String googleTranslateApi(String text, String translateFrom, String translateTo) {
        String returnString = "";
 
        try {
            String textEncoded=URLEncoder.encode(text, "utf-8");
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl="+ translateFrom+"&tl="+translateTo+"&dt=t&q=" + textEncoded;
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();
                out.close();
 
                String aJsonString = responseString;
                
                aJsonString = aJsonString.replace("[", "");
                aJsonString = aJsonString.replace("]", "");
                aJsonString = aJsonString.substring(1);
                int plusIndex = aJsonString.indexOf('"');
                aJsonString = aJsonString.substring(0, plusIndex);
 
                returnString = aJsonString;
            } else{
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch(Exception e) {
            returnString = e.getMessage();
        }
 
        return returnString;
    }


    public void startWindow(){
        JFrame sFrame = new JFrame("Pocket translator");
        ButtonGroup group = new ButtonGroup();

        JRadioButton rb1 = new JRadioButton("Easy", true);
        JRadioButton rb2 = new JRadioButton("Medium");
        JRadioButton rb3 = new JRadioButton("Hard");

        group.add(rb1);
        group.add(rb2);
        group.add(rb3);

        sbar = new JLabel("selected");

        sFrame.setSize(350, 250);
        sFrame.setTitle("Radio buttons");
        sFrame.setLocationRelativeTo(null);
        sFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sFrame.setVisible(true);


    }


}


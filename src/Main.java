import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {
    private static boolean run = true;
    public static void main(String[] args) throws IOException {

        backProcess();
        // translate();

    }

    public static String getTextFromClipboard() {
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

    public static void translate() throws IOException {
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        try {
//            HttpGet httpget = new HttpGet("https://translate.google.com/#en/pl/do");
//
//            System.out.println("Executing request " + httpget.getRequestLine());
//
//            // Create a custom response handler
//            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
//
//                @Override
//                public String handleResponse(
//                        final HttpResponse response) throws ClientProtocolException, IOException {
//                    int status = response.getStatusLine().getStatusCode();
//                    if (status >= 200 && status < 300) {
//                        HttpEntity entity = response.getEntity();
//                        return entity != null ? EntityUtils.toString(entity) : null;
//                    } else {
//                        throw new ClientProtocolException("Unexpected response status: " + status);
//                    }
//                }
//
//            };
//            String responseBody = httpclient.execute(httpget, responseHandler);
//            System.out.println("----------------------------------------");
//            System.out.println(responseBody);
//            System.out.println("----------------------------------------");
//            getTranslateFromRegex(responseBody);
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            httpclient.close();
//        }

        Document doc = Jsoup.connect("https://translate.google.com/#en/pl/do").get();
        System.out.println(doc);




    }

    public static void getTranslateFromRegex(String line){

        // String to be scanned to find the pattern.
        //String line = "This order was placed for QT3000! OK?";
        String pattern = "(.m*)<span id=\"result_box\" class=\"short_text\" lang=\"pl\"><span>(.m*) </span></span>(.m*)";

        //String patt = "(.*)<!(.*)";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(line);
        if (m.find( )) {
            System.out.println("Found value: " + m.group(0) );
            System.out.println("Found value: " + m.group(1) );
            System.out.println("Found value: " + m.group(2) );
        }else {
            System.out.println("NO MATCH");
        }

    }

    public static void backProcess() {
        // might throw a UnsatisfiedLinkError if the native library fails to load or a RuntimeException if hooking fails
        GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true); // use false here to switch to hook instead of raw input

        System.out.println("Global keyboard hook successfully started, press [escape] key to shutdown. Connected keyboards:");
        for(Entry<Long,String> keyboard:GlobalKeyboardHook.listKeyboards().entrySet())
            System.out.format("%d: %s\n", keyboard.getKey(), keyboard.getValue());

        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override public void keyPressed(GlobalKeyEvent event) {
                // System.out.println(event);
                if(event.getVirtualKeyCode()==GlobalKeyEvent.VK_ESCAPE)
                    run = false;
                //keys to launch translate
                if(event.isShiftPressed() && event.isControlPressed() && event.isMenuPressed()) {
                    System.out.println("ctrl shift alt");
                    System.out.println(getTextFromClipboard());


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

}


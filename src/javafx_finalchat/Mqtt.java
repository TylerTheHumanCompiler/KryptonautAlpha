/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafx_finalchat;

import com.google.common.base.CharMatcher;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author fpiplip
 */
public class Mqtt {

    private static MqttClient client;

    protected static final List<String> contactsOnline = new ArrayList<>();
    public static final List<String> receivedMessages = new ArrayList<>();
    protected static String userhashstr;

    public static Listen lpcontacts = new Listen();




    Mqtt() throws MqttException, IOException, NoSuchAlgorithmException {

        String fingerprintuser = "delta";
        client = new MqttClient("tcp://iot.eclipse.org:1883", fingerprintuser);
        client.connect();
    }


    public static class Listen {

        public static  List<String> receivedMessages2 = new ArrayList<>();

        public void subscribe(String topic) throws MqttException {

            client.subscribe(topic);
            MqttCallback callback = new MqttCallback() {
                @Override
                public void connectionLost(Throwable thrwbl) {
                    //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws MqttException {
                    String byteload = new String(message.getPayload(), Charset.forName("ASCII"));
                    if (byteload.isEmpty() == true || byteload.matches("") == true || CharMatcher.ASCII.matchesAllOf(byteload) == false || byteload.startsWith("ERROR")) {} else {
                        System.out.println("\nreceived < " + topic + "\nMQTT PAYLOAD ARRIVED:\n********************\n" + byteload.substring(0, 76) + "\n\n");

                        synchronized (receivedMessages) {
                            receivedMessages.add(byteload);
                        }


                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken imdt) {
                    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            };
            client.setCallback(callback);
        }

        public static String receive2() {
            List<String> messages = new ArrayList<>();
            synchronized (Listen.receivedMessages2) {
                messages.addAll(Listen.receivedMessages2);
                Listen.receivedMessages2.clear();
            }
            String output = new String();
            for (String s : messages) {
                output += s;
            }
            return output;
        }

    }


    public static String receive() {
        List<String> messages = new ArrayList<>();
        synchronized (receivedMessages) {
            messages.addAll(receivedMessages);
            receivedMessages.clear();
        }
        String output = new String();
        for (String s : messages) {
            output += s;
        }
        return output;
    }






    public void Disconnect() throws MqttException {
        String topic_body = "cryptonautphi/";
        client.unsubscribe(topic_body);
        client.disconnect();
    }


    public static void sendMessage(String output, String topic) throws MqttException {
        MqttMessage message = new MqttMessage();
        String payload = output;
        System.out.println("send > " + topic + "\n\nMQTT SEND MESSAGE:\n****************\n" + payload + "\n\n");
        message.setPayload(payload.getBytes());
        client.publish(topic, message);
        //client.disconnect(); // wenn dieser hier, funzt event handler nicht auf key.Enter event, also nur 1x!
    }


    public static void authentifiktor() throws Exception {
        Thread rl = new RequestListener();
        rl.start();
    }



    public static void requestor() throws Exception {
        Thread t2 = new RequestPusher();
        t2.start();
    }




public String privateListeners() throws MqttException, InterruptedException, IOException {
    String requestcipher = receive();
    String requesthash = Cryptor.getClrtxt(requestcipher);
    return requesthash;

}


protected static int getIndexFromHash(String reqhash) throws IOException, NoSuchAlgorithmException {
    int j = 0;
    int max = Cryptor.getLastIndex();
    for (int i = 3; i < max; i++) {
        if (reqhash.matches(Cryptor.getFingerprint(i)) == true) {
            j = i;
            break;
        }
    }
    return j;
}


protected static String matchToken(int j) {
    String indexandtoken = "";
        synchronized (contactsOnline) {
            int snr = contactsOnline.size();
        for(int q = 0; q <= snr; q++) {

                if(contactsOnline.get(q).startsWith(Integer.toString(j)) == true) {
                    indexandtoken = contactsOnline.get(q);
                }
            }
        }

    return indexandtoken;
}

















 //   public void login2(/*String keystring*/) throws Exception {

  /*      Subscribe("ch/test/login/");

        MqttMessage message = new MqttMessage();

        byte[] pubkey = Cryptor.readfromIndex(1, "C:\\Users\\Skynet\\Java\\testk\\DECRYPT2.txt");
        System.out.println("\nOriginal: " + encode(pubkey));
        String pubclean = encode(pubkey).replaceAll("\n", ""); */
/*
        int i = pubkey.length;
        byte[] pukmod = new byte[(int) pubkey.length - 3];
        byte[] pukext = new byte[(int) 3];
        ByteArrayInputStream biomod = new ByteArrayInputStream(pubkey);
        biomod.read(pukmod, 0, (i-3));
        biomod.read(pukext, 0, 3);
        biomod.close();


        String pukmod64 = encode(pukmod);
        String pukmodcleaned = new String(pukmod64.replace("\n", "").replace("\r", ""));
        String pukext64 = encode(pukext);
        String pukextcleaned = new String(pukext64.replace("\n", "").replace("\r", ""));


*/

  //      String topic_body = "ch/test/login/";
//        String topic_ext = "fingerprint/";


   /*     String pem64 = encode(getKeyString("C:\\Users\\Skynet\\Java\\testk\\contact2-pub1k.der").getBytes());
        String keybody = pukmodcleaned + pukextcleaned;
        byte[] reformat = keybody.getBytes();

        String strclean;
        int n = reformat.length;
        int m = (n % 64) + n;
        ByteArrayInputStream bioref = new ByteArrayInputStream(reformat);
        byte[] bionew = new byte[(int) 63];
        int j = 0;
        do {
            bioref.read(bionew, 0, 63);
            String biostr = new String(bionew + "\n");
            strclean = "" + biostr;
            j = (j + 63);
            bionew = new byte[(int) 63];
        } while(j < n-63 == true);


        String pemkeystart = "-----BEGIN PUBLIC KEY-----\n";
        String pemkeyend = "-----END PUBLIC KEY-----";
        String pemkey = pemkeystart + strclean + "\n" + pemkeyend + "\n";
        System.out.println("\n------------------>" + pemkey + "\n");


*/
    /*    String pub64 = "-----BEGIN PUBLIC KEY-----\n" + encode(pubkey) + "\n-----END PUBLIC KEY-----";

        System.out.println("send > " + topic_body + pub64);
        message.setPayload(pub64.getBytes());
        client.publish(topic_body, message);
        client.unsubscribe("ch/test/login/");
        Subscribe("ch/test/" + topic_ext);
        String vericipher = Received();
        Thread.sleep(159);
        if(vericipher.isEmpty()) {vericipher = Received();
            System.out.println("\nVERITOKEN2: " + vericipher);}
        else{
        System.out.println("\nVERITOKEN: " + vericipher);}
    //    if(CharMatcher.ASCII.matchesAllOf(vericipher) == false || vericipher.equals("") == true || vericipher.isEmpty() == true) {} else {


        byte[] verbyte = vericipher.getBytes();
        int n = verbyte.length;
        n = n + 0;
        ByteArrayInputStream bioref = new ByteArrayInputStream(verbyte);
        byte[] bionew = new byte[(int) n];
        String strclean = "";
        int j;
        for(j = 0;  n - j > 76 ; j = j + 76)
        {   bioref.read(bionew, 0, 76);
            String byte2str = new String(bionew, Charset.forName("ASCII"));
            String bioadd = byte2str + "\n";
            strclean += bioadd;
            }
        bionew = new byte[(int) n];
        bioref.read(bionew, 0, (n - j));
        String byte2str = new String(bionew, Charset.forName("ASCII"));
        String bioadd = byte2str + "\n";
        strclean += bioadd;
        bioref.close();



        System.out.println("\nVERITOKEN: " + strclean);
            String verificationstring = Cryptor.getToken(vericipher);
            System.out.println(verificationstring);
            message.setPayload(verificationstring.getBytes("UTF-8"));
            client.publish("ch/test/" + topic_ext, message);
            String status = "online";
            message.setPayload(status.getBytes());
            client.publish("ch/test/" + topic_ext, message);
    //    }
    } */
}
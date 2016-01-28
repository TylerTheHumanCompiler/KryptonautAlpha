/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaChat;

import com.google.common.base.CharMatcher;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author fpiplip
 */
public class Mqtt {

    private static MqttClient client;

    private static final List<String> contactsOnline = new ArrayList<>();

    public static Listen lpcontacts = new Listen();




    Mqtt() throws MqttException, IOException {
        int userhashint = Cryptor.getFingerprint(1);
        String fingerprintuser = new Integer(userhashint).toString();
        client = new MqttClient("tcp://iot.eclipse.org:1883", fingerprintuser);
        client.connect();
    }


    public static class Listen {

        private static final List<String> receivedMessages = new ArrayList<>();

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
                    if (/*byteload.isEmpty() == true || byteload.matches("") == true || */CharMatcher.ASCII.matchesAllOf(byteload) == false || byteload.startsWith("ERROR")) {} else {
                        System.out.println("\nreceived < " + topic + "\n---------------------\nMQTT PAYLOAD ARRIVED:\n---------------------\n" + byteload + "\n\n\n\n");

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
        public String receive() {
            List<String> messages = new ArrayList<>();
            synchronized (receivedMessages) {
                messages.addAll(receivedMessages);
                receivedMessages.clear();
            }
            String output = "";
            for (String s : messages) {
                output += s;
            }
            return output;
        }
    }










    public void Disconnect() throws MqttException {
        String topic_body = "ch/test/phi/";
        String topic_ext = "jfx/";
        client.unsubscribe(topic_body + topic_ext);
        client.disconnect();
    }


    public void sendMessage(String output, String topic) throws MqttException {
        MqttMessage message = new MqttMessage();
        String payload = output;
        System.out.println("send > " + topic + "\n----------------\nMQTT SEND MESSAGE:\n----------------\n" + payload + "\n\n\n\n");
        message.setPayload(payload.getBytes());
        client.publish(topic, message);
        //client.disconnect(); // wenn dieser hier, funzt event handler nicht auf key.Enter event, also nur 1x!
    }

    public void login() throws Exception {
        int userhashint = Cryptor.getFingerprint(1);
        String userhashstr = new Integer(userhashint).toString();
        Listen ulp = new Listen();
        ulp.subscribe("cryptonautphi/" + userhashstr);

        int max = Cryptor.getLastIndex();

        for(int i = 3; i < max; i++) {
            int konhashint = Cryptor.getFingerprint(i);
            String konhashstr = new Integer(konhashint).toString();
            String topic = new String("cryptonautphi/" + konhashstr);
            /*
            int sharedint = userhashint * konhashint;
            String sharedintstr = new Integer(sharedint).toString();
            int sharedhash = sharedintstr.hashCode();
            String listentopic = new String("cryptonautphi/" + Integer.toString(sharedhash) + "/login");
            Subscribe(listentopic);*/
            sendMessage(Cryptor.getCipher(i, userhashstr), topic);
            TimeUnit.MILLISECONDS.sleep(50);
            String verifycip = ulp.receive();
            if(verifycip.isEmpty() == false) {
                String verifymsg = Cryptor.getClrtxt(verifycip);
                String verifytoken = verifymsg.substring((verifymsg.indexOf("#TOKEN:") + 7));
                String verifysender = verifymsg.substring(0, verifymsg.indexOf("#TOKEN:"));
                if (verifytoken.startsWith("VERIFIED:") == false) {
                    if (konhashstr.matches(verifysender) == false) {
                        System.out.println("QUEUE ERROR");/*
                String topic2 = new String("cryptonautphi/" + verifysender);
                sendMessage(Cryptor.getCipher(i, verifytoken), topic2);*/

                    } else {
                        String tokenreturn = new String(userhashstr + "#RETURN:" + verifytoken);
                        sendMessage(Cryptor.getCipher(i, tokenreturn), topic);
                        System.out.println("Verify Correct");
                    }
                }
                TimeUnit.MILLISECONDS.sleep(50);
                String confcip = ulp.receive();

                String confmsg = Cryptor.getClrtxt(confcip);
                String conftoken = confmsg.substring((confmsg.indexOf("#TOKEN:") + 7));
                String confsender = confmsg.substring(0, confmsg.indexOf("#TOKEN:"));
                if (conftoken.startsWith("VERIFIED:") == true) {
                    if (konhashstr.matches(confsender) == false) {
                        System.out.println("QUEUE ERROR");

                    } else {
                        String addtolist = new String(Integer.toString(i) + "#TOPIC:" + verifytoken);
                        synchronized (contactsOnline) {
                            contactsOnline.add(addtolist);
                        }

                        lpcontacts.subscribe("cryptonautphi/" + verifytoken + "/");
                        System.out.println("Verify Correct");
                    }
                }
            }


        }
        do {
            String verifier = ulp.receive();
            if(verifier.isEmpty() == false) {
                String verifstr = Cryptor.getClrtxt(verifier);
                int j;
                for(int i = 3; i < max; i++) {
                    int verifhashint = Cryptor.getFingerprint(i);
                    String verifhashstr = new Integer(verifhashint).toString();
                    if(verifhashstr.matches(verifstr) == true) {
                        j = i;
                        String veritopic = new String("crypronautphi/" + verifstr);
                        SecureRandom random = new SecureRandom();
                        String token = new BigInteger(130, random).toString();
                        String tknmsg = new String(userhashstr + "#TOKEN:" + token);
                        sendMessage(Cryptor.getCipher(j, tknmsg), veritopic);
                        TimeUnit.MILLISECONDS.sleep(50);
                        String cipherrcv = ulp.receive();
                        String tokenrcv = Cryptor.getClrtxt(cipherrcv);
                        String tokentoverify = tokenrcv.substring((tokenrcv.indexOf("#RETURN:") + 8));
                        String tokensender = tokenrcv.substring(0, tokenrcv.indexOf("#RETURN:"));
                        if(tokensender.matches(verifstr) == false || tokensender.matches(verifhashstr) == false) {
                            System.out.println("Verify ERROR");
                        } else {
                            if(tokentoverify.matches(token) == false) {
                                System.out.println("TOKEN ERROR");
                            } else {
                                String confirmation = new String(userhashstr + "#TOKEN:VERIFIED:" + token);
                                sendMessage(Cryptor.getCipher(j, confirmation), veritopic);
                                String addtolist = new String(Integer.toString(j) + "#TOPIC:" + token);
                                synchronized (contactsOnline) {
                                    contactsOnline.add(addtolist);
                                }
                                lpcontacts.subscribe("cryptonautphi/" + token + "/");
                            }
                        }


                        break;
                    }
                }
                TimeUnit.MILLISECONDS.sleep(100);
            } else {
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } while(0 < 1);
    }




public String privateListeners() throws MqttException, InterruptedException, IOException {
    String requestcipher = lpcontacts.receive();
    String requesthash = Cryptor.getClrtxt(requestcipher);
    return requesthash;

}


protected int getIndexFromHash(String reqhash) throws IOException {
    int j = 0;
    int max = Cryptor.getLastIndex();
    for (int i = 3; i < max; i++) {
        if (reqhash.matches(new Integer(Cryptor.getFingerprint(i)).toString()) == true) {
            j = i;
            break;
        }
    }
    return j;
}


protected String matchToken(int j) {
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
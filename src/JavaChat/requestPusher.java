package JavaChat;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import static JavaChat.Mqtt.sendMessage;

/**
 * Created by Skynet on 30.01.2016.
 */
public class RequestPusher extends Thread {





    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(5000);

            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            String userhashstr = Cryptor.getFingerprint(1);

            Mqtt.Listen ulp = new Mqtt.Listen();
            ulp.subscribe("cryptonautphi/" + userhashstr + "/");





            int max = Cryptor.getLastIndex() - 1;


            for (int i = 3; i <= max; i++) {
                String konhashstr = Cryptor.getFingerprint(1);
                String topic = new String("cryptonautphi/" + konhashstr + "/");
                System.out.print("\n\nPUSHER\nReqiest SENT:" + userhashstr + " @topic: " + topic);
                String cipher = Cryptor.getCipher(i, userhashstr);
                sendMessage(cipher, topic);
                TimeUnit.MILLISECONDS.sleep(7900);
                String verifycip = waitForAnswer(ulp);
                if (verifycip.isEmpty() == false && verifycip.matches("#FAIL") == false) {

                    System.out.println("\n\nPUSHER\nverifcipher: " + verifycip);
                    String verifymsg = Cryptor.getClrtxt(verifycip);
                    System.out.println("\n\nPUSHER\nverifiymessage: " + verifymsg);
                    String verifysender = verifymsg.substring(verifymsg.indexOf("#TOKEN:"));
                    String verifytoken = verifymsg.substring((verifymsg.indexOf("#TOKEN:") + 7), verifymsg.length());
                    if (verifytoken.startsWith("VERIFIED:") == false) {
                        if (konhashstr.matches(verifysender) == false) {
                            System.out.println("QUEUE ERROR");/*
                String topic2 = new String("cryptonautphi/" + verifysender);
                sendMessage(Cryptor.getCipher(i, verifytoken), topic2);*/
                        } else {
                            String tokenreturn = new String(userhashstr + "#RETURN:" + verifytoken);
                            System.out.println("\n\nPUSHER\nReturn Token Sent :" + tokenreturn);
                            sendMessage(Cryptor.getCipher(i, tokenreturn), topic);
                            System.out.println("PUSHER\nVerify Correct");
                        }
                    }

                    String confcip = waitForAnswer(ulp);

                    if (confcip.isEmpty() == false && confcip.matches("#FAIL") == false) {
                        String confmsg = Cryptor.getClrtxt(confcip);
                        String conftoken = confmsg.substring((confmsg.indexOf("#TOKEN:") + 7));
                        String confsender = confmsg.substring(0, confmsg.indexOf("#TOKEN:"));
                        if (conftoken.startsWith("VERIFIED:") == true) {
                            if (konhashstr.matches(confsender) == false) {
                                System.out.println("QUEUE ERROR");

                            } else {
                                String addtolist = new String(Integer.toString(i) + "#TOPIC:" + verifytoken);
                                synchronized (Mqtt.contactsOnline) {
                                    Mqtt.contactsOnline.add(addtolist);
                                }

                                Mqtt.lpcontacts.subscribe("cryptonautphi/" + verifytoken + "/");

                                System.out.println("Verify Correct");
                            }
                        }
                    }
                }}
            }catch(MqttException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }catch(NoSuchAlgorithmException e){
                e.printStackTrace();
            }catch(InterruptedException e){
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
        }
    }

    public String waitForAnswer(Mqtt.Listen listenobject) throws InterruptedException {
        int cx = 0;
        String checkx;
        do {
            TimeUnit.MILLISECONDS.sleep(300);
            checkx = listenobject.receive2();
            if (checkx.isEmpty() == true) {cx++;
            } else {cx = 5;}
        } while (cx < 5);
        if(checkx.isEmpty() == false && checkx.matches("") == false && checkx.length() > 16) {
            return checkx;} else { checkx = "#FAIL";
            return checkx;}
    }


}

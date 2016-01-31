package JavaChat;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import static JavaChat.Cryptor.getFingerprint;
import static JavaChat.Mqtt.receive;
import static JavaChat.Mqtt.sendMessage;

/**
 * Created by Skynet on 29.01.2016.
 */
public class RequestListener extends Thread {


    public void run() {
        try {
            String userhashstr = Cryptor.getFingerprint(1);
            Mqtt.Listen listenerport2 = new Mqtt.Listen();
            listenerport2.subscribe("cryptonautphi/" + userhashstr + "/");



            System.out.print("♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥♥");
            int xxx = 1;

            while (true) {
                System.out.println("niet");
                String verifier = receive();
                if (verifier.isEmpty() == false && verifier.matches("") == false) {
                    System.out.println("\n\nLISTENER:\nRequestor\n" + verifier);
                    int inmax = Cryptor.getLastIndex();
                    int max = inmax - 1;
                    String verifstr = Cryptor.getClrtxt(verifier);
                    System.out.println("\n\nLISTENER:\nRequestor Encrypred\n" + verifstr);
                    int j = 0;
                    for (int i = 3; i <= max; i++) {
                        String verifhashstr = new String(getFingerprint(i));
                        if (verifhashstr.matches(verifstr) == true) {
                            j = i;
                            break;
                        }
                    }
                    if (j < 3) {
                        System.out.println("LISTENER:\nFINGERPRINT MISMATCH");
                    } else {
                        String veritopic = new String("crypronautphi/" + verifstr + "/");
                        SecureRandom random = new SecureRandom();
                        String token = new BigInteger(130, random).toString();
                        //    String token = longtoken.substring(0, 64);

                        String tknmsg = new String(userhashstr + "#TOKEN:" + token);
                                                                                                                        System.out.println("\n\nLISTENER:\nTOKEN SENT: " + tknmsg);
                        sendMessage(Cryptor.getCipher(j, tknmsg), veritopic);
                        TimeUnit.MILLISECONDS.sleep(500);
                        String cipherrcv = receive();
                        String tokenrcv = Cryptor.getClrtxt(cipherrcv);
                                                                                                                        System.out.println("\n\nLISTENER:\nTOKEN Received" + j + ": " + tokenrcv);
                        String tokentoverify = tokenrcv.substring((tokenrcv.indexOf("#RETURN:") + 8), tokenrcv.length());
                        String tokensender = tokenrcv.substring(0, tokenrcv.indexOf("#RETURN:"));

                    if (tokensender.matches(verifstr) == false) {
                        System.out.println("FINGERPRINT MISMATCH 2");
                    } else {
                        if (tokentoverify.matches(token) == false) {
                            System.out.println("TOKEN MISMATCH");
                        } else {
                            String confirmation = new String(Mqtt.userhashstr + "#TOKEN:VERIFIED:" + token);
                                                                                                                        System.out.println("\n\nConfirmation: " + confirmation);
                            sendMessage(Cryptor.getCipher(j, confirmation), veritopic);
                        }
                        System.out.println("REQUEST VERIFIED!");
                        String addtolist = new String(Integer.toString(j) + "#TOPIC:" + token);
                        synchronized (Mqtt.contactsOnline) {
                            Mqtt.contactsOnline.add(addtolist);
                        }

                        Mqtt.lpcontacts.subscribe("cryptonautphi/" + token + "/");}
                    }
                }
                if((xxx % 10 == 0) == true) {
                System.out.println("sleep" + xxx);}
                TimeUnit.MILLISECONDS.sleep(400);
                xxx++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
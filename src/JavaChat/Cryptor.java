
package JavaChat;


import gnu.crypto.sig.rsa.RSA;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gnu.crypto.util.Base64.decode;
import static gnu.crypto.util.Base64.encode;

/**
 *
 * @author Skynet
 */


public class Cryptor {

    protected static String userdata = "";


//GETPUBLICKEY "DER"
    protected static PublicKey getPublicDERKey(int i) throws Exception {

        byte[] redBytes = readfromIndex(i);
        String keystr = new String(redBytes);
    //    String extkey = keystr.substring((keystr.indexOf("Y-----") + 6), keystr.indexOf("-----END PUBLIC KEY-----"));
        System.out.println("\n\nEXTRACTED KEY: \n" + keystr.substring(0, 23) + "...");
        byte[] keyBytes = decode(keystr);
        X509EncodedKeySpec xspecs = new X509EncodedKeySpec(keyBytes);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(xspecs);
    }


    //GETPUBLICKEY "PGP"
    protected static PublicKey getPublicKey(String filename) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        System.out.println(encode(keyBytes));
     //   String keybody = keyBytes.toString().replace("-----BEGIN PGP PUBLIC KEY BLOCK-----\nVersion: GnuPG v2\n\n", "").replace("\n-----END PGP PRIVATE KEY BLOCK-----", "").replace("\n", "").replace("\r", "");
        int i = keyBytes.length;
        ByteArrayInputStream bio = new ByteArrayInputStream(keyBytes);
        byte[] biomod = new byte[(int) i-3];
        byte[] bioexp = new byte[(int) 3];
        bio.read(biomod, 0, i-3);
        bio.read(bioexp, 0, 3);
        bio.close();
        BigInteger modulo = new BigInteger(biomod);
        BigInteger exponent = new BigInteger(bioexp);
        RSAPublicKeySpec xspecs = new RSAPublicKeySpec(modulo, exponent);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(xspecs);
    }


//GETPRIVATEKEY "DER"
    private static PrivateKey getPrivateDERKey() throws Exception {

        byte[] redBytes = readfromIndex(2);
        String keystr = new String(redBytes);
        //    String extkey = keystr.substring((keystr.indexOf("Y-----") + 6), keystr.indexOf("-----END PUBLIC KEY-----"));
        System.out.println("\n\nEXTRACTED KEY: \n" + keystr.substring(0, 23) + "...\n..." + keystr.substring((keystr.length() - 17), keystr.length()));
        byte[] keyBytes = decode(keystr);
        PKCS8EncodedKeySpec xspecs = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePrivate(xspecs);
    }


    //GETPUBLICKEY "PGP"
    private static PrivateKey getPrivateKey(String privfile) throws Exception {
        File f = new File(privfile);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        int i = keyBytes.length;
        ByteArrayInputStream bio = new ByteArrayInputStream(keyBytes);
        byte[] biomod = new byte[(int) i-3];
        byte[] bioexp = new byte[(int) 3];
        bio.read(biomod, 0, i-3);
        bio.read(bioexp, 0, 3);
        bio.close();
    //    System.out.println(encode(biomod));
    //    System.out.println(encode(bioexp));
        BigInteger modulo = new BigInteger(biomod);
        BigInteger exponent = new BigInteger(bioexp);
        RSAPrivateKeySpec rsaspecs = new RSAPrivateKeySpec(modulo, exponent);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePrivate(rsaspecs);

    }


    protected static String getCipher(int i, String clrtxt) {
        try {
            if (!clrtxt.isEmpty()) {
                PublicKey pubkey = getPublicDERKey(i);
                byte[] cpt = clrtxt.getBytes(Charset.forName("UTF-8"));
/*          //<Gebastel

            IBlockCipher cipher = CipherFactory.getInstance("AES");
            Map attributes = new HashMap();
            attributes.put(IBlockCipher.CIPHER_BLOCK_SIZE, new Integer(128));
            attributes.put(IBlockCipher.KEY_MATERIAL, pubkey);
            cipher.init(attributes);
            int bs = cipher.currentBlockSize();
            byte[] ct = new byte[128 * pt.length];


            for (int i = 0; i + bs < pt.length; i += bs)
            {
                cipher.encryptBlock(pt, i, ct, i);
            }
        */
                Security.addProvider(new BouncyCastleProvider());
/*            PGPDigestCalculator sha1Calc =
                    new BcPGPDigestCalculatorProvider()
                            .get(HashAlgorithmTags.SHA1);
            PGPDigestCalculator sha256Calc =
                    new BcPGPDigestCalculatorProvider()
                            .get(HashAlgorithmTags.SHA256);
*/
                Cipher bcCipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
                bcCipher.init(Cipher.ENCRYPT_MODE, pubkey); /*new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256,
                    PSource.PSpecified.DEFAULT));*/
                byte[] ct = bcCipher.doFinal(cpt, 0, cpt.length);
                BigInteger m = new BigInteger(ct);
                BigInteger cryptm = RSA.encrypt(pubkey, m);
/*            ByteArrayInputStream cypherstream = new ByteArrayInputStream(RSA.encrypt(pubkey, m).toByteArray()); // = gnu.crypto.sig.rsa.RSA.encrypt(pubkey, m).toByteArray();
            byte[] cypherbyte = new byte[(int) RSA.encrypt(pubkey, m).toByteArray().length];
            cypherstream.read(cypherbyte);
            cypherstream.close();
*/
                byte[] cypherbyte = cryptm.toByteArray();
                String cYph3r64 = encode(cypherbyte);
                return cYph3r64;
            }
           } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } String errorbte = "ERROR";
        return errorbte;
    }





    public static String getClrtxt(String cpr64) {
        try {
                //System.out.println("\n\nGETCLRTEXT64:\n************\n" + cpr64.substring(15) + "...");
            byte[] cprbyte = decode(cpr64);
            BigInteger cphbint = new BigInteger(cprbyte);
            PrivateKey privk = getPrivateDERKey();
            BigInteger clrbint = RSA.decrypt(privk, cphbint);
            byte[] cpt = clrbint.toByteArray();
//SELFMADE GEBASTEL
/*            IBlockCipher cipher = CipherFactory.getInstance("AES");
            Map attributes = new HashMap();
            attributes.put(IBlockCipher.CIPHER_BLOCK_SIZE, 256);
            attributes.put(IBlockCipher.KEY_MATERIAL, getPrivateKey(privfile));
            cipher.init(attributes);
            int bs = cipher.currentBlockSize();
            byte[] ct = new byte[cpt.length];
            for (int i = 0; i + bs < cpt.length; i += bs)
            {
                cipher.decryptBlock(ct, i, cpt, i);
            }
*/         Security.addProvider(new BouncyCastleProvider());
     /*       Security.insertProviderAt(new com.sun.crypto.provider.SunJCE(),1);
            super();
            RSACore cipher = RSACore.initEncodeMode(key);
        //    Cipher bcCipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
         //   byte[] MAGICSTRING = new String("40156ca44f22db70b7f852eec9d57ce10000000913").getBytes();
       //     OAEPParameterSpec defaultOaepSpec = OAEPParameterSpec.DEFAULT;
        //    bcCipher.init(Cipher.DECRYPT_MODE, privk); /*, new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256,
         //           PSource.PSpecified.DEFAULT));
           // byte[] ct = bcCipher.doFinal(cpt, 0, cpt.length);

            MessageDigest sha1 = MessageDigest.getInstance("SHA256", "BC");
            byte[] digest = sha1.digest(cpt);
            ASN1ObjectIdentifier sha1oid_ = new ASN1ObjectIdentifier("1.3.14.3.2.26");

            AlgorithmIdentifier sha1aid_ = new AlgorithmIdentifier(sha1oid_, null);
            DigestInfo di = new DigestInfo(sha1aid_, digest);

            byte[] plainSig = di.getEncoded();
    */         Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, privk);
            byte[] ct  = cipher.doFinal(cpt);
            String ctb = new String(ct, Charset.forName("UTF-8"));
                System.out.println("\n\nDECIPHERED:\n**********\n" + ctb + "\n");
            return ctb;
        } catch (Exception ex) {
            Logger.getLogger(JavaFx_finalchat.class.getName()).log(Level.SEVERE, null, ex);
            String err = "ERROR";
            System.out.println(err);
        }
        String err = "ERROR";
        return err;
    }

/* //NODERED ÜBERBLIBSU

    protected static String getToken(String cpr64) {
        try {
            byte[] cprBint = decode(cpr64);
            BigInteger cphBint = new BigInteger(cprBint);
            String privfile = "C:\\Users\\Skynet\\Java\\testk\\DECRYPT2.txt";
            PrivateKey privk = getPrivateDERKey();
            BigInteger clrbint = RSA.decrypt(privk, cphBint);
            Security.addProvider(new BouncyCastleProvider());
            Cipher c = Cipher.getInstance("RSA/NONE/NOPADDING", "BC");
/*             OAEPParameterSpec oaepSpec = new OAEPParameterSpec("SHA-256",
               "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
*/ /*
               c.init(Cipher.DECRYPT_MODE , privk);
        //    c.init(2, getPrivateKey(privfile));
            byte[] token = c.doFinal(clrbint.toByteArray());


            return new String(token);

                } catch (Exception ex) {
                Logger.getLogger(JavaFx_finalchat.class.getName()).log(Level.SEVERE, null, ex);
        String err = "ERROR";
        System.out.println(err);
            return err;
        }}
*/


    protected static void createKeyPair(/*String name, */String passwd, String filename) throws InvalidKeySpecException, IOException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException {
        try {
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            keyGen.initialize(2048, random);
            KeyPair pair = keyGen.generateKeyPair();
            Key publicKey = pair.getPublic();
            Key privateKey = pair.getPrivate();
            KeyFactory fact = KeyFactory.getInstance("RSA");
            String userstr1 = new String("#1" + encode(publicKey.getEncoded()) + "\n#2" + encode(privateKey.getEncoded()) + "\n#3");
            DESKeySpec dks = new DESKeySpec(passwd.getBytes());
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            SecretKey desKey = skf.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            InputStream is = new ByteArrayInputStream(userstr1.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            javax.crypto.CipherInputStream cis = new javax.crypto.CipherInputStream(is, cipher);
            FileOutputStream os = new FileOutputStream("C:\\Users\\Skynet\\Java\\testk\\" + filename + ".phi");
            byte[] bytes = new byte[64];
            int numBytes;
            while ((numBytes = cis.read(bytes)) != -1) {
                os.write(bytes, 0, numBytes);
            }
            os.flush();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }




    public static byte[] readfromIndex(int i/*, String file*/) throws IOException {

        String index = new String("#" + i);
        int j = i + 1;
        String indpls1 =new String("#" + j);
        String inpstr = userdata;
        String output = inpstr.substring((inpstr.indexOf(index) + 2), inpstr.indexOf(indpls1));
        System.out.println("INDEX: " + i + "\n" + output.substring(0, 23) + "...");
        byte[] indexBytes = output.getBytes();
        return indexBytes;
    }




    protected static byte[] encryptFile(String passwd, String file) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IOException {

        DESKeySpec dks = new DESKeySpec(passwd.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        InputStream is = new FileInputStream(file);
        cipher.init(Cipher.ENCRYPT_MODE, desKey);
        javax.crypto.CipherInputStream cis = new javax.crypto.CipherInputStream(is, cipher);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes2 = new byte[64];
        int numBytes2;
        while ((numBytes2 = cis.read(bytes2)) != -1) {
            bos.write(bytes2, 0, numBytes2);
        }
        byte[] filedata = bos.toByteArray();
        return filedata;
    }




    protected static void decryptFile(String passwd, String file) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IOException {

        DESKeySpec dks = new DESKeySpec(passwd.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        InputStream is = new FileInputStream(file);
        cipher.init(Cipher.DECRYPT_MODE, desKey);
        javax.crypto.CipherInputStream cis = new javax.crypto.CipherInputStream(is, cipher);
        FileOutputStream os = new FileOutputStream("C:\\Users\\Skynet\\Java\\testk\\OUTPUTFILE.txt");
        byte[] bytes = new byte[64];
        int numBytes;
        while ((numBytes = cis.read(bytes)) != -1) {
            os.write(bytes, 0, numBytes);
        }
        os.flush();
    }




    protected static void decryptLoginFile(String passwd, String file) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IOException {

        DESKeySpec dks = new DESKeySpec(passwd.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        InputStream is = new FileInputStream(file);
        cipher.init(Cipher.DECRYPT_MODE, desKey);
        javax.crypto.CipherInputStream cis = new javax.crypto.CipherInputStream(is, cipher);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes2 = new byte[64];
        int numBytes2;
        while ((numBytes2 = cis.read(bytes2)) != -1) {
            bos.write(bytes2, 0, numBytes2);
        }
        userdata = new String(bos.toByteArray(), Charset.forName("UTF-8"));
        System.out.println("userdata success\n\n");
    }




    protected static void addContact(String passwd, String contactpubkey, String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {

        String userstr = userdata;
        int l = userstr.length();
        int i = Character.getNumericValue(userstr.charAt(l - 1));
        int j = i + 1;
        String usernew = new String(userstr + contactpubkey + "#" + Integer.toString(j));
        userdata = new String(usernew);
        System.out.println("\n\nADDKONTAKT:\n\n" + userdata.substring(0, 768) + "...\n..." + userdata.substring((userdata.length() - 512), userdata.length()));
        DESKeySpec dks = new DESKeySpec(passwd.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        InputStream is = new ByteArrayInputStream(usernew.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, desKey);
        javax.crypto.CipherInputStream cis = new javax.crypto.CipherInputStream(is, cipher);
        FileOutputStream os = new FileOutputStream("C:\\Users\\Skynet\\Java\\testk\\" + filename + ".phi");
        byte[] bytes = new byte[64];
        int numBytes;
        while ((numBytes = cis.read(bytes)) != -1) {
            os.write(bytes, 0, numBytes);
        }
        os.flush();
    }




    protected static int getLastIndex() throws IOException {

        String userstr = userdata;
        int l = userstr.length();
        int i = Character.getNumericValue(userstr.charAt(l - 1));
        return  i;
    }



    protected static String getFingerprint(int i) throws IOException, NoSuchAlgorithmException {

        byte[] keyBytes = readfromIndex(i);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(keyBytes);
        String fpdigested = encode(messageDigest.digest());
        String fp64 = fpdigested.replace("/", "").replace("+", "");
        String fingerprint = new String();
        if(fp64.endsWith("=")) {
            fingerprint = fp64.substring(0, fp64.indexOf("="));
        } else {
            fingerprint = fp64;
        }
        System.out.println("\n\nFINGERPINT" + i + ": " + fingerprint);
        return fingerprint;
    }
}

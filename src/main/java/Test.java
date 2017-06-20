/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author probook
 */
public class Test {
    
    public static void mainj(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, FileNotFoundException, IOException, InvalidKeySpecException{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        FileOutputStream privateOut = new FileOutputStream("private.key");
        FileOutputStream publicOut = new FileOutputStream("public.key");
        IOUtils.write(publicKey.getEncoded(), publicOut);
        IOUtils.write(privateKey.getEncoded(), privateOut);
        privateOut.close();
        publicOut.close();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec x509KeySpec = new PKCS8EncodedKeySpec(FileUtils.readFileToByteArray(new File("private.key")));
        privateKey = keyFactory.generatePrivate(x509KeySpec);
        X509EncodedKeySpec x509KeySpec2 = new X509EncodedKeySpec(FileUtils.readFileToByteArray(new File("public.key")));
        publicKey = keyFactory.generatePublic(x509KeySpec2);
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] str = c.doFinal("{a:koufana crepin sosthene,b:12/02/2015,c:validation,d:CMDLP,e:100000,f:observation}".getBytes("UTF-8"));
        String st = Base64.encodeBase64String(str);
        System.out.println(st);
        System.out.println(st.length());
        
        c.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] rs = c.doFinal(Base64.decodeBase64(""+st+"554"));
        System.out.println(new String(rs));
        System.out.println(new String(rs).length());
    }
    
}

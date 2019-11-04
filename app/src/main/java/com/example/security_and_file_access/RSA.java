package com.example.security_and_file_access;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

class RSA {

    private PrivateKey PrivateKey = null;
    private PublicKey PublicKey = null;

    private Context context;

    RSA() {


    }

    void setContext(Context context) {
        this.context = context;
    }

    private void setPrivateKeyString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encodedPrivateKey = stringToBytes(key);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        this.PrivateKey = keyFactory.generatePrivate(privateKeySpec);
    }

    private void setPublicKeyString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] encodedPublicKey = stringToBytes(key);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        this.PublicKey = keyFactory.generatePublic(publicKeySpec);
    }

    private String getPrivateKeyString() {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(this.PrivateKey.getEncoded());
        return bytesToString(pkcs8EncodedKeySpec.getEncoded());
    }

    private String getPublicKeyString() {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(this.PublicKey.getEncoded());
        return bytesToString(x509EncodedKeySpec.getEncoded());
    }


    void genKeyPair() throws NoSuchAlgorithmException {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.genKeyPair();

        PublicKey publicKey = kp.getPublic();

        this.PrivateKey = kp.getPrivate();
        this.PublicKey = publicKey;
    }

    String Encrypt(String plain) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] encryptedBytes;

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.PublicKey);
        encryptedBytes = cipher.doFinal(plain.getBytes());

        return bytesToString(encryptedBytes);

    }

    private String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    private byte[] stringToBytes(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }


    void saveToDiskPrivateKey() {
        try {
            FileOutputStream outputStream;
            outputStream = this.context.openFileOutput(Strings.privateKeyPath, Context.MODE_PRIVATE);
            outputStream.write(this.getPrivateKeyString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d("RSA:", "Error write PrivateKey");
        }
    }

    void saveToDiskPublicKey() {
        try {
            FileOutputStream outputStream;
            outputStream = this.context.openFileOutput(Strings.publicKeyPath, Context.MODE_PRIVATE);
            outputStream.write(this.getPublicKeyString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d("RSA:", "Error write Public");
        }
    }

    void openFromDiskPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        String content = this.readFileAsString(Strings.publicKeyPath);
        this.setPublicKeyString(content);
    }

    void openFromDiskPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        String content = this.readFileAsString(Strings.privateKeyPath);
        this.setPrivateKeyString(content);
    }


    private String readFileAsString(String filePath) throws IOException {

        BufferedReader fin = new BufferedReader(new InputStreamReader(context.openFileInput(filePath)));
        String txt = fin.readLine();
        fin.close();
        return txt;

    }

}
package com.example.security_and_file_access;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PushbackReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView text;
    private TextView textEncoded;
    private TextView textDecoded;

    public MainActivity() throws NoSuchAlgorithmException, NoSuchPaddingException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.text = findViewById(R.id.editText);
        this.textEncoded = findViewById(R.id.textView);
        this.textDecoded = findViewById(R.id.textView2);
        this.button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crypt(v);
            }
        });

    }
    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("password.xml", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("password.xml");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
//                    stringBuilder.append(receiveString);
                    System.out.println(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public String createXML(String userText, String userTextCrypt, Context context) throws IOException {
        String xml = "";
        xml += "\t<data>\n" +
                "\t\t<text>" + userText + "</text>\n" +
                "\t\t<cipher_text>" + userTextCrypt + "</cipher_text>\n" +
                "\t</data>\n" +
                "</content_file>\n";

        InputStream inputStream = context.openFileInput("password.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String input = "";

        while ((line = reader.readLine()) != null) {

            if (line.equals("</content_file>")) {
                input += xml;
            } else {
                input += line + "\n";
            }
        }
        return input;
    }

    public void crypt(View view) {

        try {
            String password = this.text.getText().toString();
            String encodedText;
            String decodedText;
            RSA encodeRsa = new RSA();

            encodeRsa.setContext(getBaseContext());
            encodeRsa.genKeyPair(1024);
            encodeRsa.saveToDiskPrivateKey("rsa.pri");
            encodeRsa.saveToDiskPublicKey("rsa.pub");
            encodedText = encodeRsa.Encrypt(password);
            textEncoded.setText(encodedText);

            RSA decodeRsa = new RSA();

            decodeRsa.setContext(getBaseContext());
            decodeRsa.openFromDiskPrivateKey("rsa.pri");
            decodeRsa.openFromDiskPublicKey("rsa.pub");
            decodedText = decodeRsa.Decrypt(encodedText);
            textDecoded.setText(decodedText);

            writeToFile(createXML(password, encodedText, getApplicationContext()), getApplicationContext());
            readFromFile(getApplicationContext());

        } catch (Exception e) {
            System.out.println("An error ocurred encrypting the password");
        }


    }
}
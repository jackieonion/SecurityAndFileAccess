package com.example.security_and_file_access;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText textInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.textInput = findViewById(R.id.editText);
        Button button = findViewById(R.id.button2);
        Button registerListButton = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crypt(v);
            }
        });
        registerListButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity();
            }
        });
    }

    private void startActivity(){
        Intent intent = new Intent(this, RegisterListActivity.class);
        startActivity(intent);
    }

    private void writeToFile(String data ) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplication().openFileOutput("data.xml", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private boolean checkFile() throws IOException{
        boolean isCreated;
        try{
            InputStream inputStream = getApplication().openFileInput("data.xml");
            inputStream.close();
            isCreated = true;
        }
        catch (IOException error){
            Log.i("Exception", error.toString());
            isCreated = false;
        }
        return isCreated;
    }

    private void createFile(String password, String encodedPassword){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss", Locale.FRANCE);
        Date date = new Date(System.currentTimeMillis());
        String head = "<?xml version="+ '"' + "1.0"+ '"' + "encoding=" + '"' + "UTF-8" + '"' + "?>\n" +
                "<content_file>\n" +
                "\t<data id="+ '"' + 1 + '"' +">\n" +
                "\t\t<time>" + formatter.format(date) + "</time>\n" +
                "\t\t<textInput>" + password + "</textInput>\n" +
                "\t\t<cipher_text>" + encodedPassword + "</cipher_text>\n" +
                "\t</data>\n" +
                "</content_file>\n";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplication().openFileOutput("data.xml", Context.MODE_PRIVATE));
            outputStreamWriter.write(head);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String addData(String userText, String userTextCrypt ) throws IOException {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss", Locale.FRANCE);
        Date date = new Date(System.currentTimeMillis());
        int idCount = 1;
        String xml = "";
        InputStream inputStream = getApplication().openFileInput("data.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder input = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            if(line.contains("id=")){
                idCount++;
            }
            if (line.equals("</content_file>")) {
                xml += "\t<data id="+'"'+ idCount + '"' +">\n" +
                        "\t\t<time>" + formatter.format(date) + "</time>\n" +
                        "\t\t<textInput>" + userText + "</textInput>\n" +
                        "\t\t<cipher_text>" + userTextCrypt + "</cipher_text>\n" +
                        "\t</data>\n" +
                        "</content_file>\n";
                input.append(xml);
            } else {
                input.append(line).append("\n");
            }
        }
        return input.toString();
    }

    private void dataAdded(){
        Toast.makeText(this,"Data added correctly", Toast.LENGTH_SHORT)
                .show();
    }

    public void crypt(View view) {

        try {
            String password = this.textInput.getText().toString();
            String encodedText;
            RSA encodeRsa = new RSA();

            encodeRsa.setContext(getBaseContext());
            encodeRsa.genKeyPair(1024);
            encodeRsa.saveToDiskPrivateKey("rsa.pri");
            encodeRsa.saveToDiskPublicKey("rsa.pub");
            encodedText = encodeRsa.Encrypt(password);

            RSA decodeRsa = new RSA();

            decodeRsa.setContext(getBaseContext());
            decodeRsa.openFromDiskPrivateKey("rsa.pri");
            decodeRsa.openFromDiskPublicKey("rsa.pub");

            if(checkFile()){
                writeToFile(addData(password, encodedText));
                textInput.setText("");
                dataAdded();
            }
            else{
                createFile(password, encodedText);
                textInput.setText("");
                dataAdded();
            }

        } catch (Exception e) {
            System.out.println("An error ocurred encrypting the password");
        }
    }
}
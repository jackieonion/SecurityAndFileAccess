package com.example.security_and_file_access;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Strings {
    final static String fileName = "data.xml";
    final static String dateFormatPattern = "yyyy-MM-dd 'at' HH:mm:ss";
    final static String successfulToastMessage = "Data added correctly";
    final static String registerIsEmpty = "Register can not be empty";
    final static String privateKeyPath = "rsa.pri";
    final static String publicKeyPath = "rsa.pub";
    final static String errorEncryptingText = "An error occurred encrypting the password";
    final static String errorWritingText = "File writing failed: ";
}

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
                encodeData(v);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textInput.getWindowToken(), 0);
            }
        });
        registerListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
            }
        });
    }

    private void startActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplication().openFileOutput(Strings.fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", Strings.errorWritingText + e.toString());
        }
    }

    private boolean checkFile() {
        try {
            InputStream inputStream = getApplication().openFileInput(Strings.fileName);
            inputStream.close();
            return true;
        } catch (IOException error) {
            Log.i("Exception", error.toString());
            return false;
        }
    }

    private void createFile(String password, String encodedPassword) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss", Locale.FRANCE);
        Date date = new Date(System.currentTimeMillis());
        String head = "<?xml version=" + '"' + "1.0" + '"' + "encoding=" + '"' + "UTF-8" + '"' + "?>\n" +
                "<content_file>\n" +
                "\t<data id=" + '"' + 1 + '"' + ">\n" +
                "\t\t<time>" + formatter.format(date) + "</time>\n" +
                "\t\t<textInput>" + password + "</textInput>\n" +
                "\t\t<cipher_text>" + encodedPassword + "</cipher_text>\n" +
                "\t</data>\n" +
                "</content_file>\n";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplication().openFileOutput(Strings.fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(head);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", Strings.errorWritingText + e.toString());
        }
    }

    public String xmlFormat(String userText, String userTextCrypt) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat(Strings.dateFormatPattern, Locale.FRANCE);
        Date date = new Date(System.currentTimeMillis());
        int idCounter = 1;
        String xml = "";
        InputStream inputStream = getApplication().openFileInput(Strings.fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder input = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            if (line.contains("id=")) {
                idCounter++;
            }
            if (line.equals("</content_file>")) {
                xml += "\t<data id=" + '"' + idCounter + '"' + ">\n" +
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

    private void showStatusToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
                .show();
    }

    public void encodeData(View view) {

        try {
            String password = this.textInput.getText().toString();
            String encodedText;
            RSA encodeRsa = new RSA();

            encodeRsa.setContext(getBaseContext());
            encodeRsa.genKeyPair();
            encodeRsa.saveToDiskPrivateKey();
            encodeRsa.saveToDiskPublicKey();
            encodedText = encodeRsa.Encrypt(password);

            RSA decodeRsa = new RSA();

            decodeRsa.setContext(getBaseContext());
            decodeRsa.openFromDiskPrivateKey();
            decodeRsa.openFromDiskPublicKey();

            if(!password.isEmpty()){
            if (checkFile(Strings.fileName)) {
                writeToFile(xmlFormat(password, encodedText));
                textInput.setText("");
            } else {
                createFile(password, encodedText);
                textInput.setText("");
            }
            showStatusToast(Strings.successfulToastMessage);
            }
            else{
                showStatusToast(Strings.registerIsEmpty);
            }

        } catch (Exception e) {
            System.out.println(Strings.errorEncryptingText);
        }
    }
}
package com.example.security_and_file_access;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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

    public void crypt(View view) {

        try {

            //Obtenemos el texto desde el cuadro de texto
            String original = this.text.getText().toString();


            RSA rsa = new RSA();

            //le asignamos el Contexto
            rsa.setContext(getBaseContext());

            //Generamos un juego de claves
            rsa.genKeyPair(1024);

            //Guardamos en la memoria las claves
            rsa.saveToDiskPrivateKey("rsa.pri");
            rsa.saveToDiskPublicKey("rsa.pub");

            //Ciframos
            String encode_text = rsa.Encrypt(original);

            //Mostramos el texto cifrado
            textEncoded.setText(encode_text);


            //Creamos otro objeto de nuestra clase RSA
            RSA rsa2 = new RSA();

            //Le pasamos el contexto
            rsa2.setContext(getBaseContext());

            //Cargamos las claves que creamos anteriormente
            rsa2.openFromDiskPrivateKey("rsa.pri");
            rsa2.openFromDiskPublicKey("rsa.pub");

            //Desciframos
            String decode_text = rsa2.Decrypt(encode_text);

            //Mostramos el texto ya descifrado
            textDecoded.setText(decode_text);
        } catch (Exception e) {

        }


    }
}
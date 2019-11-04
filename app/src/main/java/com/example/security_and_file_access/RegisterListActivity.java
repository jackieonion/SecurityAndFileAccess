package com.example.security_and_file_access;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RegisterListActivity extends AppCompatActivity {

    ArrayList<RegisterModel> registerList = new ArrayList<>();
    RegisterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_list);
        try {
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerViewRegister = findViewById(R.id.recyclerViewRegister);
        recyclerViewRegister.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RegisterAdapter(registerList);
        recyclerViewRegister.setAdapter(adapter);
    }

    private void loadData() throws IOException {
        RegisterModel registerModel;
        int idCounter = 1;
        String id = "", password = "", date = "", encodedPassword = "";
        InputStream inputStream = getApplication().openFileInput("data.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            if(line.contains("id=")){
                id = idCounter + "";
                idCounter ++;
            }
            else if (line.contains("time")) {
                date = line;
            }
            else if(line.contains("text")){
                password = line;
            }
            else if(line.contains("cipher_text")){
                encodedPassword = line;
            }
        }
        registerModel = new RegisterModel(id, password, date, encodedPassword);
        registerList.add(registerModel);
    }
}

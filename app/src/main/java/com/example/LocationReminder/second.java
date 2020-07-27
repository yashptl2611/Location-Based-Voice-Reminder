package com.example.LocationReminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class second extends AppCompatActivity {

    TextView textView;
    TextInputEditText remind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textView = findViewById(R.id.location);
        remind = findViewById(R.id.remind);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(second.this,third.class);
                intent.putExtra("remind", Objects.requireNonNull(remind.getText()).toString());
                startActivity(intent);
            }
        });
    }
}

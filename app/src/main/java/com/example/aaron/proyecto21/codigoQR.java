package com.example.aaron.proyecto21;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aaron.proyecto21.carpetaQR.IntentIntegrator;
import com.example.aaron.proyecto21.carpetaQR.IntentResult;

/**
 * Created by Aaron on 09/02/2017.
 */

public class codigoQR extends AppCompatActivity {

    public static String marca2;
    public String finalJuego="FELICIDADEEEES!!, HAS COMPLETADO EL JUEGO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_qr);
        configureButtonReader();
     Button botonMarca2 = (Button)findViewById(R.id.button1);
        botonMarca2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(marca2.equals("Encontrado")){
                    alertDialogo();
                }else{
                    Intent databack= new Intent();
                    databack.putExtra("marca2",marca2);
                    setResult(RESULT_OK,databack);
                    finish();
                }
            }
        });

    }

    private void alertDialogo() {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("¡¡¡LO HAS CONSEGUIDO !!!");
        build.setMessage(finalJuego);
        build.setPositiveButton("Aceptar",null);
        build.create();
        build.show();
    }

    private void configureButtonReader() {
        final ImageButton buttonReader = (ImageButton)findViewById(R.id.imageButton);
        buttonReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(codigoQR.this).initiateScan();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        handleResult(scanResult);
    }

    private void handleResult(IntentResult scanResult) {
        if (scanResult != null) {
            updateUITextViews(scanResult.getContents(), scanResult.getFormatName());
        } else {
            Toast.makeText(this, "No se ha leído nada ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUITextViews(String scan_result, String scan_result_format) {
        final TextView tvResult =(TextView)findViewById(R.id.tvResult);

        tvResult.setText(scan_result);
        Linkify.addLinks(tvResult, Linkify.ALL);
    }


}

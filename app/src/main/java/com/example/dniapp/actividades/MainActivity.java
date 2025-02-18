package com.example.dniapp.actividades;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.dniapp.R;
import com.example.dniapp.beans.Dni;
import com.example.dniapp.beans.DniX;
import com.example.dniapp.beans.DniY;
import com.example.dniapp.beans.DniZ;
import com.example.dniapp.util.Preferencias;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    public static final String TAG_APP = "DNI_APP";
    private RadioButton radioButtonSeleccionado;
    private EditText caja_dni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.caja_dni = findViewById(R.id.dni);
        int id_radio = Preferencias.obtenerRadioActivo(this);
        if (id_radio==0)//no había guardado
        {
            this.radioButtonSeleccionado = findViewById(R.id.radio1);
        }
        else{
            this.radioButtonSeleccionado = findViewById(id_radio);
            this.radioButtonSeleccionado.setChecked(true);

        }

        String ultimo_dni = Preferencias.obtenerUltimoDNI(this);
        this.caja_dni.setText(ultimo_dni);


    }


    private Dni crearObjetoDni (int num_dni)
    {
        Dni dni = null;

        switch (this.radioButtonSeleccionado.getId())
        {
            case R.id.radio1: dni = new Dni(num_dni);//nacional
                break;
            case R.id.radio2: dni = new DniX(num_dni);//X
                break;
            case R.id.radio3: dni = new DniY(num_dni);//Y
                break;
            case R.id.radio4: dni = new DniZ(num_dni);//Z
                break;
        }

        return dni;
    }

    public void calcularLetraDni(View view) {
        Log.d(TAG_APP, "Ha tocado el botón de calcular");
        //TODO
        //1 obtener el número de dni introducido por el usario
        EditText caja_dni = findViewById(R.id.dni);
        String sdni = caja_dni.getText().toString();
        //2 Casting de String a Int //NO ESTOY VALIDANDO LA ENTRADA
        int num_dni = Integer.parseInt(sdni);
        //2.1 Creo el objeto DNI nuevo
        Dni dni = crearObjetoDni(num_dni);
        //3 calcular letra
        char letra_dni = dni.calculaLetra();
        Log.d(TAG_APP, "Letra calculada = "+letra_dni);

        dni.setLetra(letra_dni);

        //4 Serializo objeto DNI a string con formato JSON utilizando la libreria GSON
        Gson gson =new Gson();
        String str_dni_json = gson.toJson(dni);
        Log.d(TAG_APP, "JSON del DNI: " + str_dni_json);

        //5 escribir strig JSON en fichero SharedPreferences
        int contadorDNI = Preferencias.obtenerContadorDNIJSON(this);
        contadorDNI = contadorDNI + 1;
        Preferencias.guardarContadorDNIJSON( this, contadorDNI);
        Log.d(TAG_APP, "SE GUARDA CONTADOR DE DNIS: " + contadorDNI);

        Preferencias.guardarDNIJSON(this, String.valueOf(contadorDNI), str_dni_json);
        Log.d(TAG_APP, "SE GUARDA CLAVE: " + contadorDNI + " VALOR: " + str_dni_json);

        //6 Lanzo la actividad del resultado pasándole la letra
        Intent intent = new Intent(this, AnimacionLetraActivity.class);
        intent.putExtra("LETRA", letra_dni);
        startActivity(intent);
        caja_dni.setText("");
    }


    private String obtenerDni ()
    {
        String dni_actual = null;

            EditText editText = findViewById(R.id.dni);
            dni_actual = editText.getText().toString();

        return dni_actual;
    }

    //este método se invoca cuando el usuario pulsa el  botón de hacia atrás
    @Override
    public void onBackPressed() {
        Log.d(TAG_APP, "El usuario le ha dado para atrás");
        mostrarDialogoGuardar();
    }


    public void tocadoRadio(View view) {
        Log.d(TAG_APP, "Tocó RadioButton");
        this.radioButtonSeleccionado = (RadioButton)view;
    }


    private void mostrarDialogoGuardar ()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Desea guardar los cambios?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG_APP, "Tocó SÍ");
                dialog.dismiss();
                String dni_guardar = MainActivity.this.obtenerDni();
                Preferencias.guardarDNI(MainActivity.this, dni_guardar);
                Preferencias.guardarRadioActivo(MainActivity.this, MainActivity.this.radioButtonSeleccionado.getId());
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.d(TAG_APP, "Tocó NO");
                dialog.cancel();
                Preferencias.guardarDNI(MainActivity.this, "");
                Preferencias.guardarRadioActivo(MainActivity.this, 0);
                MainActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

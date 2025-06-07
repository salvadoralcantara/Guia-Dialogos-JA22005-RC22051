package sv.edu.ues.usoonclick;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgviewFoto;
    private Uri imgURI;
    private EditText tvestadomodo;
    private Switch swModo;
    private CheckBox chkboxdesamodo;
    private ListView lstdepartamentos;
    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        lstdepartamentos = findViewById(R.id.lstDepartamentos);
        imgviewFoto = findViewById(R.id.ivImagenLogo);
        tvestadomodo = findViewById(R.id.edtEstadoModo);
        swModo = findViewById(R.id.SwCambiarModo);
        chkboxdesamodo = findViewById(R.id.chboxDesactivacambiomodo);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        ImageButton imgbuttonfecha = findViewById(R.id.imgButtonFecha);
        ImageButton imgbuttonhora = findViewById(R.id.imgButtonHora);

        if (savedInstanceState != null) {
            String uriGuardada = savedInstanceState.getString("img_uri");
            if (uriGuardada != null) {
                imgURI = Uri.parse(uriGuardada);
                imgviewFoto.setImageURI(imgURI);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        items = new ArrayList<>();
        items.add("1 - Ahuachapán");
        items.add("2 - Santa Ana");
        items.add("3 - Sonsonate");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lstdepartamentos.setAdapter(adapter);

        swModo.setOnClickListener(view -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Cambiando modo")
                    .setMessage("Se cambiará el modo de la aplicación")
                    .setPositiveButton("OK", null)
                    .show();

            boolean estado = swModo.isChecked();
            if (estado) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                tvestadomodo.setText("Modo oscuro está activado");
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                tvestadomodo.setText("");
            }
        });

        chkboxdesamodo.setOnClickListener(view -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Confirmar cambio de modo")
                    .setMessage("¿Está seguro que desea deshabilitar el cambio de modo?")
                    .setPositiveButton("Sí", (dialog, i) ->
                            swModo.setEnabled(!chkboxdesamodo.isChecked()))
                    .setNegativeButton("No", (dialog, i) ->
                            chkboxdesamodo.setChecked(!chkboxdesamodo.isChecked()))
                    .show();
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radbtnOcultarLista) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialogo_personalizado, null);
                EditText razonOcultar = dialogView.findViewById(R.id.edtJustificacion);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Razón para ocultar")
                        .setView(dialogView)
                        .setPositiveButton("Registrar", (dialog, which) -> {
                            String razon = razonOcultar.getText().toString();
                            tvestadomodo.setText(razon);
                            imgviewFoto.setVisibility(View.INVISIBLE);
                        })
                        .setNegativeButton("Cancelar", (dialog, i) -> tvestadomodo.setText(""))
                        .show();
            } else {
                imgviewFoto.setVisibility(View.VISIBLE);
                tvestadomodo.setText("");
            }
        });

        lstdepartamentos.setOnItemClickListener((adapterView, view, position, id) -> {
            TextView itemSeleccionado = (TextView) view;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Ingrese el nuevo valor para el elemento seleccionado");

            final EditText nuevoTexto = new EditText(MainActivity.this);
            nuevoTexto.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(nuevoTexto);

            builder.setPositiveButton("Actualizar", (dialog, which) ->
                    itemSeleccionado.setText(nuevoTexto.getText().toString()));
            builder.setNegativeButton("Cancelar", null);
            builder.show();
        });

        imgviewFoto.setOnClickListener(view -> openGallery());

        imgbuttonfecha.setOnClickListener(view -> {
            LocalDate hoy = LocalDate.now();
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                        tvestadomodo.setText("Fecha: " + fecha);
                    },
                    hoy.getYear(), hoy.getMonthValue() - 1, hoy.getDayOfMonth());
            datePickerDialog.show();
        });

        imgbuttonhora.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int hora = calendar.get(Calendar.HOUR_OF_DAY);
            int minutos = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                    (view1, hourOfDay, minute) -> {
                        String horaStr = hourOfDay + ":" + String.format("%02d", minute);
                        tvestadomodo.setText("Hora: " + horaStr);
                    }, hora, minutos, true);
            timePickerDialog.show();
        });

        fab.setOnClickListener(view -> finish());
    }

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgURI = data.getData();
            imgviewFoto.setImageURI(imgURI);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imgURI != null) {
            outState.putString("img_uri", imgURI.toString());
        }
    }
}

package es.vcarmen.reproductor;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static es.vcarmen.reproductor.Reproductor.mp;

public class MainActivity extends AppCompatActivity {
ListView lv;
    String [] items;
    ImageButton Cerrar_app;
    Boolean CancionEncontrada=false;
    ArrayList<File> canciones = new ArrayList<File>();
    private static final int RECOGNIZE_SPEECH_ACTIVITY = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=(ListView)findViewById(R.id.playList);
        //utilizamos el metodo encontrar canciones para que busque en la tarjeta sd para almacenar las canciones
        canciones=EncontrarCanciones(Environment.getExternalStorageDirectory());


        items = new String[canciones.size()];
        for (int i=0;i< canciones.size();i++)
        {
            //para que no muestre la terminacion mp3
        items[i] =canciones.get(i).getName().toString().replace(".mp3","").replace(".vav","").toLowerCase();
        }
        //Añadimos las canciones al xml creando un adaptador y asignandoselo al listview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.canciones,R.id.textView,items);
        lv.setAdapter(adapter);
        //creamos el evento para que cuando pinchemos en una cancion se reproduzca
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startActivity(new Intent(getApplicationContext(),Reproductor.class).putExtra("pos",position).putExtra("canciones",canciones));

            }
        });



    }
    public ArrayList<File> EncontrarCanciones(File f){
        ArrayList<File> canciones= new ArrayList<File>();
        File[] archivos = f.listFiles();
        for (File lista : archivos ) {
    if (lista.isDirectory() && !lista.isHidden())
    {
        //Hacemos recursividad para que vaya de carpeta en carpeta
        canciones.addAll(EncontrarCanciones(lista));

        }
        else{
    //obtenemos todos los archivos que sean mp3 o vav
            if(lista.getName().endsWith(".mp3") || lista.getName().endsWith(".vav"))
            {
//si encuentra algun archivo mp3 o vav lo añade al array
                canciones.add(lista);

            }
}
        }
        return canciones;
    }


    //RECONOCIMIENTO DE VOZ


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RECOGNIZE_SPEECH_ACTIVITY:

                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> speech = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String leido = speech.get(0);

                    //grabar.setText(strSpeech2Text);
                    Toast toast1;
                    String can="";
                    int pos=0;

                    List<String>listaleidas= new ArrayList<>();
                    String[] palabrasbusqueda=leido.split(" ");
                    for (String cancion:items){
                        for (int i=0; i<palabrasbusqueda.length;i++){
                            if(cancion.toLowerCase().contains(palabrasbusqueda[i].toLowerCase())){
                                listaleidas.add(cancion);
                                can=cancion;

                                Log.d("canciones",""+cancion);
                                break;

                            }
                            else{

                            }

                        }


                    }

                    int aleatorio=0;
                    int posicion=0;
                    Log.d("hellou",""+listaleidas.size());
                    for (int i=0; i<items.length;i++){

                        if(items[i]==can){
                            posicion=i;

                        }
                    }

                    aleatorio=(int)(Math.random() * listaleidas.size());
                    Log.d("aleatorio",""+aleatorio);
                    if (listaleidas.size() != 0) {
                        toast1 = Toast.makeText(getApplicationContext(),
                                leido, Toast.LENGTH_SHORT);
                        Log.d("can2",""+can);

                        startActivity(new Intent(getApplicationContext(),Reproductor.class).putExtra("pos",posicion).putExtra("canciones",canciones));

                    }
                        else{
                        toast1 = Toast.makeText(getApplicationContext(),
                                "Cancion no encontrada", Toast.LENGTH_SHORT);


                    }


                    toast1.show();


                }

                break;
            default:

                break;
        }
    }

    public void onClickImgBtnHablar(View v) {

        Intent intentActionRecognizeSpeech = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Configura el Lenguaje (Español-México)
        intentActionRecognizeSpeech.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-MX");
        try {
            startActivityForResult(intentActionRecognizeSpeech,
                    RECOGNIZE_SPEECH_ACTIVITY);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    //salta aqui si el dispositivo no tiene reconocimiento de voz por la version de android
                    "Tú dispositivo no soporta el reconocimiento por voz",
                    Toast.LENGTH_SHORT).show();
        }

    }


}

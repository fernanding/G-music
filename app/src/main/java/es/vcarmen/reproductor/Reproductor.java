package es.vcarmen.reproductor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

public class Reproductor extends AppCompatActivity implements View.OnClickListener {

    static MediaPlayer mp;
    ArrayList<File> canciones;
    int posicion;
    Uri u;
    String aux="";
AudioManager audioManager;

    ImageButton btnatcan,btnadecan,btnplay,btnretro,btnadelan,btnplayList;
    public TextView t1,t2,nombreCan;
    SeekBar sb,sb_volumen;

    Thread actualizarSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);
       btnatcan=(ImageButton) findViewById(R.id.atrascancion);
        btnadecan=(ImageButton) findViewById(R.id.adelantecancion);
        btnplay=(ImageButton) findViewById(R.id.play);
        btnretro=(ImageButton) findViewById(R.id.retroceder);
        btnadelan=(ImageButton) findViewById(R.id.adelantar);
        btnplayList=(ImageButton) findViewById(R.id.btn_lista);

        btnatcan.setOnClickListener(this);
        btnadecan.setOnClickListener(this);
        btnplay.setOnClickListener(this);
        btnretro.setOnClickListener(this);
        btnadelan.setOnClickListener(this);
        btnplayList.setOnClickListener(this);
        sb=(SeekBar)findViewById(R.id.barra);


        t2=(TextView) findViewById(R.id.tiempo2);
        t1=(TextView) findViewById(R.id.tiempo1);
        nombreCan=(TextView)findViewById(R.id.nombreCancion);


      //metodo para ver el proceso de la cancion
        actualizarSeekbar = new Thread(){
            @Override
            public void run(){
                super.run();
                int duracion=mp.getDuration();
                sb.setMax(duracion);
                int posicionActual=0;

               // boolean ban=false;
                while (posicionActual<duracion)
                {

                    try {
                        sleep(500);
                        posicionActual= mp.getCurrentPosition();
                        sb.setProgress(posicionActual);
                        aux=calcularTiempo(posicionActual);


                       // t2.setText("hola");
                   } catch (InterruptedException e) {

                    }
                }
            }

        };

        if(mp!=null)
        {
            mp.stop();
        }
        try {
            //Obtenemos el nombre de la cancion con el intent y la seleccionamos

            Intent i=getIntent();
            Bundle b=i.getExtras();
            canciones=(ArrayList)b.getParcelableArrayList("canciones");
            posicion=(int) b.getInt("pos",0);
            u= Uri.parse(canciones.get(posicion).toString());
            nombreCan.setText(canciones.get(posicion).getName().toString());//Extrae el nombre de la cancion
            mp= MediaPlayer.create(getApplication(),u);
            //una vez seleccionada la cancion vamos actualizando el seekbar y reproducimos la cancion con el mediaplayer
            actualizarSeekbar.start();
            mp.start();
            Volumen();
            t2.setText(calcularTiempo(mp.getDuration()));


        }catch (Exception e)
        {

        }
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                       t1.setText(aux.toString().trim());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            mp.seekTo(seekBar.getProgress());
            }
        });


    }

    private String calcularTiempo(int milisegundos)
    {
        int segundos =(int)(milisegundos/1000)% 60;
        int minutos =(int)(milisegundos/(1000*60))% 60;
        int horas =(int)(milisegundos/(1000*60*60))% 24;
String aux="";
        aux=((horas<10) ? "0"+horas:horas)+":"+((minutos<10)?"0"+minutos:minutos)+":" +((segundos<10)?"0"+segundos:segundos);
        return aux;

    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        switch (id) {
            case R.id.play:
            if(mp.isPlaying()){
              btnplay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
              mp.pause();
        }else{
                btnplay.setImageResource(R.drawable.ic_pause_black_24dp);
        mp.start();
        }
         break;
            case R.id.retroceder:
                mp.seekTo(mp.getCurrentPosition()-5000);
                break;
            case R.id.adelantar:
                mp.seekTo(mp.getCurrentPosition()+5000);
                break;
            case R.id.atrascancion:
                SiguienteCancion();
                break;
            case R.id.adelantecancion:
                AnteriorCancion();
               break;
            case R.id.btn_lista:


                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            break;
        }

    }

    public void SiguienteCancion() {
        mp.stop();
        posicion = (posicion + 1) % canciones.size();
       // nombreCan.setText(calcularTiempo(mp.getDuration()));
        nombreCan.setText(canciones.get(posicion).getName().toString());
        u = Uri.parse(canciones.get(posicion).toString());
        mp = MediaPlayer.create(getApplicationContext(), u);

        mp.start();
        sb.setMax(0);//calcula la duracion maxima al seekbar
        t2.setText(calcularTiempo(mp.getDuration()));//muestra el tiempo que dura la cancion

        try {
            sb.setMax(mp.getDuration()); //envia la duracion maxima al seekbar
        } catch (Exception e) {

        }
    }

  public void AnteriorCancion(){

  mp.stop();//para la cancion que esta reproduciendose
      if(posicion-1<0){//si la posicion es 0 va a la ultima cancion
          posicion=canciones.size()-1;
      }else
      {
          posicion=posicion-1;
      }
  nombreCan.setText(canciones.get(posicion).getName().toString());

      u=Uri.parse(canciones.get(posicion).toString());
      mp= MediaPlayer.create(getApplicationContext(),u);
      mp.start();
      sb.setMax(0);
      t2.setText(calcularTiempo(mp.getDuration()));//muestra el tiempo que dura la cancion

      sb.setMax(mp.getDuration());//agrega la duracion al seekbar


  }
  public void Volumen(){
      try {
          sb_volumen=(SeekBar) findViewById(R.id.sb_audio);
          audioManager =(AudioManager) getSystemService(Context.AUDIO_SERVICE);
          sb_volumen.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));//Calcula el maximo de volumen
          sb_volumen.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));//Capturamos el volumen que tiene el telefono

          sb_volumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
              @Override
              public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                  audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
              }

              @Override
              public void onStartTrackingTouch(SeekBar seekBar) {

              }

              @Override
              public void onStopTrackingTouch(SeekBar seekBar) {

              }
          });



      }catch (Exception e)
      {
          e.printStackTrace();
      }


  }

}


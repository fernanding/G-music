package es.vcarmen.reproductor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

public class PrincipalActivity extends Activity {
    private final int Duracion=4000;
    ImageView image;
    ImageView image2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_principal);
        image= (ImageView)findViewById(R.id.imageView1);
        image2= (ImageView)findViewById(R.id.imageView2);


        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(PrincipalActivity.this,MainActivity.class);
                startActivity(intent);



                finish();
            }
        },Duracion);
    }
}

package com.maha.leviathan.monstershooter;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beyondar.android.fragment.BeyondarFragment;
import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.BeyondarGLSurfaceView;
import com.beyondar.android.view.OnTouchBeyondarViewListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    //    Variabel untuk menampilkan dunia AR dalam aplikasi
    BeyondarFragmentSupport mBeyondarFragment;
    World world;

    //    Variabel untuk widget pada layout yang telah dibuat sebelumnya
    TextView skorText, countDown, info, Textdurasi, skorFinal;
    ImageView cross;
    Button bMenu;
    RelativeLayout skorFinalLayout;

    //    Variabel untuk keperluan operasional game,
    //    mulai dari mengatur posisi monster berdasarkan radius, mengatur gameplay, dll
    Timer spawn = new Timer();
    Random random = new Random();
    double radiusInDegrees = 20 / 111000f, w, t;
    int skorInt = 0, mulai = 0, count = 5, durasi, id = 0;

    //    Variabel array yang berisi monster-monster yang akan ditampilkan
    int[] img = {R.drawable.monster1,
            R.drawable.monster2,
            R.drawable.monster3,
            R.drawable.monster4,
            R.drawable.monsterboss
    };

    //    Variabel arraylist yang digunakan pada event touch listener
    ArrayList<GeoObject> geoObjArr = new ArrayList<GeoObject>();
    ArrayList<GeoObject> geoObjMenu = new ArrayList<GeoObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        skorText = (TextView) findViewById(R.id.textViewSkor);
        skorFinal = (TextView) findViewById(R.id.textViewSkorFinal);
        skorFinalLayout = (RelativeLayout) findViewById(R.id.skorFinal);
        cross = (ImageView) findViewById(R.id.imageView);
        countDown = (TextView) findViewById(R.id.textViewCountDown);
        info = (TextView) findViewById(R.id.textView2);
        Textdurasi = (TextView) findViewById(R.id.textView);
        bMenu = (Button) findViewById(R.id.buttonBToMenu);

        bMenu.setVisibility(View.INVISIBLE);
        countDown.setText("");
        skorText.setText("0");
        skorFinal.setText("");
        skorFinalLayout.setVisibility(View.INVISIBLE);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int xMax = size.x;
        final int yMax = size.y;

        final MediaPlayer gun = MediaPlayer.create(this, R.raw.gun);
        final MediaPlayer tada = MediaPlayer.create(this, R.raw.tada);
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        world = new World(this);
        world.setDefaultImage(R.mipmap.ic_launcher);
        world.setGeoPosition(-8.691782, 115.223726);
        mBeyondarFragment.setWorld(world);

        bMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDown.setText("");
                backToMenu();
            }
        });

        mBeyondarFragment.setOnTouchBeyondarViewListener(new OnTouchBeyondarViewListener() {
            @Override
            public void onTouchBeyondarView(MotionEvent motionEvent, BeyondarGLSurfaceView beyondarGLSurfaceView) {
                try {
                    gun.reset();
                    gun.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.gun));
                    gun.prepare();
                    gun.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                v.vibrate(50);

                ArrayList<BeyondarObject> geoObjects = new ArrayList<BeyondarObject>();

                Log.e("INI RESOLUSI X", String.valueOf(xMax/2));
                Log.e("INI RESOLUSI Y", String.valueOf(yMax/2));

                beyondarGLSurfaceView.getBeyondarObjectsOnScreenCoordinates(xMax/2, yMax/2, geoObjects);

                if(geoObjects.isEmpty() == false){
                    if (geoObjects.get(0).getId() == 1000001 || geoObjects.get(0).getId() == 1000002){
                        for (int i = 0; i<geoObjMenu.size(); i++){
                            geoObjMenu.get(i).setGeoPosition(0,0);
                        }
                        startGame();
                    }
                    else if (geoObjects.get(0).getId() == 1000003 || geoObjects.get(0).getId() == 1000004){
                        finish();
                    }
                    else {
                        if (mulai == 1){
                            GeoObject g = (GeoObject) geoObjects.get(0);
                            g.setGeoPosition(0,0);
                            skorInt++;
                            skorText.setText(String.valueOf(skorInt));
                        }
                    }
                }
            }
        });

        spawn.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mulai == 1){
                            cross.setVisibility(View.VISIBLE);
                            Log.e("Mulai", "1");
                            if (count == 0){
                                if (durasi == 0){
                                    Textdurasi.setText(String.valueOf(durasi));
                                    for (int i = 0; i<geoObjArr.size(); i++){
                                        geoObjArr.get(i).setImageResource(R.drawable.up);
                                    }
                                    bMenu.setVisibility(View.VISIBLE);
                                    try {
                                        tada.reset();
                                        tada.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tada));
                                        tada.prepare();
                                        tada.start();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    stopGame();
                                }
                                else {
                                    countDown.setText("");
                                    info.setText("");
                                    Textdurasi.setText(String.valueOf(durasi));
                                    durasi--;

                                    if(durasi % 2 == 0){
                                        summonMons();
                                    }
                                    else {
                                        summonMons();
                                        summonMons();
                                    }
                                }
                            }
                            else {
                                cross.setVisibility(View.INVISIBLE);
                                Textdurasi.setText(String.valueOf(durasi));
                                count--;
                                countDown.setText(" "+String.valueOf(count)+" ");
                                info.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            Log.e("Mulai", "0");
                        }
                    }
                });
            }
        }, 0, 1000);

        geoMenu();
    }

    @Override
    protected void onStop() {
        spawn.cancel();
        super.onStop();
    }

    public void newObjAug(double lat, double lng, String al, int stat, int mon){
        GeoObject go = new GeoObject(id);
        String altitudenya = "";
        if(stat == 0){
            altitudenya = altitudenya+"-";
        }
        altitudenya = altitudenya+"0.0000"+al;
        go.setGeoPosition(lat, lng, Double.parseDouble(altitudenya));

        go.setImageResource(img[mon]);
        geoObjArr.add(go);
        world.addBeyondarObject(go);
    }

    public void summonMons(){
        w = radiusInDegrees * Math.sqrt(random.nextDouble());
        Log.e("w", String.valueOf(w));

        t = 2 * Math.PI * random.nextDouble();
        Log.e("t", String.valueOf(t));

        double lat1 = w * Math.cos(t);
        double lat2 = w * Math.sin(t);

        double new_lat1 = lat1 / Math.cos(-8.691782);

        double foundLongitude = new_lat1 + 115.223726;
        double foundLatitude = lat2 + -8.691782;

        int al = random.nextInt(8 - 0 + 1);
        int stat = random.nextInt(1 - 0 + 1);
        //ganti ini kalo nambah monster
        int mon = random.nextInt(4 - 0 + 1);
        Log.e("AL", String.valueOf(al));
        Log.e("STAT", String.valueOf(stat));
        newObjAug(foundLatitude, foundLongitude, String.valueOf(al), stat, mon);
    }

    public void geoMenu(){

        //  Menu start pertama
        GeoObject str1 = new GeoObject(1000001);
        str1.setGeoPosition(-8.691733, 115.223723, 0.00002);
        str1.setImageResource(R.drawable.start);
        geoObjMenu.add(str1);
        world.addBeyondarObject(str1);

        //  Menu start kedua
        GeoObject str2 = new GeoObject(1000002);
        str2.setGeoPosition(-8.691824, 115.223724, 0.00002);
        str2.setImageResource(R.drawable.start);
        geoObjMenu.add(str2);
        world.addBeyondarObject(str2);

        //  Menu exit pertama
        GeoObject stp1 = new GeoObject(1000003);
        stp1.setGeoPosition(-8.691733, 115.223723, 0.000005);
        stp1.setImageResource(R.drawable.exit);
        geoObjMenu.add(stp1);
        world.addBeyondarObject(stp1);

        //  Menu exit kedua
        GeoObject stp2 = new GeoObject(1000004);
        stp2.setGeoPosition(-8.691824, 115.223724, 0.000005);
        stp2.setImageResource(R.drawable.exit);
        geoObjMenu.add(stp2);
        world.addBeyondarObject(stp2);
    }

    public void startGame(){
        mulai = 1;
        durasi = 30;
        count = 5;
        skorInt = 0;
    }

    public void stopGame(){
        cross.setVisibility(View.INVISIBLE);
        skorFinal.setText(String.valueOf(skorInt));
        skorFinalLayout.setVisibility(View.VISIBLE);
        skorText.setText("0");
        mulai = 0;
    }

    public void backToMenu(){
        for (int i = 0; i<geoObjArr.size(); i++){
            geoObjArr.get(i).setGeoPosition(0,0);
        }
        geoObjMenu.get(0).setGeoPosition(-8.691733, 115.223723, 0.00002);
        geoObjMenu.get(1).setGeoPosition(-8.691824, 115.223724, 0.00002);
        geoObjMenu.get(2).setGeoPosition(-8.691733, 115.223723, 0.000005);
        geoObjMenu.get(3).setGeoPosition(-8.691824, 115.223724, 0.000005);
        bMenu.setVisibility(View.INVISIBLE);
        skorFinalLayout.setVisibility(View.INVISIBLE);
        cross.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

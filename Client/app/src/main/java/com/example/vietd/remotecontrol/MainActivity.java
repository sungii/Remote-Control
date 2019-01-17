package com.example.vietd.remotecontrol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.io.DataOutputStream;
import java.util.Base64;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Base64.Decoder;

public class MainActivity extends FragmentActivity
        implements mainFragment.OnFragmentInteractionListener, menuFragment.OnFragmentInteractionListener, HelpFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mainFragment fragment = new mainFragment();
        fragmentTransaction.add(R.id.view_group_id, fragment);
        fragmentTransaction.commit();*/

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        CollectionPagerAdapter collectionPagerAdapter =
                new CollectionPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);

        //Add Tabs
        collectionPagerAdapter.addFragment(new mainFragment(), "Video");
        collectionPagerAdapter.addFragment(new menuFragment(), "Menu");
        collectionPagerAdapter.addFragment(new HelpFragment(), "Help");

        mViewPager.setAdapter(collectionPagerAdapter);

        /*mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });*/
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    //WARNING TO ME: IF YOU NEED DIFFERENT INTERACTION THEN YOU HAVE TO DEFINE METHODS FOR ALL FRAGMENTACTIONLISTENER
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //Connect to Server
    /*public void connectToServer() throws IOException {
        final String ip_address = getIntent().getExtras().getString("ip_address");
        final int port = getIntent().getExtras().getInt("port");


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Create Streams
                Socket socket = null;
                DataInputStream data_in = null;
                DataOutputStream data_out = null;
                try {
                    socket = new Socket(ip_address, port);
                    data_in = new DataInputStream(socket.getInputStream());
                    data_out = new DataOutputStream(socket.getOutputStream());


                    //Declare all important variables
                    int size, readBytes;
                    Decoder decoder = Base64.getDecoder();

                    //Get width and height of display
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                    final int height = displayMetrics.heightPixels;
                    final int width = displayMetrics.widthPixels;

                    while (true) {
                        //Connection shouldn't close
                        data_out.write(1);
                        try {
                            //Create image
                            size = data_in.readInt();
                            byte[] img_bytes = new byte[size];
                            readBytes = 0;
                            while (readBytes != size)
                                readBytes += data_in.read(img_bytes, readBytes, size - readBytes);

                            //Decode sent image bytes
                            img_bytes = decoder.decode(img_bytes);

                            //Show Image
                            final Bitmap bitmap = BitmapFactory.decodeByteArray(img_bytes, 0, img_bytes.length); //Array length of img_bytes increased cause of Decoding

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageView imageView = findViewById(R.id.screenModeView);
                                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                        data_in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();
    }*/


}




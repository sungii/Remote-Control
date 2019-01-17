package com.example.vietd.remotecontrol;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

public class startActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Start MainActivity
        Button connect_btn = (Button) findViewById(R.id.connect_btn);
        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMainActivity(v);
            }
        });


    }

    public void switchToMainActivity(View v){
        Intent intent = new Intent(this, MainActivity.class);

        //Pass Data to main Activity
        EditText ipAdress_editText = findViewById(R.id.ip_adress_editText);
        EditText port_editText = findViewById(R.id.port_nr_editText);
        String str_port = port_editText.getText().toString();

        String ip_address = ipAdress_editText.getText().toString();
        int port = Integer.parseInt(str_port);

        connectionValid(ip_address, port, intent);
    }

    private void connectionValid(final String ip_address, final int port, final Intent intent){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try{
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(ip_address, port), 5000);
                }catch(UnknownHostException e){
                    e.printStackTrace();
                }catch(IOException e){
                    e.printStackTrace();
                }catch(SecurityException e){
                    e.printStackTrace();
                }catch(IllegalArgumentException e){
                    e.printStackTrace();
                }finally{
                    if(socket.isConnected()){
                        try{
                            socket.close();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                        intent.putExtra("ip_address", ip_address);
                        intent.putExtra("port", port);
                        startActivity(intent);
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView warning = findViewById(R.id.warning);
                                warning.setText(getResources().getString(R.string.warning));
                            }
                        });

                    }
                }

            }
    });
            thread.start();
    }
}

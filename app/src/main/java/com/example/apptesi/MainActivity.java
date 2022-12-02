package com.example.apptesi;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.eclipse.paho.android.service.*;
import org.eclipse.paho.client.mqttv3.*;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button btn_sub;
    private Button btn_unsub;
    private Button btn_pub;


    private static final String TAG = "MainActivity";
    private String topic, clientID;
    private MqttAndroidClient client;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    public void init() {
        btn_sub = findViewById(R.id.btn_sub);
        btn_pub = findViewById(R.id.btn_pub);
        btn_unsub = findViewById(R.id.btn_unsub);

        clientID = MqttClient.generateClientId();
        topic="prova/topic";
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.1.25:1883",
                        clientID);
        btn_sub.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    connectx();
                }
        });
        btn_pub.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                publish();
            }
        });
        btn_unsub.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                unsub();
            }
        });

    }

    private void connectx(){
        try {

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("admin");
            options.setPassword("admin".toCharArray());
            IMqttToken token = client.connect(options);



            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    sub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG,  "onFailure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sub(){
        try{
            client.subscribe(topic, 0);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG,"Message:"+ new String(message.getPayload()));

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });


        }catch(MqttException e){
            e.printStackTrace();

        }
    }

    private void publish(){
        String payload = "the payload";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    private void unsub(){
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                    Log.d(TAG,  "Unsub: onSucces");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d(TAG,  "Unsub: onFailure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


}
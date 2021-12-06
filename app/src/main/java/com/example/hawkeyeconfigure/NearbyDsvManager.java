package com.example.hawkeyeconfigure;

import android.content.Context;

import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;


/**
 * Created by Sonaal on 30-08-2019.
 */



public class NearbyDsvManager {

    private static final String TAG = "Nearby Dsv";
    private final EventListener listener;
    Context ctx;
    public NearbyDsvManager(final EventListener listener, Context ctx) {
        this.listener = listener;
        this.ctx = ctx;
        Log.i(TAG, "NearbyDsvManager");
        Nearby.getConnectionsClient(ctx)
                .startDiscovery(MainActivity.SERVICE_UUID,
                        endpointDiscoveryCB,
                        new DiscoveryOptions(Strategy.P2P_POINT_TO_POINT))
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "OnSuccess...");
                                listener.startDiscovering();
                            }
                        }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "OnFailure", e);
                        e.printStackTrace();
                    }
                });
    }



    private String currentEndpoint;

    private ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String s, ConnectionInfo connectionInfo) {
                    Log.i(TAG, "Connected to endpoint ["+s+"]");
                    NearbyDsvManager.this.currentEndpoint = s;
                    Nearby.getConnectionsClient(ctx).acceptConnection(s, payloadCallback);
                    listener.onConnected();
                }

                @Override
                public void onConnectionResult(String s, ConnectionResolution connectionResolution) {
                    switch (connectionResolution.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            //listener.onConnected();
                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            Log.i(TAG, "Connection rejected");
                            listener.onError();
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            Log.i(TAG, "Connection error");
                            listener.onError();
                            break;
                    }
                }

                @Override
                public void onDisconnected(String s) {
                    Log.i(TAG, "Connection disconnected");
                    listener.onDisconnected();
                }
            };

    public void endConnection()
    {
        Nearby.getConnectionsClient(ctx).stopAllEndpoints();//disconnectFromEndpoint(currentEndpoint);
    }

    private PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String s, Payload payload) {
            Log.i(TAG, "Payload received");
            byte[] b = payload.asBytes();
            String content = new String(b);
            Log.i(TAG, "Content ["+content+"]");
        }
        @Override
        public void onPayloadTransferUpdate(String s,
                                            PayloadTransferUpdate payloadTransferUpdate) {
            Log.d(TAG, "Payload Transfer update ["+s+"]");
        }
    };

    private void getConnection(String endpointId) {
        Nearby.getConnectionsClient(ctx)
                .requestConnection(endpointId, endpointId,connectionLifecycleCallback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Requesting connection..");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error requesting connection", e);
                    }
                });
    }

    private EndpointDiscoveryCallback endpointDiscoveryCB =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String s,
                                            DiscoveredEndpointInfo discoveredEndpointInfo) {
                    Log.i(TAG, "Endpoint found ["+s+"]. Connecting....");
                    listener.onDiscovered();
                    getConnection(s);
                }

                @Override
                public void onEndpointLost(String s) {
                    Log.e(TAG, "Endpoint lost ["+s+"]");
                }
            };

    public void sendData(String data) {
        Log.i(TAG, "Sending data ["+data+"]");
        Log.i(TAG, "Current endpoint ["+currentEndpoint+"]");
        if (currentEndpoint != null) {
            Log.d(TAG, "Sending data to ["+data+"]");
            Payload payload = Payload.fromBytes(data.getBytes());
            Nearby.getConnectionsClient(ctx).sendPayload(currentEndpoint, payload).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    listener.onPayloadSuccess();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.onPayloadFailure();
                }
            });

        }
    }

}

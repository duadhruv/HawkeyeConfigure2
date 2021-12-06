package com.example.hawkeyeconfigure;

public  interface EventListener {
        public void onDiscovered();
        public void onDisconnected();
        public void startDiscovering();
        public void onConnected();
        public void onError();
        void onPayloadSuccess();
        void onPayloadFailure();

}

package com.shliama.augmentedvideotutorial;

class NeedMoreFileException extends Exception {
    String payload = "";

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}

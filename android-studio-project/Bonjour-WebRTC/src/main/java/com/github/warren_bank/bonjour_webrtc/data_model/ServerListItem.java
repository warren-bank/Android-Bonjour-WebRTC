package com.github.warren_bank.bonjour_webrtc.data_model;

public final class ServerListItem {
    public String title;
    public String ip; // host:port

    public ServerListItem(String title, String ip) {
        this.title = title;
        this.ip    = ip;
    }

    @Override
    public String toString() {
        return title;
    }

    public boolean equals(ServerListItem that) {
        return (title.equals(that.title) && ip.equals(that.ip));
    }
}

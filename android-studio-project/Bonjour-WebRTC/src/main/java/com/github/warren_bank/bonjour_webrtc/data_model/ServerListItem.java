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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof ServerListItem))
            return false;

        ServerListItem that = (ServerListItem) o;

        return (
            ((title == null) || title.equals(that.title)) &&
            ip.equals(that.ip)
        );
    }
}

package org.openhab.binding.broadlink.internal.socket;

public interface NetworkTrafficObserver {
    public void onBytesSent(byte[] sentBytes);

    public void onBytesReceived(byte[] receivedBytes);
}

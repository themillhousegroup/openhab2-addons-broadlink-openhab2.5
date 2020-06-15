package org.openhab.binding.broadlink.internal.socket;

public interface NetworkTrafficObserver {

    public void onCommandSent(byte command);

    public void onBytesSent(byte[] sentBytes);

    public void onBytesReceived(byte[] receivedBytes);
}

package com.badu.common.utils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class AddressUtils {

    public static String extractClientAddress(SocketAddress remoteAddress) {
        String clientAddress;
        if (remoteAddress instanceof InetSocketAddress) {
            clientAddress = ((InetSocketAddress) remoteAddress).getHostName();
        } else {
            clientAddress = remoteAddress.toString();
        }
        return clientAddress;
    }

}

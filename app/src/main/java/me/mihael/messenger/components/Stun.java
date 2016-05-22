package me.mihael.messenger.components;

import android.util.Log;

import java.net.InetAddress;

import de.javawi.jstun.test.DiscoveryInfo;
import de.javawi.jstun.test.DiscoveryTest;

public class Stun {
    private static volatile Stun instance;
    private DiscoveryInfo di;
    private InetAddress publicIP;
    private int publicPort = -1;
    private int localPort = -1;
    private boolean discovered = false;

    public InetAddress getPublicIP() {
        return publicIP;
    }

    public boolean getDiscovered() {
        return this.discovered;
    }

    public int getPublicPort() {
        return publicPort;
    }

    public void setLocalPort(int port) {
        this.localPort = port;
    }

    public static Stun getInstance() {
        if (instance == null) {
            instance = new Stun();
        }
        return instance;
    }

    public boolean doDiscovery() {
        if(localPort != -1) {
            try {
                DiscoveryTest test = new DiscoveryTest(InetAddress.getByName(Utils.getIPAddress(true)), localPort, "mihael.me", 3478);
                di = test.test();
                if (!di.isError()) {
                    publicIP = di.getPublicIP();
                    publicPort = di.getPublicPort();
                    discovered = true;

                    Log.d("stun", publicIP.toString() + ":" + new Integer(publicPort).toString());
                    return true;
                }

            } catch (Exception e) {
                Log.d("stun", e.toString());
            }
        }

        return false;
    }
}

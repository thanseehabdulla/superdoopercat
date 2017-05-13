package com.boontaran.supercat;

/**
 * Created by arifbs on 12/10/15.
 *
 * interface to communicate between Game and main thread (platform)
 */
public interface Callback {
    void sendMessage(int message);
    void trackEvent(String label);
    void trackPage(String name);
}

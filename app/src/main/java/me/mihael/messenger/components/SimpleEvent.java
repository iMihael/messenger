package me.mihael.messenger.components;

import java.util.EventListener;

public interface SimpleEvent extends EventListener {
    void call(Object o);
}

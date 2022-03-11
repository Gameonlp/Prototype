package com.mygdx.game;

import java.util.LinkedList;
import java.util.List;

public class MessageQueue {
    public interface Observer {
        void handleMessage(Message message);
    }

    private static final MessageQueue instance = new MessageQueue();
    private List<Observer> observers;

    private MessageQueue(){
        observers = new LinkedList<>();
    }

    public static MessageQueue getInstance() {
        return instance;
    }

    public void postMessage(Message message){
        for (Observer observer : observers) {
            observer.handleMessage(message);
        }
    }

    public void subscribe(Observer observer){
        observers.add(observer);
    }

    public void unSubscribe(Observer observer){
        observers.remove(observer);
    }
}

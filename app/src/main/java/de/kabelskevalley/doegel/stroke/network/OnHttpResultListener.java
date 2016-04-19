package de.kabelskevalley.doegel.stroke.network;

public interface OnHttpResultListener<T> {

    void onResult(T object);

    void onError(Exception e);
}

package com.messengo.tablette.services;

public interface IUpdateService {
    public void addListener(IUpdateServiceListener listener); 
    public void removeListener(IUpdateServiceListener listener); 
}

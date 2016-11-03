package edu.asu.giles.service;


public interface IFileHandlerRegistry {

    public abstract IFileTypeHandler getHandler(String contentType);

}
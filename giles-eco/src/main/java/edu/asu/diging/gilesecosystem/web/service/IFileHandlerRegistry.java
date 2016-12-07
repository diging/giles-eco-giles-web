package edu.asu.diging.gilesecosystem.web.service;


public interface IFileHandlerRegistry {

    public abstract IFileTypeHandler getHandler(String contentType);

}
package edu.asu.diging.gilesecosystem.web.core.service;


public interface IFileHandlerRegistry {

    public abstract IFileTypeHandler getHandler(String contentType);

}
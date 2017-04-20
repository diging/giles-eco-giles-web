package edu.asu.diging.gilesecosystem.web.migrate.impl;

import java.time.ZonedDateTime;

public class MigrationResult {

    private int migratedDocuments;
    private int migratedUploads;
    private int migratedFiles;
    private int migratedProcessingRequests;
    private ZonedDateTime finished;
    
    public MigrationResult(int migratedDocuments, int migratedUploads, int migratedFiles,
            int migratedProcessingRequests, ZonedDateTime finished) {
        super();
        this.migratedDocuments = migratedDocuments;
        this.migratedUploads = migratedUploads;
        this.migratedFiles = migratedFiles;
        this.migratedProcessingRequests = migratedProcessingRequests;
        this.finished = finished;
    }
    
    public int getMigratedDocuments() {
        return migratedDocuments;
    }
    public void setMigratedDocuments(int migratedDocuments) {
        this.migratedDocuments = migratedDocuments;
    }
    public int getMigratedUploads() {
        return migratedUploads;
    }
    public void setMigratedUploads(int migratedUploads) {
        this.migratedUploads = migratedUploads;
    }
    public int getMigratedFiles() {
        return migratedFiles;
    }
    public void setMigratedFiles(int migratedFiles) {
        this.migratedFiles = migratedFiles;
    }
    public int getMigratedProcessingRequests() {
        return migratedProcessingRequests;
    }
    public void setMigratedProcessingRequests(int migratedProcessingRequests) {
        this.migratedProcessingRequests = migratedProcessingRequests;
    }

    public ZonedDateTime getFinished() {
        return finished;
    }

    public void setFinished(ZonedDateTime finshed) {
        this.finished = finshed;
    }
}

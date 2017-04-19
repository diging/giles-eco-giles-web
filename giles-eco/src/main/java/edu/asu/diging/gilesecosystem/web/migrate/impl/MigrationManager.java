package edu.asu.diging.gilesecosystem.web.migrate.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;

@Service
public class MigrationManager {

    @Autowired
    private MigrateToSql migrator;
    
    private Future<MigrationResult> result;
    
    public void runMigrations(String username) throws UnstorableObjectException {
        result = migrator.migrateUserData(username);
    }
    
    public MigrationResult checkResults() throws InterruptedException, ExecutionException {
        if (result == null) {
            return new MigrationResult(0, 0, 0, 0, null);
        }
        if (result.isDone()) {
            return result.get();
        }
        return null;
    }
}

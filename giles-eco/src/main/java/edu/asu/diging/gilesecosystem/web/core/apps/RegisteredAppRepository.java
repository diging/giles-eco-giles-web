package edu.asu.diging.gilesecosystem.web.core.apps;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.asu.diging.gilesecosystem.web.core.apps.impl.RegisteredApp;

@Repository
public interface RegisteredAppRepository extends JpaRepository<RegisteredApp, String> {

}

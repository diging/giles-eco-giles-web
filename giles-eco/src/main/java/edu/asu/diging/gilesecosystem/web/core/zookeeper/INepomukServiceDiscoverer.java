package edu.asu.diging.gilesecosystem.web.core.zookeeper;

import edu.asu.diging.gilesecosystem.web.core.exceptions.NoNepomukFoundException;

public interface INepomukServiceDiscoverer {

    String getRandomNepomukInstance() throws NoNepomukFoundException;

}
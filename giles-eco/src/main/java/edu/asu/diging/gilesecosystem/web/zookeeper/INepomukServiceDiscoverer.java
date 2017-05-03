package edu.asu.diging.gilesecosystem.web.zookeeper;

import edu.asu.diging.gilesecosystem.web.exceptions.NoNepomukFoundException;

public interface INepomukServiceDiscoverer {

    String getRandomNepomukInstance() throws NoNepomukFoundException;

}
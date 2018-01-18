# Giles Ecosystem

<a href='http://diging-dev.asu.edu/jenkins/job/GECO_test_giles_web_on_push'><img src='http://diging-dev.asu.edu/jenkins/buildStatus/icon?job=GECO_test_giles_web_on_push'></a>

The Giles Ecosystem is a distributed system to run OCR on images and extract images and texts from PDF files. This repository contains the user-facing component of this system called "Giles". The system requires the following software:

* Apache Tomcat 8
* Apache Kafka
* Apache Zookeeper
* MySQL (or PostgreSQL)
* Tesseract OCR (https://github.com/tesseract-ocr/)

The core components of the Giles Ecosystem are located in the following repositories:

* Giles: https://github.com/diging/giles-eco-giles-web (this repository)
* Nepomuk: https://github.com/diging/giles-eco-nepomuk (file storage)
* Cepheus: https://github.com/diging/giles-eco-cepheus (image extraction from PDF files)
* Andromemda: https://github.com/diging/giles-eco-andromeda (text extraction from PDF files)
* Cassiopeia: https://github.com/diging/giles-eco-cassiopeia (OCR using Tesseract)

The above applications have dependencies to libraries located in the following repositories:

* https://github.com/diging/giles-eco-requests
* https://github.com/diging/giles-eco-util
* https://github.com/diging/giles-eco-september-util

Additionally, Giles depends on:

* https://github.com/jdamerow/spring-social-github
* https://github.com/jdamerow/spring-social-mitreid-connect

There are some additional components of the Giles Ecosystem that can be added if required:

* September (monitoring app for the Giles Ecosystem): https://github.com/diging/giles-eco-september
* Freddie (Solr connector): https://github.com/diging/giles-eco-freddie

There is a Docker Compose file for testing and evaluation purposes that sets up the Giles Ecosystem in Docker. You can find that file here: https://github.com/diging/giles-eco-docker

You can detailed installation information and the documentation of Giles' API [here](https://diging.atlassian.net/wiki/display/GECO/Giles+Ecosystem+Home).

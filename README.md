# giles-eco-giles-web

<a href='http://diging-dev.asu.edu/jenkins/job/GECO_test_giles_web_on_push'><img src='http://diging-dev.asu.edu/jenkins/buildStatus/icon?job=GECO_test_giles_web_on_push'></a>

This repository contains the Giles web head of the Giles Ecosystem.

The Giles Ecosystem is a distributed system to run OCR on images and extract images and texts from PDF files. This repository contains the user-facing component of this system called "Giles". The system requires the following software:

* Apache Tomcat 8
* Apache Kafka
* Apache Zookeeper (required by Apache Kafka)
* Tesseract (https://github.com/tesseract-ocr/)

The components of the Giles Ecosystem are located in the following repositories:

* Giles: https://github.com/diging/giles-eco-giles-web (this repository)
* Nepomuk: https://github.com/diging/giles-eco-nepomuk (file storage)
* Cepheus: https://github.com/diging/giles-eco-cepheus (text and image extraction from PDF files)
* Cassiopeia: https://github.com/diging/giles-eco-cassiopeia (OCR using Tesseract)

The above applications have dependencies to libraries located in the following repositories:

* https://github.com/diging/giles-eco-requests
* https://github.com/diging/giles-eco-util

There is a docker compose file for testing and evaluation purposes that sets up the Giles Ecosystem in Docker. You can find that file here: https://github.com/diging/giles-eco-docker


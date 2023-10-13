FROM ubuntu:14.04

MAINTAINER Danylo Kovalchuk <dkovalchuk@axon.dev>

RUN sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 9DA31620334BD75D9DCB49F368818C72E52529D4
RUN echo "deb [ arch=amd64 ] http://repo.mongodb.org/apt/ubuntu trusty/mongodb-org/4.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.0.list

# Install MongoDB 4.0.3
RUN sudo apt-get update \
   && sudo apt-get install -y mongodb-org=4.0.3 mongodb-org-server=4.0.3 mongodb-org-shell=4.0.3 mongodb-org-mongos=4.0.3 mongodb-org-tools=4.0.3

# Add custom mongodb configuration
ADD ./etc/mongod.conf ./
RUN mv ./mongod.conf /etc/mongod.conf

# Add startup file
ADD ./startup.sh /opt/startup.sh
RUN chmod +x /opt/startup.sh
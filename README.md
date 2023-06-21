# SAGA-event-driven-microservices

To run this project first

Download Axon Server and extract.
https://developer.axoniq.io/download

Go to the directory. 
Run the jar with this command.

java -jar axonserver.jar

Or you can create axonserver via Docker.

docker run -d --name axonserver -p 8024:8024 -p 8124:8124 axoniq/axonserver

Then install libraries-dependencies.

mvn clean install


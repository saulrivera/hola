# Tracing System

This tracing system is build using Java Language and JVM technology. Java language v11 is used and uses the next
databases: Neo4J, Mongo and Redis.

## Create environment manually using Docker
In order to create Neo4J instance, use the following command:

`docker run -p7474:7474 -p7687:7687 -e NEO4J_AUTH=neo4j/s3cr3t -d --name neo4j neo4j`

It opens the ports `7474` for web client management, and `7687` for service connection. The credentials are _neo4j_ and 
password _s3cre3t_, but also can be changed in the code line above.

To create the Redis instance, use the following command:

`docker run -d -p 6379:6379 --name redis redis` 

It automatically opens the following ports: 32770, 32769, 32768. The port `32770` is used for GUI management. The port
`32769` is used by the tracking application to connect with the server.

In order to create mongodb instance, the next command line is helpful

`docker run -it -d --name mongodb -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=root --restart=always mongo`



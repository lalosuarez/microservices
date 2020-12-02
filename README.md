### Start kafka
https://kafka.apache.org/quickstart
1. cd /Users/esuarez/dev/kafka_2.11-2.1.0
2. bin/zookeeper-server-start.sh config/zookeeper.properties
3. bin/kafka-server-start.sh config/server.properties

#### Create topic
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic <topic-name>

#### Describe topic
bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic <topic-name>

#### Delete topic
bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic <topic-name>

##### Kafka logs
config/server.properties
    log.dirs=/kafka/kafka-logs-5d41b09deb9a

##### Zookeper data
conf/zoo.cfg
    dataDir=/opt/zookeeper-3.4.13/data


#### List topics
bin/kafka-topics.sh --list --zookeeper localhost:2181

##### Start a cluster with Docker
docker-compose up -d

1. docker exec -it kafka /bin/sh
2. cd /opt/kafka_2.12-2.5.0/
3. bin/kafka-topics.sh --list --zookeeper zookeeper:2181
4. bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic test

##### Add more brokers with Docker
docker-compose scale kafka=3

##### Destroy a cluster with Docker
docker-compose stop

### REST endpoints
1. http://localhost:8084/v1/orders
curl -v -X GET http://localhost:8084/v1/orders | json_pp
curl -v -d '{"customerId": 1}' -H "Content-Type: application/json" -X POST http://localhost:8084/v1/orders

2. http://localhost:8084/v1/orders/customers/1
3. http://localhost:8084/v1/orders/validations
4. http://localhost:8084/v1/orders/validations/status
5. http://localhost:8888/order-service/default (Config)
6. http://localhost:8761/ (Discovery)

### REST endpoints gateway
1. http://localhost:9999/api/order-service/v1/orders (Micro Proxy)
2. http://localhost:9999/api/v1/orders

### TODO:
1. Create topics manually for:
-orders-out
-orders-out-fallback
-orders-in
-orders-in-fallback
-orders-validation-out
-orders-validation-in

bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic orders-out
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic orders-in
kafka-topics --bootstrap-server localhost:9092 --create --topic reactive-topic

kafka-console-producer --bootstrap-server localhost:9092 --topic reactive-topic
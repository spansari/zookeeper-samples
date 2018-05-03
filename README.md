"# zookeeper-samples" 

Dependency:  Install zookeeper and start the on the default port:2181

Build the project: mvn clean install

Start multiple instances of Service-Provider:
  java -jar service-provider\target\service-provider.jar W1 8001
  
  java -jar service-provider\target\service-provider.jar W2 8002
  ...
  
Start Service-Consumer :
  java -jar service-consumer\target\service-consumer.jar 8000
  
  
  Open browser and point to :
  localhost:8000/api/delegate
  
  Open another tab and point to sam url: 
  localhost:8000/api/delegate
  
  

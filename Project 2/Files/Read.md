The program works by setting docker environment.

The docker command used for setting up and compiling the Java files using javac command is below:
command: docker build -t javaapptest .

After compiling the java files, we can create an instance of rover by passing the node number in the command using docker:
docker run -it -p 8080:8080 --cap-add=NET_ADMIN --net nodenet --ip 172.18.0.21 javaapptest 1

It will invoke the main() in Main file.
the main file creates a rover object using a thread

Similarly multiple rover instances can be run, by the above docker command:
docker run -it -p 8081:8080 --cap-add=NET_ADMIN --net nodenet --ip 172.18.0.22 javaapptest 2

Blocking and Unblocking can be done as follows:
Using the block=ip http query parameter.
curl "http://localhost:8080/?block=172.18.0.21&block=172.18.0.22"
Using the unblock=ip http query parameter.
curl "http://localhost:8080/?unblock=172.18.0.21"
FROM openjdk:17
COPY "./target/CRUD-futbol-apirest-1.jar" "app.jar"
EXPOSE 8117
ENTRYPOINT [ "java", "-jar", "app.jar" ]
FROM openjdk:17-oracle
VOLUME /tmp
COPY build/libs/product-information-management-0.0.1-SNAPSHOT.jar product-information-management.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "product-information-management.jar"]
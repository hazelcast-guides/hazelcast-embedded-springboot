FROM gcr.io/distroless/java17-debian11:nonroot
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]

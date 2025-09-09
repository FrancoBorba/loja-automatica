FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY  pom.xml .

# Roda as depêndencias antes para gerar um cache
RUN mvn dependency:go-offline 

# Copia tudo da nossa SRC para a do docker
COPY  src ./src/

# Executa o maven
RUN mvn clean install -DskipTests 

#  A Imagem Final de Execução ( Mais leve)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Pegamos o jar que foi gerado na builder para nossa imagem final
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
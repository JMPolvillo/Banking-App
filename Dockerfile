# Etapa 1: Construcción del proyecto
FROM maven:3.8.8-openjdk-21-slim AS build

# Crear y establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo de configuración de Maven y los archivos del proyecto
COPY pom.xml .
COPY src ./src

# Ejecutar la compilación del proyecto y generar el archivo .jar
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final para ejecutar la aplicación
FROM openjdk:21-jdk-slim

# Informar en qué puerto se expone el contenedor (solo informativo)
EXPOSE 3000

# Crear un directorio raíz en el contenedor
WORKDIR /root

# Copiar el .jar generado en la etapa de compilación
COPY --from=build /app/target/*.jar /src/app.jar

# Iniciar la aplicación cuando el contenedor se ejecute
ENTRYPOINT ["java", "-jar", "/src/app.jar"]

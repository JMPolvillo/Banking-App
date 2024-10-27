# Etapa 1: Compilación del proyecto
FROM maven:3.8.8-openjdk-21-slim AS build

# Crear y establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar los archivos del proyecto al contenedor
COPY . .

# Ejecutar la compilación del proyecto y generar el archivo .jar
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final para ejecutar la aplicación
FROM openjdk:21-jdk-slim

# Informar en qué puerto se expone el contenedor
EXPOSE 3000

# Crear un directorio raíz en el contenedor
WORKDIR /root

# Copiar el .jar generado en la etapa anterior al directorio actual
COPY --from=build /app/target/*.jar /src/app.jar

# Levantar la aplicación cuando el contenedor inicie
ENTRYPOINT ["java", "-jar", "/src/app.jar"]

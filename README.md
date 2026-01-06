# Event Hub

## Docker
**Alles im Root-Verzeichnis ausführen**

Jar-Datei erstellen
`mvn clean package`

Image erstellen
`docker build -t dockerusername/event-hub .`

Container erstellen und starten
`docker run -p 8080:8080 --env-file .env dockerusername/event-hub`

In Docker Hub pushen
`docker push dockerusername/event-hub:latest`
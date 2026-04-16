# Softwaretest

Dieses Projekt ist eine kleine Immobilienverwaltung mit Spring Boot im Backend und React/Vite im Frontend. Die Anwendung erfasst (Wohn-)Immobilien, zeigt Listen- und Detailansichten an und erlaubt das Anlegen, Bearbeiten und Löschen von Einträgen.

## Funktionalitäten

- CRUD für Immobilien über eine REST-API
- Listenansicht aller Immobilien
- Detailansicht einzelner Immobilien
- Formular für Neuanlage und Bearbeitung
- Client- und Server-Validierung für Formulareingaben
- Persistenz über eine lokale H2-Datenbank
- Absicherung der API per HTTP Basic Auth
- OpenAPI/Swagger-Dokumentation unter `/swagger-ui`

## Aufbau des Backends

Das Backend liegt unter `src/main/java/de/docestate/softwaretest` und ist als klassische Spring-Boot-Anwendung aufgebaut:

- `controller/`: REST-Endpunkte für Immobilien sowie SPA-Forwarding
- `utils/`: Geschäftslogik, Mapping und Fehlerklassen
- `repos/`: JPA-Repository für den Datenzugriff
- `domain/`: Entitäten und Enum für das Datenmodell
- `dto/`: Request- und Response-Objekte für die API
- `config/`: Security-Konfiguration
- `api/`: OpenAPI- und globale Exception-Behandlung

Zentrale Fachlogik:

- `PropertyController` stellt die Endpunkte unter `/api/properties` bereit.
- `PropertyService` kapselt CRUD-Logik und sortiert Listen nach ID.
- `ApiExceptionHandler` liefert strukturierte Fehlerantworten für Validierung und nicht gefundene Datensätze.
- Die Daten werden in einer H2-Datei unter `data/immobilien-db` gespeichert.

## Aufbau des Frontends

Das Frontend liegt im Ordner `frontend` und basiert auf React, TypeScript, Vite und Tailwind CSS.

- `src/pages/`: Seiten für Liste, Detail und Formular
- `src/components/`: wiederverwendbare UI-Bausteine wie Karten, Modal, Layout und Formular
- `src/api/`: Fetch-basierte Anbindung an das Backend
- `src/types/`: TypeScript-Typen für Immobilien und API-Fehler
- `src/utils/`: Formatierung und Bezeichnungen für Immobilientypen

Die Navigation erfolgt per `react-router-dom` mit diesen Hauptpfaden:

- `/` für die Übersicht
- `/neu` für das Anlegen
- `/immobilien/:id` für Details
- `/immobilien/:id/bearbeiten` für Änderungen

Im Entwicklungsmodus proxyt Vite Anfragen auf `/api` an das Spring-Backend auf Port `8080`. Ein Produktions-Build des Frontends wird nach `src/main/resources/static` geschrieben und anschließend direkt vom Backend ausgeliefert.

## Start des Projekts

Backend starten:

```bash
./mvnw spring-boot:run
```

Frontend für die Entwicklung starten:

```bash
cd frontend
npm install
npm run dev
```

Wichtige Standardwerte aus der Konfiguration:

- Backend: `http://localhost:8080`
- Frontend: `http://localhost:5173`
- Basic Auth Benutzer: `admin`
- Basic Auth Passwort: `admin123`

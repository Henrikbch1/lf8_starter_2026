# LF8-Starter: Projektverwaltung

Voraussetzungen: JDK 26, Docker mit Compose, IntelliJ IDEA (HTTP Client und Maven-Unterstützung). Dieses Gerüst enthält nur Hello und den EmployeeClient: Die Projekt-Domäne entwickelt ihr selbst.

## Starten

Im Projektverzeichnis zuerst `docker compose -p lf8starter pull`, danach `docker compose -p lf8starter up -d`. In IntelliJ `Lf8StarterApplication` mit JDK 26 starten oder im Terminal mit `./mvnw spring-boot:run`. Zum Stoppen der Container: `docker compose -p lf8starter down`. Nur wenn alle Daten verworfen werden sollen: `docker compose -p lf8starter down -v` (setzt beide Datenbanken zurück). Immer `-p lf8starter` verwenden, um andere Projekte nicht anzufassen.

`GetToken.http` im IntelliJ HTTP Client ausführen: ein client_credentials-Token wird automatisch als `{{token}}` für `SampleRequests.http` gespeichert. Für Swagger unter <http://localhost:8080/swagger> oben **Authorize** wählen und das access_token ohne „Bearer“ einfügen. Die OpenAPI-Beschreibung steht unter <http://localhost:8080/v3/api-docs>.

| Dienst | Host-Port | Zweck |
| --- | --- | --- |
| LF8-App | 8080 | REST und Swagger |
| projekt-db | 5433 | PostgreSQL (Container-Port 5432; Webshop kann parallel auf 5432 laufen) |
| auth | 9000 | Token und JWKS |
| employee | 8089 | Mitarbeiter und Qualifikationen |
| employee-db | keiner | Nur im Compose-Netz erreichbar |

Projekt-Datenbank: `jdbc:postgresql://localhost:5433/lf8_starter`, Benutzer `lf8_starter`, Passwort `geheim`. Die Employee-Datenbank hat intern `employee_db`, Benutzer `employee`, Passwort `secret`; kein Host-Port. Verbindungsparameter stehen in `compose.yml` und `src/main/resources/application.properties`.

## Gerüst und Tests

`hello/` zeigt Entity → Repository → Service → DTO/Mapper → Controller samt Validierung und Fehlerantworten in `common/`. `security/` schützt alle Routen außer `/welcome` und Swagger/OpenAPI. `employee/EmployeeClient` bietet genau **eine öffentliche Methode** `findById(long)`: sie sendet das JWT der eingehenden Anfrage als Bearer-Token an den Employee-Service. Eine fehlende ID wird `Optional.empty()`, ein nicht erreichbarer Dienst wird als 503 beantwortet. Im Controller zeigt `/hello/greeting/{employeeId}` die Übersetzung zu 404 bzw. einer Begrüßung. Bei Integrationstests `EmployeeClient` per `@MockitoBean` ersetzen; HTTP-Aufrufe mit `jwt()` aus security-test statt `@WithMockUser` authentifizieren.

Mit Docker (für Testcontainers) `./mvnw verify` ausführen: die Tests starten ihren eigenen PostgreSQL-Container per `@ServiceConnection`, **ohne** vorher `docker compose up` aufzurufen. `contextLoads` erfolgt durch die Integrationstests. Bei Portkonflikten 8080/8089/9000/5433 freie Ports wählen und die betreffenden URLs in Compose, Properties und HTTP-Dateien gemeinsam anpassen. Ist das Token abgelaufen (401), `GetToken.http` erneut ausführen. Liefert die Begrüßung 503, prüfen, ob `docker compose -p lf8starter up -d` läuft; Token- und Datenbankfehler getrennt prüfen.

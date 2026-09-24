# LF8-Starter: Projektverwaltung

Dieses Gerüst zeigt Hello durch alle Schichten und den Aufruf eines Employee-Service. Die Projektverwaltung mit ihren eigenen Fachregeln entwickelt ihr selbst.

## 1. Voraussetzungen

- JDK 26 und IntelliJ IDEA mit Maven-Unterstützung und HTTP Client
- Docker mit Docker Compose (Docker muss für die Tests laufen)

Alle Befehle in dieser Anleitung führt ihr im Verzeichnis dieses Starters aus.

## 2. Vorbereitung zu Hause

Die Container-Images sind groß. Ladet sie schon zu Hause herunter:

```bash
docker compose pull
```

## 3. Starten

1. Startet die lokalen Dienste mit `docker compose up -d`. Wartet, bis der Employee-Service bereit ist.
2. Startet `Lf8StarterApplication` in IntelliJ mit JDK 26 oder führt `./mvnw spring-boot:run` aus. Die App läuft auf Port 8080.
3. Führt `GetToken.http` im IntelliJ HTTP Client aus. Die Datei speichert das Zugriffstoken automatisch als `{{token}}`.
4. Führt die Beispiele aus `SampleRequests.http` aus: Hello anlegen, danach bei Bedarf die erhaltene ID für DELETE einsetzen; Begrüßung und direkte Employee-Aufrufe testen.

Zum Stoppen der Container: `docker compose down`. Die App stoppt ihr separat in IntelliJ oder im Terminal.

## 4. Swagger

Öffnet <http://localhost:8080/swagger>. Klickt auf **Authorize** und fügt das `access_token` aus der Antwort von `GetToken.http` ein, **ohne** das Wort „Bearer“. Die API-Beschreibung gibt es auch unter <http://localhost:8080/v3/api-docs>.

## 5. Dienste und Ports

| Dienst | Host-Port | Zweck |
| --- | --- | --- |
| LF8-App | 8080 | REST und Swagger |
| projekt-db | 5433 | Projekt-PostgreSQL; Webshop kann parallel auf 5432 laufen |
| auth | 9000 | Stellt JWTs und öffentliche Schlüssel bereit |
| employee | 8089 | Mitarbeiter und Qualifikationen |
| employee-db | keiner | Datenbank nur im Compose-Netz |

## 6. Datenbank in IntelliJ ansehen

1. Öffnet das Fenster **Database** und fügt eine neue Datenquelle **PostgreSQL** hinzu.
2. Tragt als URL `jdbc:postgresql://localhost:5433/lf8_starter` ein.
3. Benutzer: `lf8_starter`, Passwort: `geheim`. Testet die Verbindung; die Container müssen dafür laufen.

Die Werte stehen auch in `compose.yml` und `src/main/resources/application.properties`.

## 7. Aufbau des Codes

- `hello/`: Entity, Repository, Service, DTOs, Mapper und Controller – eine vollständige kleine REST-Kette. Die Suche läuft über `GET /hello?message=…`.
- `employee/`: `EmployeeClient.findById(long)` ruft den fremden Dienst auf und reicht das JWT der eingehenden Anfrage weiter. Ergänzt weitere Methoden nach diesem Muster: URL aufrufen, Token mitsenden und eine fehlende Antwort gezielt behandeln.
- `common/`: Fehlerantworten für Validierung, unbekannte IDs und nicht erreichbare Dienste.
- `security/`: JWT-Schutz; `/welcome` und Swagger/OpenAPI sind ohne Token erreichbar.
- `config/`: RestClient und OpenAPI-Konfiguration.

`GET /hello/greeting/{employeeId}` zeigt, wie aus einer fremden Antwort eine eigene Antwort wird: Mitarbeiter gefunden → Begrüßung; unbekannte ID → 404; Dienst nicht erreichbar → 503.

## 8. Tests

```bash
./mvnw verify
```

Docker muss laufen, **`docker compose up` ist für Tests nicht nötig**: Testcontainers startet eine eigene PostgreSQL mit `@ServiceConnection`. Die Hello-Tests erben `@MockitoBean EmployeeClient` aus `AbstractIntegrationTest` und nutzen `jwt()` für authentifizierte Anfragen statt `@WithMockUser`. `EmployeeClientTest` simuliert den fremden HTTP-Dienst ohne Compose.

## 9. Fehlerhilfe

| Symptom | Ursache | Lösung |
| --- | --- | --- |
| „port is already allocated“ | Port 8080, 8089, 9000 oder 5433 ist belegt | Freien Host-Port wählen; zugehörige URLs in `compose.yml`, `application.properties` und den HTTP-Dateien zusammen anpassen. |
| 401 bei geschützten Endpunkten | Token fehlt oder ist abgelaufen | `GetToken.http` erneut ausführen, dann den Request wiederholen. |
| 503 bei der Begrüßung | Employee-Service ist nicht erreichbar | `docker compose up -d` prüfen und auf den Start des Employee-Service warten. |
| Beispieldaten zurücksetzen | Alte Daten liegen in den Volumes | `docker compose down -v` löscht **beide** Datenbanken; anschließend `docker compose up -d`. |

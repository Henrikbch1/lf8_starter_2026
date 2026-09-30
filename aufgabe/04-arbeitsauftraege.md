# Arbeitsaufträge Projektverwaltung

Das Projekt dauert **20 Unterrichtsstunden**. Ihr arbeitet in Gruppen von zwei bis drei Personen
mit einem gemeinsamen Git-Repository und einem gemeinsamen Projekt auf
**sprint.heidelab.de**.

Am Ende steht eine Retrospektive. Wie ihr die Zeit bis dahin auf Planung und Umsetzung
verteilt, plant ihr selbst. Wie bewertet wird, steht in der [Bewertung](05-bewertung.md).

**Vorher zu Hause:** Die Docker-Images sind groß. Zieht sie **vor** der ersten Projektstunde,
nicht alle gleichzeitig im Schul-WLAN: Starter-Repository klonen
(<https://github.com/berndheidemann/lf8_starter_2026>), dann `docker compose pull`.

---

## Auftrag 1: Loslegen

1. Eine Person aus der Gruppe forkt das Starter-Repository <https://github.com/berndheidemann/lf8_starter_2026>. Die anderen werden als
   Collaborators eingeladen und klonen den Fork.
2. Jede und jeder startet die Umgebung und arbeitet die README bis zum Ende durch:
   `docker compose up -d`, Token holen mit `GetToken.http`, alle Requests aus
   `SampleRequests.http` ausprobieren, Swagger öffnen, alle Tests in IntelliJ laufen lassen
   (Rechtsklick auf `src/test/java` → *Run All Tests*).
3. Lest den Code des Hello-Beispiels und den `EmployeeClient`. Beantwortet in der Gruppe:
   Was passiert bei `GET /hello/greeting/1`, von der Anfrage bis zur Antwort? Woher kommt das
   Token, das der `EmployeeClient` mitschickt?
4. Legt auf sprint.heidelab.de ein Projekt für eure Gruppe an. Ladet alle aus der Gruppe und
   die Lehrkraft ein.

**Fertig, wenn** bei allen die Tests grün sind und `GET /hello/greeting/1` mit
„Hallo Max Mustermann“ antwortet.

---

## Auftrag 2: Sprint planen

Grundlage: [Ausgangssituation](01-ausgangssituation.md),
[Anforderungsdefinition mit Use-Case-Diagramm](02-anforderungsdefinition.md),
[Musterstory](03-musterstory.md).

1. **Stories schreiben.** Legt im Backlog für jede Muss-Anforderung eine User Story an, mit
   Akzeptanzkriterien nach dem Muster. Die Musterstory stammt aus einem anderen Service und
   zeigt nur die Form; alle Stories für die Projektverwaltung schreibt ihr selbst. Setzt
   bei den Muss-Stories den Kundennutzen auf *hoch* und ordnet sie nach oben. Die
   Kann-Anforderungen legt ihr als Stories mit Kundennutzen *mittel* darunter an.
2. **API entwerfen.** Bevor jemand programmiert, legt ihr in der README eures Forks eine Tabelle
   an: Methode, URI, Request-Body, Statuscodes im Erfolgs- und Fehlerfall, für jede
   Muss-Story. Zeigt sie der Lehrkraft, bevor ihr weitermacht.
3. **Schätzen.** Schätzt jede Muss-Story im Planning Poker auf sprint.heidelab.de. Liegen die
   Karten weit auseinander, erklären die höchste und die niedrigste Karte kurz ihre Gründe.
   Danach einigt ihr euch auf einen Wert und übernehmt ihn.
4. **Tasks schneiden.** Zerlegt jede Muss-Story in Tasks, die eine Person in höchstens einer
   Doppelstunde schafft.
5. **Definition of Done festlegen.** Sie gilt für eine **Story**. Mindestens: Der Endpunkt ist
   auf `main`, ein Integrationstest für den Erfolgsfall ist grün, der Endpunkt ist in Swagger
   dokumentiert und der Request steht in `SampleRequests.http`.
6. **Sprint starten.** Legt den Sprint an, zieht die Muss-Stories hinein und startet ihn.

**Fertig, wenn** alle Muss-Stories mit Akzeptanzkriterien, Schätzung und Tasks im laufenden
Sprint liegen und die Lehrkraft eure API-Tabelle gesehen hat.

---

## Auftrag 3: Umsetzen

Setzt euren Sprint um. Arbeitet arbeitsteilig:

- Wer einen Task anfängt, zieht ihn auf dem Board auf *in Arbeit*. Wer fertig ist, zieht ihn
  weiter. Das Board zeigt jederzeit den echten Stand.
- Jeder Task bekommt einen eigenen Branch. Er kommt auf `main`, wenn ein anderes
  Gruppenmitglied den Code angesehen hat und alle Tests grün sind. Ein Task muss dafür nicht
  die ganze Story fertig machen: Eine Entität ohne Endpunkt darf auf `main`.
- Wer den Code angesehen hat, schreibt einen Kommentar an den Task auf sprint.heidelab.de
  (z. B. „Review: ok“). Habt ihr im Pair gearbeitet, vermerkt ihr das ebenso am Task.
- Ist niemand aus der Gruppe da, der den Code ansehen kann, fragt ihr die Lehrkraft. Geht auch
  das nicht, darf der Task nach grünen Tests auf `main`; ihr vermerkt es am Task und holt das
  Review in der nächsten Stunde nach.
- Eine Story ist erst fertig, wenn sie die Definition of Done erfüllt. Dann zieht ihr sie auf
  dem Board auf *fertig*.
- Neue Aufrufe an den Employee-Service baut ihr als weitere Methode im `EmployeeClient`, nach
  dem Muster von `findById`. In den Tests mockt ihr den `EmployeeClient`, wie es die
  Hello-Tests zeigen.
- Bleibt ihr länger als 20 Minuten an derselben Stelle hängen: Fragt zuerst in der Gruppe, dann
  die Lehrkraft.

Wenn alle Muss-Stories fertig sind, macht ihr in dieser Reihenfolge weiter:

1. **Fachregeln mit Tests absichern.** Schreibt Integrationstests für die Fälle, in denen das
   Zuordnen scheitern oder gerade noch klappen muss: Zeiträume teilen sich genau einen Tag
   (abweisen), ein Zeitraum beginnt am Tag nach dem Ende des anderen (erlaubt), dieselbe Person
   ein zweites Mal, fehlende Qualifikation. Dazu: Nach dem Löschen eines Projekts ist die Person
   wieder frei. Und: Ist der Employee-Service nicht erreichbar, ändert sich in eurer Datenbank
   nichts.
2. **Kann-Stories.** Ergänzt ihre Akzeptanzkriterien und Tasks, dann zieht sie in den Sprint.
3. **Optional: Projekt abschließen.** Das tatsächliche Enddatum eines Projekts setzen. Schreibt
   vorher als Story fest, was gelten soll: Darf ein abgeschlossenes Projekt noch Mitarbeitende
   bekommen? Darf man es ein zweites Mal abschließen? Die Verfügbarkeitsprüfung bleibt bei den
   geplanten Zeiträumen.

**Fertig, wenn** alle Muss-Stories die Definition of Done erfüllen.

**Abgabe:** Link zum Git-Repository über diesen Auftrag.

---

## Auftrag 4: Zurückblicken

Führt am Ende des Projekts die Retrospektive auf sprint.heidelab.de durch. Leitfrage: Was nehmen
wir als Team ins nächste Projekt mit?

Danach füllt jede und jeder den Selbsteinschätzungsbogen aus der [Bewertung](05-bewertung.md)
aus. Das Fachgespräch am eigenen Repo folgt in einem eigenen Termin nach dem Projekt.

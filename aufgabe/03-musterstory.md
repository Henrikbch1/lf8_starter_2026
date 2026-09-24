# Musterstory: Projekt anlegen

So sieht eine fertige User Story aus. Alle weiteren schreibt ihr selbst, nach diesem Muster.

**Titel:** Projekt anlegen

**Story:** Als Projektleiterin der HiTec GmbH möchte ich ein neues Kundenprojekt anlegen, damit
es für alle Abteilungen an einer Stelle erfasst ist.

**Kundennutzen:** hoch (Muss)

**Akzeptanzkriterien**

1. Ein Projekt wird mit `POST /projects` angelegt. Der Body enthält die Pflichtangaben
   Bezeichnung, ID des verantwortlichen Mitarbeiters, Kunden-ID, Startdatum und geplantes
   Enddatum. Ansprechperson beim Kunden und Kommentar sind freiwillig: Fehlen sie, wird das
   Projekt trotzdem angelegt.
2. Im Erfolgsfall antwortet der Service mit **201 Created**. Der Body enthält das angelegte
   Projekt mit seiner neuen ID.
3. Fehlt eine Pflichtangabe → **400** mit einer Meldung, welches Feld nicht stimmt.
4. Liegt das geplante Enddatum vor dem Startdatum, wird das Projekt nicht angelegt. Welchen
   Fehlercode ihr dafür wählt, legt ihr nach der API-Richtlinie selbst fest.
5. Gibt es die ID des verantwortlichen Mitarbeiters im Employee-Service nicht → **404** mit einer
   Meldung, die die ID nennt.
6. Ist der Employee-Service nicht erreichbar → **503**. Das Projekt wird dann nicht angelegt.
7. Ohne gültiges Token → **401**.
8. Der Endpunkt ist in Swagger dokumentiert, mit allen genannten Statuscodes.
9. Ein Integrationstest deckt den Erfolgsfall ab.

**Tasks**

- DTOs für Anfrage und Antwort anlegen, mit Validierung
- Entität `Project` und Repository anlegen
- Service-Methode: verantwortlichen Mitarbeiter über den `EmployeeClient` prüfen, dann speichern
- Controller-Endpunkt `POST /projects` mit OpenAPI-Annotationen
- Integrationstest für den Erfolgsfall (EmployeeClient gemockt)
- Request in `SampleRequests.http` ergänzen

## Woran ihr eine gute Story erkennt

- Die Story sagt, **wer** etwas **wozu** will, nicht wie es technisch umgesetzt wird.
- Jedes Akzeptanzkriterium lässt sich mit Ja oder Nein prüfen. Statuscodes stehen drin, und zwar
  nach der API-Richtlinie in der [Anforderungsdefinition](02-anforderungsdefinition.md).
- Die Tasks sind so klein, dass eine Person sie in höchstens einer Doppelstunde schafft.

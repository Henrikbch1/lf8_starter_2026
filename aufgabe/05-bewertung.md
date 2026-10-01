# Bewertung Projektverwaltung

Die Note setzt sich aus drei Teilen zusammen:

| Teil | Anteil | Wer wird bewertet | Worauf es sich stützt |
|---|---|---|---|
| A: Projektmanagement | 30 % | eure Gruppe | Board auf sprint.heidelab.de, GitHub, README |
| B: Fachgespräch am eigenen Repo | 50 % | du allein | Gespräch am Code eures Forks |
| C: Reflexion | 20 % | du allein | dein Selbsteinschätzungsbogen |

Jede Zeile wird mit 0 bis 3 Punkten bewertet: voll erfüllt (3), eher erfüllt (2), eher nicht
erfüllt (1), nicht erfüllt (0). Umfang
zählt nicht: Kein Punkt hängt davon ab, wie viele Stories oder Endpunkte ihr gebaut habt.
Bewertet wird, wie gut die Arbeit ist und wie gut ihr sie begründet.

Jede Zeile ist nach dem Muster „Kompetenz **indem** Performanz“ gebaut: vorn, was ihr könnt,
hinten, woran man es sieht.

## Teil A: Projektmanagement (Gruppe)

Bewertet wird eure Gruppe am Ende des Projekts.

| Nr. | Wir haben … indem wir … | 3 = voll erfüllt, wenn … |
|---|---|---|
| A1 | die Anforderungen in Stories übersetzt, **indem wir** für jede Muss-Anforderung eine Story mit prüfbaren Akzeptanzkriterien geschrieben haben, auch für die Fehlerfälle mit Statuscode nach der API-Richtlinie. | Jede Muss-Story hat Kriterien für Erfolgs- und Fehlerfälle. Die Statuscodes passen zur Richtlinie. |
| A2 | die Schnittstelle vorab entworfen, **indem wir** die API-Tabelle in der README angelegt und gegen die API-Richtlinie geprüft haben, bevor programmiert wurde. | Die Tabelle ist vollständig, entstand vor dem ersten Endpunkt, folgt der API-Richtlinie und stimmt mit dem Code überein, oder Abweichungen sind nachgetragen. |
| A3 | den Aufwand gemeinsam eingeschätzt, **indem wir** jede Muss-Story im Planning Poker geschätzt, in Tasks von höchstens einer Doppelstunde zerlegt und eine Definition of Done festgelegt haben. | Schätzung, Tasks und DoD haben die Arbeit tatsächlich gesteuert: Weit auseinanderliegende Karten wurden besprochen, die Tasks ließen sich einzeln verteilen und abschließen, und die DoD enthält mindestens die Punkte aus Auftrag 2. |
| A4 | arbeitsteilig und nachvollziehbar gearbeitet, **indem wir** jeden Task in einem eigenen Branch umgesetzt haben und das Board den echten Stand zeigte. | Die Arbeit war so aufgeteilt, dass alle parallel arbeiten konnten, und Branches und Board zeigten jederzeit, wer woran arbeitet. |
| A5 | die Qualität gesichert, **indem** jeder Task vor dem Merge von einem anderen Gruppenmitglied angesehen wurde und nur Stories nach DoD auf *fertig* stehen. | Jeder Task wurde vor dem Merge von einem anderen Gruppenmitglied geprüft, belegt durch den Kommentar am Task auf sprint.heidelab.de, und keine Story steht ohne erfüllte DoD auf *fertig*. |
| A6 | aus dem Projekt gelernt, **indem wir** in der Retrospektive geprüft haben, wie gut unsere Werkzeuge (Git, Board, Poker) und unsere Dokumentation die Zusammenarbeit getragen haben, und daraus konkrete Maßnahmen fürs nächste Projekt festgehalten haben. | Die Maßnahmen sind konkret und umsetzbar, nicht „besser kommunizieren“. |

## Teil B: Fachgespräch am eigenen Repo (einzeln)

Das Gespräch findet in einem **eigenen Termin nach dem Projekt** statt und dauert etwa
**15 Minuten**. Bring deinen Laptop mit laufendem Fork mit. Grundlage ist der Stand von `main`
zur Abgabe.

**Du musst jede Zeile erklären können, die eure Gruppe geschrieben oder geändert hat**, nicht nur
deinen eigenen Code und den, den du reviewt hast. Die Lehrkraft wählt die Stellen frei. Den
mitgelieferten Starter-Code (z. B. `SecurityConfig`, `EmployeeClient`) musst du nur so weit
erklären, wie euer Code ihn benutzt. Stammt die Stelle aus einem Task, den du reviewt hast,
sagst du zusätzlich, worauf du beim Review geachtet hast. Am besten bereitest du dich vor, indem
ihr euch in der Gruppe gegenseitig euren Code erklärt.

| Nr. | Ich kann … indem ich … | 1 | 2 | 3 |
|---|---|---|---|---|
| B1 | zeigen, dass unsere Lösung funktioniert und für andere nutzbar ist, **indem ich** sie mit `SampleRequests.http` oder Swagger im Erfolgs- und Fehlerfall vorführe. | Der Erfolgsfall läuft. | Erfolgs- und Fehlerfall laufen, und ich erkläre, was die Antwort samt Fehlermeldung jemandem sagt, der die Schnittstelle nutzt (z. B. dem Team der späteren Weboberfläche). | Zusätzlich zeige ich einen Grenzfall und begründe, warum er so behandelt wird. |
| B2 | den Weg einer Anfrage durch unseren Code erklären, **indem ich** von Controller über Service bis Repository bzw. `EmployeeClient` zeige, was wo passiert. | Ich beschreibe den Weg richtig, ohne zu begründen. | Ich erkläre, warum die Logik im Service liegt und nicht im Controller. | Ich wäge eine Alternative ab und sage, warum wir sie nicht gewählt haben. |
| B3 | eine Regel oder Fehlerbehandlung umsetzen, **indem ich** eine Fachregel (Verfügbarkeit, Qualifikation, Mitarbeiterprüfung) **oder** eine Fehlerbehandlung (Pflichtfeld, unbekannte ID, Datum) im Code zeige und den Statuscode begründe. | Die Regel ist umgesetzt. | Ich begründe den Statuscode mit der API-Richtlinie. | Ich wäge ab (z. B. 409 gegen 422, oder was passiert, wenn der Employee-Service nicht antwortet). |
| B4 | beurteilen, wie unser Code abgesichert ist, **indem ich** einen Integrationstest aus unserem Repo erkläre. | Der Test existiert und ist grün. | Ich erkläre, was er prüft, und ob der `EmployeeClient` gemockt werden muss. | Ich nenne einen Fehler, den der Test fangen würde, und einen, den er nicht fängt. |
| B5 | über die Lösung hinausdenken, **indem ich** eine Änderung durchspiele, die die Lehrkraft im Gespräch vorgibt. | Ich weiß, wo die Änderung ansetzt. | Ich skizziere Endpunkt, Regel und Test. | Ich wäge zwei Umsetzungen ab und nenne Folgen für bestehende Regeln. |

Die Kann-Stories bringen keine Zusatzpunkte. Du kannst sie aber als Material für B1 bis B4
nutzen.

## Teil C: Reflexion (einzeln)

**Nach der Retrospektive** füllst du den Selbsteinschätzungsbogen unten aus. Die Stufe, die
du dir selbst gibst, geht **nicht** in die Note ein. Bewertet wird, wie gut du deine
Einschätzung begründest. Wer sich mit guten Belegen realistisch niedrig einstuft, bekommt
volle Punkte in C. Im Fachgespräch besprechen wir, wo deine Einschätzung und die der Lehrkraft
auseinanderliegen.

| Nr. | Ich kann meine Leistung einschätzen … indem ich … | 3 = voll erfüllt, wenn … |
|---|---|---|
| C1 | meine Einstufungen belege, **indem ich** zu jeder Zeile einen konkreten Nachweis angebe. | Es gibt überall einen passenden, auffindbaren Beleg. |
| C2 | Stärken und Entwicklungspunkte benenne, **indem ich** je mindestens einen konkret, bezogen auf eine Rasterzeile, beschreibe. | Beide sind konkret („B4: Ich teste nur den Erfolgsfall“), nicht allgemein („ich muss mehr üben“). |
| C3 | einen nächsten Schritt ableite, **indem ich** aus dem Entwicklungspunkt eine überprüfbare Maßnahme fürs nächste Projekt formuliere. | Man kann nachprüfen, ob die Maßnahme umgesetzt wurde. |

## Beitrag in der Gruppe

Teil A ist eine Gruppenbewertung. Wer deutlich zu wenig oder nichts zum Projekt beiträgt,
bekommt auf Teil A und Teil C nur die Hälfte oder keine Punkte. Das passiert nur nach einem
Hinweis während des Projekts und erst nach dem Fachgespräch, in dem du Gründe nennen kannst.
Maßgeblich sind Board, Reviews, Commits, die Beobachtung im Unterricht und das Gespräch, nie
die Commit-Zahl allein. Pair Programming und Reviews zählen als Beitrag, wenn sie als Kommentar
am Task auf sprint.heidelab.de stehen.

## Selbsteinschätzungsbogen

Kopiere den Bogen, fülle ihn nach der Retrospektive aus und gib ihn vor deinem Fachgespräch
ab. Trag je Zeile eine Stufe von 0 bis 3 **und einen Beleg** ein: Commit, Kommentar am Task,
Codezeile oder Task. Das geht auch für eine niedrige Stufe, etwa „Fehlerfall fehlt, siehe
Zeile 40 im Service“.

Teil A schätzt du für deine Gruppe ein, B1 bis B4 für dich selbst. B5 entfällt, weil die
Aufgabe erst im Gespräch kommt.

```text
Name:                         Gruppe:

Zeile | Stufe (0–3) | Beleg
------+-------------+--------------------------------------------
A1    |             |
A2    |             |
A3    |             |
A4    |             |
A5    |             |
A6    |             |
B1    |             |
B2    |             |
B3    |             |
B4    |             |

Stärke (mit Rasterzeile):
Entwicklungspunkt (mit Rasterzeile):
Nächster Schritt (überprüfbar, fürs nächste Projekt):
```

Nach dem Fachgespräch bekommst du das Raster zurück: deine Einschätzung neben der der
Lehrkraft, dazu je Teil eine Stärke und einen Entwicklungsimpuls.

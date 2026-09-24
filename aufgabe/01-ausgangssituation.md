# Ausgangssituation Projektmanagement-Service

Ihr seid Mitarbeitende der **HiTec GmbH**. Das mittelgroße IT-Systemhaus ist seit über 15 Jahren
IT-Dienstleister und nach ISO/IEC 27001 zertifiziert. Es gibt vier Geschäftsbereiche:

- **Entwicklung:** eigene Softwareprodukte
- **Consulting:** Beratung und Schulungen zu IT-, Kommunikations- und Sicherheitsthemen
- **IT-Systembereich:** von einzelnen Komponenten bis zu komplexen Netzwerken und Diensten
- **Support und Wartung:** Betreuung einfacher und vernetzter IT-Systeme

Jeder Bereich hat eine eigene Abteilung mit einer Abteilungs- bzw. Projektleitung. Die
Leitungen arbeiten eng zusammen.

## Ausgangslage

Für Backoffice-Aufgaben nutzt die HiTec GmbH bisher zwei Anwendungen, die historisch gewachsen
und voneinander unabhängig sind. Dazu gehören Termine, Kunden, Kundenprojekte,
Stundenerfassung, Urlaubsplanung, Raum- und Parkplatzbuchung. Manche Funktionen gibt es nur in
einer der beiden Anwendungen, andere in beiden. Projekte werden deshalb je nach Abteilung
doppelt angelegt und gepflegt.

Beide Systeme sollen durch **eine** Backoffice-Lösung ersetzt werden, die der ganze Betrieb
nutzt. Das spart Arbeitszeit und verhindert Fehler durch doppelt gepflegte Daten. Mehrere
Entwicklerteams bauen die neue Lösung schrittweise, neben ihren Kundenprojekten. Deshalb
entsteht sie aus kleinen, voneinander unabhängigen **Microservices**.

## Euer Auftrag

Ihr baut das Backend des Backoffice-Moduls **Projektverwaltung**. Über eine REST-Schnittstelle
soll eine (später entstehende) Weboberfläche Kundenprojekte anlegen, abrufen, ändern und
löschen und den Projekten Mitarbeitende zuordnen können. Die Projektdaten speichert euer
Service in einer eigenen Datenbank. Er antwortet im JSON-Format.

In der ersten Version dürfen alle angemeldeten Benutzer alle Endpunkte nutzen. Rollen (etwa:
nur die Projektleitung darf Projekte ändern) kommen erst in einer späteren Version.

## Die Nachbarn eures Service

- **Anmeldedienst.** Alle Services der HiTec GmbH nutzen einen zentralen Anmeldedienst
  (Single Sign-on). Wer sich dort anmeldet, bekommt ein **JWT** (JSON Web Token) und schickt
  es bei jeder Anfrage mit. Euer Service prüft das Token, eine eigene Anmeldung baut ihr nicht.
- **Employee-Service.** Die Stammdaten der Mitarbeitenden und ihre Qualifikationen verwaltet
  ein bestehender Microservice. Euer Service fragt ihn, ob es eine Mitarbeiter-ID gibt und
  welche Qualifikationen die Person hat. Auch der Employee-Service verlangt das JWT.

Beide Dienste laufen während der Entwicklung bei euch **lokal in Docker**. Das
Starter-Repository bringt sie mit.

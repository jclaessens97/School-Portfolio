# Reflectie document Brecht & Jeroen
## Oplevering 1: 30/09/2019
### pre
In het begin raakten we moeilijk van start omdat we spring helemaal niet zo goed kenden
en niet wisten waar te beginnen. Toen we een bepaalde scope hadden vast gesteld
(sensor simulation -> sensor service) ging het al beter. De code dat we voorlopig
al hebben geschreven ging redelijk vanzelf, buiten het versturen/ontvangen van een queue 
message zorgde voor wat vertraging.

Vragen:
- Hoevaak en wat precies loggen?
- Hoe precies exceptions afhandelen? Waarschijnlijk van lower level wrappen in eigen exception en dan
in higher level opvangen en afhandelen. In controller is het makkelijk, dan kan je iets returnen naar de frontend
maar wat als er een exception in simulator.runAsync of in de receive gebeurd zonder echte frontend interactie?

### post
Over het algemeen nog niet heel veel features, maar wel een heel goede code kwalitieit met nog 
enkele opmerkingen:

**sensorservice**

De JSON annotations horen eerder thuis op de DTO klasse ipv de model klasse. De annotations 
van hibernate zou je ook kunnen zeggen dat ze op een soort DAO klasse moet komen, maar dan
maak je eigelijk meerdere kopieën van eenzelfde klasse wat overbodige code duplicatie kan opleveren.

**Simulator**

De sensor message generation is goed, maar als je over een heel lange periode data zou willen
genereren, kan het nogal heavy worden om het in memory te laten staan. Een oplossing zou kunnen
zijn om de messages on the fly the genereren en versturen ipv ze te cachen in een list.

De comments zijn niet altijd even duidelijk. Bijvoorbeeld wat het verschil is tussen de 
sensorsimulation en de sensorsimulationservice. De comment boven de interfaces van "defines a contract"
is overbodig want een interface is altijd een contract naar de buitenwereld toe.

**Algemeen**

_hoevaak en wat precies loggen?:_ Bij elke belangrijke gebeurtenis. Niet overheen alle layers heen
want dan is het nut van loggen eigelijk weg omdat het veel te cluttered wordt. Maar wel best practice
is om te loggen wanneer er iets gebeurd dat essentieel is voor de werking. bv. commands uitvoeren,
bericht op de queue geplaatst, errors, ...

_exceptions_: Er is altijd wel een mogelijkheid om de exceptions naar de frontend te brengen.

In de nieuwe implementatie werken we met het command pattern. Het was voorlopig zo geïmplementeerd
met een CommandFactory en een switch case in de csv parser. Dit is een mogelijke oplossing, maar nadeel
is dat als je een command toevoegd dit op 2 plaatsen moet worden aangepast in de code. Er is een andere 
manier om dit met het Spring Framework te doen en en map van commands te autowiren en elke command 
een component te maken. Hierdoor moet er enkel nog maar 1 wijziging in de code worden gedaan, 
maar als je ooit van Spring Framework zou willen wegstappen heeft dit een grotere impact.

De taakverdeling zou nog iets beter kunnen. Bij de rideservice gaan we proberen te zorgen dat iedereen
50/50 eraan werkt.

## Oplevering 2: 20/10/2019
### pre
Deze sprint hebben we naar ons gevoel veel progress gemaakt. Zowel de simulator als de sensor service zijn voor 95% klaar.
Nog geen 100% omdat we tegen de finale oplevering (het examen) de logging en foutafhandeling nog eens deftig willen bekijken.
Alle echte features zouden wel al klaar zijn.
Voor de ride service zijn er twee grote delen waar nog aan gewerkt moet worden, de prijsafhandeling en het testen. 

De taakverdeling was ongeveer 50/50 voor de rideservice, 90% Jeroen bij de simulator en 80% Brecht bij de sensor service. 

We hebben vooral geprobeerd om onze code van de eerste keer al zo goed mogelijk te schrijven.
We verwachten dus niet dat er veel aan refactoring zal moeten worden gedaan. 
De communicatie en algemene samenwerking verloopt vlot en ook buiten de contacturen communiceren we naar elkaar waar we aan werken en wat we kunnen verbeteren aan elkaars code.

Vragen: 
- Wat is de betekenis van de boolean return value bij het locken/unlocken van vehicles?

### post
In de simulator kunnen we overwegen (indien we nog tijd hebben) om Lombok te gaan gebruiken.
Het commandpattern is goed, maar misschien nog een delegeren naar een sender/receiver ipv die logica
ook in de execute methode te zetten. Ook eens bekijken of we een template method pattern kunnen
gebruiken voor de delay ipv overal een super te moeten zetten. De simulator zou 2 soorten exceptions
moeten hebben: internal exceptions en exceptions wanneer de csv niet gelezen kan worden.
De Sender klasse hoort misschien net iets meer thuis in de controller package dan in het domain.
Soms zijn er te veel interfaces, wanneer we maar 1 implementation hebben, maar deze mogen we laten staan.

In de sensorservice hoort de receiver klasse meer thuis in de controller package.
We handlen hier nog niet genoeg exceptions af.

In de ride service kunnen we onnodige code weghalen (zoals bv bikelots) omdat code weghalen in productie niet 
simpel is. De openriderepository moet gerefactored worden naar een soort strategy pattern, en hoort misschien
ook niet helemaal thuis in de repository package. De methodes die annotated zijn met @cacheable nog onderbrengen
naar een eigen proxy-wrapper klasse, samen met de IOException.
Hidden features nog eens bekijken in de opdrachtbeschrijving (configuratie en logging)

### Oplevering 3: 04/11/2019
## pre
Deze sprint hebben we vooral gefocused om de feedback van oplevering 2 te verwerken en testen te schrijven. 
We hebben alles wat in de feedback van oplevering 2 ter sprake kwam doorgevoerd. Daarnaast hebben we ook verdere exceptionhandling en logging
geimplementeerd en de CRUD api voor bikes gedaan. 
Bij de testen hebben we bij pure CRUD methodes niet expliciet getest in de service layer, maar wel of ze een exception throwen wanneer we dit verwachten.

De taakverdeling was ongeveer 50/50 voor de rideservice, 90% Jeroen bij de simulator en 80% Brecht bij de sensor service. 

 
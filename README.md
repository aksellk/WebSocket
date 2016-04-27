# WebSocket
Laget av Aksel Langø Karlsen.

Dette prosjektet er en del av faget TDAT2004-A ved NTNU IIE våren 2016.

Prosjektet er testet ved å bruke operativsystemet Windows 7 og med nettleseren Google Chrome.

### Filer
* I mappen server ligger selve prosjektet med WebSocket tjeneren, javadoc, og kjørbar jar-fil
* I mappen client ligger WebSocket-klienten som er brukt for å teste serveren
* WebSocket-serveren kan også klones fra github om det er å foretrekke:
```sh
git clone https://github.com/aksellk/WebSocket.git
``` 
* WebSocket klienten kan også klones fra github om det er å foretrekke:
```sh
git clone https://github.com/aksellk/WebSocket_klient
```

### Kjøring av prosjektet
1. naviger til mappen server/WebSocket\NettverksprogrammeringProsjekt\dist
2. åpne kommandovindu i denne mappen
3. start tjener ved:  
```sh 
java -jar WebSocket.jar
``` 
4. naviger til mappen client/WebSocket_klient\websocket
5.  start klient ved:
```sh
nodemon --harmony server/server.js
``` 
6. åpne nettleser og skriv inn localhost:3000
7. avslutt klienten og tjeneren ved å bruke 
```
Ctrl + C
```

### javadoc
Dokumentasjonen av klassene og metodene i prosjektet finnes på javadocformat:
* Naviger til mappen WebSocket\NettverksprogrammeringProsjekt\dist
* Åpne filen index.html

### Oppbygging
Denne java-applikasjonen er en WebSocket-tjener som håndterer kommunikasjon med WebSocket-klienter ved å bruke WebSocket-protokollen som er spesifisert i (Fette & Melnikov 2011). Tjeneren støtter kommunikasjon med flere klienter samtidig ved å bruke en tråd for hver klient. For kommunikasjon med klienter gjennomføres alltid først en åpnings-handshake der det utveklses nøkler. Deretter overføres informasjon ved å bruke WebSocket-protokollen. 

Når en tråd får en melding fra en klient sendes den ut til alle andre aktive tråder som sender videre til klienten de håndterer.

Når det gjelder overføring av informasjon ved WebSocket-protokollen er det tatt hensyn for tolking av FIN-flag, opcodene TEXT_FRAME, CONTINUATION_FRAME, CLOSE og PONG, samt lengder av to forskjellige størrelser: hvis nyttelasten er 125 byte eller mindre og hvis nyttelasten er mellom 126 byte og 2^16 byte. I tillegg til å overføre vanlige text-frames når meldinger overføres sendes det ut melding til klienten når tjeneren tar initiativ til å avslutte forbindelsen.

Prosjektet er delt opp i tre pakker der klassene hører logisk sammen:
* thread: Her ligger ansvaret for kjøring av tjeneren ved hjelp av tråder og selve overføringen av informasjon fra og til klient.
* handshake: Her ligger ansvaret for å utføre handshake.
* message: Her ligger ansvaret for å tolke WebSocket meldinger fra klient og å lage WebSocket-meldinger som skal sendes til klient.

I tillegg er det laget en egen pakke for test-klasser

##### Pakkene:
###### thread
Denne pakken inneholder klassene Main og ServerThread. Main klassen inneholder main()-metoden som starter applikasjonen. I Main-klassen opprettes tråder for hver klient, ved å opprette ServerThread-objekter som er subklasser til Thread. I Main-klassen ligger også logikken for synkronisering av tråder når en melding skal sendes ut til alle trådene. I tillegg er det her logikk for å fjerne en tråd når en klient kobler seg fra.

I ServerThread-klassen er run()-metoden implementert. Her gjøres kall på metodene som kommuniserer med klienten. Først sørges det for å gjennomføre "handshake", deretter kommuniseres det med WebSocket-protokollen og til slutt lukkes IO-strømmer.

ServerThread inneholder også metodene som sørger for å sende meldinger til klienten over WebSocket-protokollen.
###### handshake
Denne pakken inneholder tre klasser: HSHandler, HSMessage og Encoder. Her ligger logikken som styrer opprettelse av forbindelse mellom klient og tjener (handshake). Hovedklassen som styrer dette er HSHandler som styrerer hele overføringen i metoden handle(). I handle() gjøres først kall på findKey()-metoden i klassen HSMessage. findKey()-metoden bruker en instans av BufferedReader til å gå gjennom HTTP-GET-forespørselen fra klienten. Metoden prøver å finne headerlinjen "Sec-WebSocket-Key" som inneholder WebSocket-nøkkelen fra klienten. Hvis denne headerlinjen finnes hentes nøkkelen ut og returneres.

Når nøkkelen fra klienten er funnet gjøres det kall på createKey()-metoden i Encoder-klassen. createKey() konkatenerer nøkkelen med GUID-en som spesifisert i (Fette & Melnikov 2011) kapittel 1.3. Deretter enkodes nøkkelen med base64 og hasher med SHA-1 hashing-algoritmen. Den enkodede nøkkelen returneres, og blir en del av response meldingen fra tjeneren. Denne responsen har HTTP-status 101 som betyr at det skal byttes protokoll. Headerlinjene "upgrade: " og "connection: " spesifiserer at det skal byttes til WebSocket-protokollen. Responsen inneholder også den enkodede nøkkelen i headerlinjen "Sec-WebSocket-Accept: ". Etter at handshaken er gjennomført kan partene begynne å kommunisere over WebSocket-protokollen.

###### message
Denne pakken inneholder klassene Handler, MessageHandler og Message som har ansvaret for å tolke kommunikasjon som kommer fra klienten og produsere svar som skal sendes tilbake over WebSocket-protokollen, men som nevnt er selve ansvaret med å sende til klient overlatt til klasse ServerThread. 

Klassen Handler er hovedklassen, som inneholder metoden handle() som går i løkke og lytter på meldinger fra klienten. Metoden sørger også for å sende meldinger til en instans av Main som skal sende videre til alle klientene, når det kommer en melding. Metoden sørger også for å si fra til Main()-instansen at en klient er koblet fra når en close-frame er mottat slik at tråden som håndterer kommunikasjon med denne klienten fjernes. Ansvaret med å tolke selve meldingene fra klient og å produsere responser er overlatt til klassen MessageHandler.

I klassen MessageHandler blir metoden decodeMessage() kalt når det har kommet en ny melding fra klienten. decodeMessage() sørger for å finne FIN-flagget og opcoden til meldingen fra klienten i henhold til hvordan WebSocket-protokollen er bygd opp, spesifisert i (Fette & Melnikov 2011) kapittel 5.2. Når opkoden og FIN-flagget er funnet delegeres ansvaret videre til metoden handleMessage() for å avgjøre hva som skal gjøres med denne meldingen.

Metoden handleMessage() inneholder en switch-case som bestemmer hva som skal gjøres med meldingen fra klienten basert på opcoden som ble funnet. Hvis opcoden "TEXT_FRAME" ble mottat produseres en melding som skal sendes tilbake til klient med opcoden "TEXT_FRAME". Hvis opcoden "CONTINUATION_FRAME" ble mottat produseres en melding som skal sendes tilbake til klienten med opcoden "CONTINUATION FRAME". Hvis opcoden "CLOSE" ble mottatt skal forbindelsene til denne klienten lukkes. Hvis opcoden "PONG" ble mottat gjøres ingenting (bortsett fra utskrift til console). 

handleMessage() gjør kall på metoden decodeTextFrame() som tolker resten av meldingen fra klienten (etter første byte) Her unmaskes nyttelasten, ved å bruke nyttelasten på posisjon i XOR-et med masking-key-oktetten i posisjon i modulus 4 i henhold til (Fette & Melnikov 2011 kap. 5.3), og returneres. I tillegg er det håndtert for ulik lengde på den motatte nyttelasten. Hvis nyttelastelengden i byte 2 er 125 eller mindre er nyttelastelengden verdien i denne byten. Hvis nyttelastelengden i byte 2 er 126 er de to neste bytene lengden av nyttelasten representert ved en 16-bit unsigned int (Fette & Melnikov 2011 kap. 5.2). Videre kommuniserer handleMessage() med et objekt av klassen Message() for å produsere responser.

I klassen Message er det metoder for å produsere byte[] som inneholder respons-meldinger til klienten. 

createMessage() lager en respons når enten en TEXT-FRAME-opcode og en CONTINUATION-FRAME-opcode er mottat. Om det er kontinuerlig melding eller en enkel tekst-melding avgjøres av en boolean "cont" i parameteret. En int med navn "FIN" i parameteret brukes til å avgjøre om det skal sendes melding met FIN-flagget satt i første bit. Det er også håndtert for ulik lengde. Hvis lengden er 125 eller mindre settes byte 2 til å være nyttelastens lengde. Hvis lengden er større eller lik 126 og mindre eller lik 2^16 er byte 2 satt til 129 og de to påfølgende bytene lengden på 16-bits unsigned integer format. 

Metodene createCloseMessage() og createPing() lager meldinger for ping og lukking av forbindelse, med opcodene satt for de respektive operasjonene og lengde 0.

###### Test Packages
Det er laget tester for to metoder. I klassen Encoder testes createKey()-metoden som enkoder nøkkelen fra klienten. Det testes på at nøkkelen er korrekt enkodet med en dummy-enkodet nøkkel. I Main-klassen testes metoden removeThread som sørger for å fjerne en tråd fra listen med aktive tråder etter at en klient har tatt initiativ for nedkobling, lukker IO-forbindelsene til denne tråden, og legger den i søppel-lista. Her testes det på fjerning fra aktiv liste og å legge tråden i søppellisten.

##### Referanser
Fette, I. & Melnikov A. (2011) The WebSocket Protocol. https://tools.ietf.org/html/rfc6455

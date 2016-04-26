# WebSocket
Denne java-applikasjonen er en WebSocket-tjener som håndterer kommunikasjon med WebSocket-klienter ved å bruke WebSocket-protokollen som er spesifisert i .

Prosjektet er delt opp i tre pakker der klassene hører logisk sammen:
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

Metoden handleMessage() inneholder en switch-case som bestemmer hva som skal gjøres med meldingen fra klienten basert på opcoden som ble funnet. Hvis opcoden "TEXT_FRAME" ble mottat produseres en melding som skal sendes tilbake til klient med FIN-flagget satt og med opcoden "TEXT_FRAME". Hvis opcoden "CONTINUATION_FRAME" ble mottat produseres en melding som skal sendes tilbake til klienten med flagget FIN ikke satt og med opcoden "CONTINUATION FRAME"
##### Referanser
Fette, I. & Melnikov A. (2011) The WebSocket Protocol. https://tools.ietf.org/html/rfc6455

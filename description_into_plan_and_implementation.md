Ohjelman pitää heti käynnistyessään ja vanhan pelitilanteen latauksen jälkeen tarkistaa:
1) onko jompikumpi pelaaja seuraavaksi siirtämässä L pelinappulaansa.
 Jos on: 2) tarkistaa sama asia kuin ennen
jokaisen pelaajan L pelinappulan siirtoa, trakistetaan seuraava:

3) Eli onko pelaajalla oikeutta siirtää L pelinappulaansa millekään L pelinappulalle
lailliseen positioon? Jos on, niin sillloin peli voi jatkua tämän
pelaajan toimesta uuteen L pelinappula position jne.
4) Muuten ilmoitetaan kyseisen pelaajan hävinneen sekä passivoidaan muut pelipainonapit,
paitsi ei: "Uusi peli..." painonappia. Tämä tilanne jatkuu niin kauan kuin kunnes
uusi peli on aloitettu.

Alla lisää siitä kuinka L pelinappulan vapaiden peliruudut löydetään tai todetaan onko peli hävitty
ja loppu, joka tehdään seuraavan logiikan mukaan:

1) Etsi 3 vapaata peliruutua, jotka:
a) joiden rivi koordinaatit ovat samat, mutta joiden kolumni arvot ovat yhdellä numerolla
peräkkäin. Esim kun pelitaulukko alkaa rivit 0 luvusta ja kolumnit 0 luvusta alkaen, niin
silloin toisen rivin eli rivi=1 ja kolumnien arvot kolumni=1, kolumni=2 ja kolumni=3.
Toinen vaihtoehto on kuten yllä, mutta nyt etsitäänkin samanlaisia, mutta kolumnien
suhteen, esim: toisen kolumni eli kolumni=1 ja rivien arvot rivi=1, rivi=2 ja rivi=3.

Tulos a) Jos tällaista ei ole vapaana peliruutuina, on pelaaja hävinnyt. Jos löytyy, niin
jatketaan seuraavalla säännollä eli b-sääntö:

b) Testataan löytyykö yllä olevien esimerkkien mukaisesti vapaata peliruutua jostakin seuraavista
positioista: esim 1 eli riviesimerkkin mukaan kun kolumni=1,rivi=1, niin löytyykö sen vierestä
vapaata peliruutua positiosta: kolumni=1,rivi=0 tai positiosta: kolumni=1,rivi=2, jos löytyy,
niin peli jatkuu normaalisti.
c) Sitten haetaan kuten yllä, mutta testataan löytyykö yllä olevien esimerkkien mukaisesti
vapaata peliruutua jostakin seuraavista positioista: esim 1 eli riviesimerkkin mukaan kun
kolumni=3,rivi=1, niin löytyykö sen vierestä vapaata peliruutua positiosta:
kolumni=3,rivi=0 tai positiosta: kolumni=3,rivi=2, jos löytyy, niin peli jatkuu normaalisti.
d) Jos vielä yhtään L kirjasimen muotoista ja oikea lukumäärä vapaita peliruutuja, ei ole
löytynyt, jatketaan hakuja yllä olveien sääntöjen pohjalta, kunnes kaikki vapaat ruudut on
käyty läpi. Jos ei edelleenkään löydy, pelaaja on hävinyt pelin.
package com.metait.javafxlgame;

import java.util.HashMap;
import java.util.Map;

public class LanguageSupport {
    private static final Map<String, Map<String, String>> translations = new HashMap<>();

    static {
        // --- FINNISH (fi) ---
        Map<String, String> fi = new HashMap<>();
        fi.put("valikko", "Valikko");
        fi.put("keskeneraiset", "Valitse toista keskeneräistä peliä");
        fi.put("muokkaaNimia", "Muokkaa pelaajien nimiä");
        fi.put("paattyneet", "Päättyneet pelit");
        fi.put("lopeta", "Lopeta peli");
        fi.put("tietoa", "Tietoa pelistä");
        fi.put("valitseKieli", "Valitse kieli");
        fi.put("apua", "Apua");
        fi.put("apuaDialogTitle", "Apua L-peliin");
        fi.put("tietoaDialogTitle", "Tietoa L-pelistä");
        fi.put("tietoaText", "L-Peli (L Game)\nVersio 1.0\nKeksijä: Edward de Bono (1967)");
        fi.put("pelaajienNimet", "Pelaajien nimet");
        fi.put("pelaaja1", "Pelaaja 1");
        fi.put("pelaaja2", "Pelaaja 2");
        fi.put("tallenna", "Tallenna");
        fi.put("peruuta", "Peruuta");
        fi.put("punainen", "Punainen");
        fi.put("sininen", "Sininen");
        fi.put("voittaja", "PELI PÄÄTTYI! Voittaja: ");
        fi.put("asetaL", "Aseta L-nappula (R=Pyöritä, F=Peilaa, klikkaa + V=Vahvista)");
        fi.put("siirraKolikko", "Siirrä kolikkoa (klikkaa kolikkoa + tyhjää ruutua tai S=Ohita)");
        fi.put("vahvista", "Vahvista (V)");
        fi.put("ohita", "Ohita (S)");
        fi.put("pyorita", "Pyöritä (R)");
        fi.put("peilaa", "Peilaa (F)");
        fi.put("uusiPeliBtn", "Uusi peli (N)");
        fi.put("uusiPeliPromptTitle", "Uusi peli");
        fi.put("uusiPeliPromptText", "Aloitetaanko uusi L peli?");
        fi.put("kylla", "Kyllä");
        fi.put("ei", "Ei");
        fi.put("lataaPeli", "Lataa peli");
        fi.put("listaKeskeneraiset", "Valitse toinen keskeneräinen peli");
        fi.put("listaPaattyneet", "Lista päättyneistä peleistä");
        fi.put("kieliDialogTitle", "Valitse kieli");
        fi.put("kieliDialogHeader", "Aseta pelin kieli");
        fi.put("esikatselu", "Pelilaudan tilanne:");
        fi.put("noMovesLost", "Ei siirtoja. Olet hävinnyt pelin!");
        fi.put("errOutOfBounds", "Siirto on virheellinen: nappulan on oltava kokonaan laudan sisällä.");
        fi.put("errNotMoved", "Siirto on virheellinen: nappulan on siirryttävä uuteen paikkaan (vähintään yksi uusi ruutu).");
        fi.put("errOverlapOpponent", "Siirto on virheellinen: nappula ei voi mennä vastustajan nappulan päälle.");
        fi.put("errOverlapCoin", "Siirto on virheellinen: nappula ei voi mennä kolikoiden päälle.");
        translations.put("fi", fi);

        // --- ENGLISH (en) ---
        Map<String, String> en = new HashMap<>();
        en.put("valikko", "Menu");
        en.put("keskeneraiset", "Select another unfinished game");
        en.put("muokkaaNimia", "Edit player names");
        en.put("paattyneet", "Finished games");
        en.put("lopeta", "Quit game");
        en.put("tietoa", "About game");
        en.put("valitseKieli", "Choose language");
        en.put("apua", "Help");
        en.put("apuaDialogTitle", "L Game Help");
        en.put("tietoaDialogTitle", "About L Game");
        en.put("tietoaText", "L Game\nVersion 1.0\nInventor: Edward de Bono (1967)");
        en.put("pelaajienNimet", "Player Names");
        en.put("pelaaja1", "Player 1");
        en.put("pelaaja2", "Player 2");
        en.put("tallenna", "Save");
        en.put("peruuta", "Cancel");
        en.put("punainen", "Red");
        en.put("sininen", "Blue");
        en.put("voittaja", "GAME OVER! Winner: ");
        en.put("asetaL", "Place L-piece (R=Rotate, F=Flip, click + V=Confirm)");
        en.put("siirraKolikko", "Move coin (click coin + empty cell or S=Skip)");
        en.put("vahvista", "Confirm (V)");
        en.put("ohita", "Skip (S)");
        en.put("pyorita", "Rotate (R)");
        en.put("peilaa", "Flip (F)");
        en.put("uusiPeliBtn", "New game (N)");
        en.put("uusiPeliPromptTitle", "New Game");
        en.put("uusiPeliPromptText", "Start a new L game?");
        en.put("kylla", "Yes");
        en.put("ei", "No");
        en.put("lataaPeli", "Load Game");
        en.put("listaKeskeneraiset", "Select another unfinished game");
        en.put("listaPaattyneet", "List of finished games");
        en.put("kieliDialogTitle", "Choose Language");
        en.put("kieliDialogHeader", "Set game language");
        en.put("esikatselu", "Board state:");
        en.put("noMovesLost", "No moves. You have lost the game!");
        en.put("errOutOfBounds", "Invalid move: the piece must be entirely inside the board.");
        en.put("errNotMoved", "Invalid move: the piece must move to a new position (at least one new cell).");
        en.put("errOverlapOpponent", "Invalid move: the piece cannot overlap the opponent's piece.");
        en.put("errOverlapCoin", "Invalid move: the piece cannot overlap coins.");
        translations.put("en", en);

        // --- SWEDISH (sv) ---
        Map<String, String> sv = new HashMap<>();
        sv.put("valikko", "Meny");
        sv.put("keskeneraiset", "Välj annat oavslutat spel");
        sv.put("muokkaaNimia", "Redigera spelarnamn");
        sv.put("paattyneet", "Avslutade spel");
        sv.put("lopeta", "Avsluta spel");
        sv.put("tietoa", "Om spelet");
        sv.put("valitseKieli", "Välj språk");
        sv.put("apua", "Hjälp");
        sv.put("apuaDialogTitle", "Hjälp till L-spel");
        sv.put("tietoaDialogTitle", "Om L-spelet");
        sv.put("tietoaText", "L-spel\nVersion 1.0\nUppfinnare: Edward de Bono (1967)");
        sv.put("pelaajienNimet", "Spelarnamn");
        sv.put("pelaaja1", "Spelare 1");
        sv.put("pelaaja2", "Spelare 2");
        sv.put("tallenna", "Spara");
        sv.put("peruuta", "Avbryt");
        sv.put("punainen", "Röd");
        sv.put("sininen", "Blå");
        sv.put("voittaja", "SPELET SLUT! Vinnare: ");
        sv.put("asetaL", "Placera L-pjäs (R=Rotera, F=Vänd, klicka + V=Bekräfta)");
        sv.put("siirraKolikko", "Flytta mynt (klicka mynt + tom ruta eller S=Hoppa över)");
        sv.put("vahvista", "Bekräfta (V)");
        sv.put("ohita", "Hoppa över (S)");
        sv.put("pyorita", "Rotera (R)");
        sv.put("peilaa", "Vänd (F)");
        sv.put("uusiPeliBtn", "Nytt spel (N)");
        sv.put("uusiPeliPromptTitle", "Nytt spel");
        sv.put("uusiPeliPromptText", "Starta ett nytt L-spel?");
        sv.put("kylla", "Ja");
        sv.put("ei", "Nej");
        sv.put("lataaPeli", "Ladda spel");
        sv.put("listaKeskeneraiset", "Välj ett annat oavslutat spel");
        sv.put("listaPaattyneet", "Lista över avslutade spel");
        sv.put("kieliDialogTitle", "Välj språk");
        sv.put("kieliDialogHeader", "Välj språk för spelet");
        sv.put("esikatselu", "Brädets tillstånd:");
        sv.put("noMovesLost", "Inga drag kvar. Du har förlorat spelet!");
        sv.put("errOutOfBounds", "Ogiltigt drag: pjäsen måste vara helt inom brädet.");
        sv.put("errNotMoved", "Ogiltigt drag: pjäsen måste flyttas till en ny position (minst en ny ruta).");
        sv.put("errOverlapOpponent", "Ogiltigt drag: pjäsen får inte överlappa motståndarens pjäs.");
        sv.put("errOverlapCoin", "Ogiltigt drag: pjäsen får inte överlappa mynt.");
        translations.put("sv", sv);

        // --- GERMAN (de) ---
        Map<String, String> de = new HashMap<>();
        de.put("valikko", "Menü");
        de.put("keskeneraiset", "Anderes unvollendetes Spiel auswählen");
        de.put("muokkaaNimia", "Spielernamen bearbeiten");
        de.put("paattyneet", "Beendete Spiele");
        de.put("lopeta", "Spiel beenden");
        de.put("tietoa", "Über das Spiel");
        de.put("valitseKieli", "Sprache auswählen");
        de.put("apua", "Hilfe");
        de.put("apuaDialogTitle", "Hilfe zum L-Spiel");
        de.put("tietoaDialogTitle", "Über das L-Spiel");
        de.put("tietoaText", "L-Spiel\nVersion 1.0\nErfinder: Edward de Bono (1967)");
        de.put("pelaajienNimet", "Spielernamen");
        de.put("pelaaja1", "Spieler 1");
        de.put("pelaaja2", "Spieler 2");
        de.put("tallenna", "Speichern");
        de.put("peruuta", "Abbrechen");
        de.put("punainen", "Rot");
        de.put("sininen", "Blau");
        de.put("voittaja", "SPIEL BEENDET! Gewinner: ");
        de.put("asetaL", "L-Teil platzieren (R=Drehen, F=Spiegeln, Klick + V=Bestätigen)");
        de.put("siirraKolikko", "Münze bewegen (Klick Münze + freies Feld oder S=Überspringen)");
        de.put("vahvista", "Bestätigen (V)");
        de.put("ohita", "Überspringen (S)");
        de.put("pyorita", "Drehen (R)");
        de.put("peilaa", "Spiegeln (F)");
        de.put("uusiPeliBtn", "Neues Spiel (N)");
        de.put("uusiPeliPromptTitle", "Neues Spiel");
        de.put("uusiPeliPromptText", "Neues L-Spiel starten?");
        de.put("kylla", "Ja");
        de.put("ei", "Nein");
        de.put("lataaPeli", "Spiel laden");
        de.put("listaKeskeneraiset", "Anderes unvollendetes Spiel auswählen");
        de.put("listaPaattyneet", "Liste der beendeten Spiele");
        de.put("kieliDialogTitle", "Sprache auswählen");
        de.put("kieliDialogHeader", "Spielsprache festlegen");
        de.put("esikatselu", "Spielfeldzustand:");
        de.put("noMovesLost", "Keine Züge mehr. Sie haben das Spiel verloren!");
        de.put("errOutOfBounds", "Ungültiger Zug: Das Teil muss sich vollständig innerhalb des Spielfelds befinden.");
        de.put("errNotMoved", "Ungültiger Zug: Das Teil muss sich auf eine neue Position bewegen (mindestens ein neues Feld).");
        de.put("errOverlapOpponent", "Ungültiger Zug: Das Teil darf das gegnerische Teil nicht überlappen.");
        de.put("errOverlapCoin", "Ungültiger Zug: Das Teil darf Münzen nicht überlappen.");
        translations.put("de", de);

        // --- SPANISH (es) ---
        Map<String, String> es = new HashMap<>();
        es.put("valikko", "Menú");
        es.put("keskeneraiset", "Seleccionar otra partida inacabada");
        es.put("muokkaaNimia", "Editar nombres de jugadores");
        es.put("paattyneet", "Partidas terminadas");
        es.put("lopeta", "Terminar juego");
        es.put("tietoa", "Acerca del juego");
        es.put("valitseKieli", "Seleccionar idioma");
        es.put("apua", "Ayuda");
        es.put("apuaDialogTitle", "Ayuda del Juego L");
        es.put("tietoaDialogTitle", "Acerca del Juego L");
        es.put("tietoaText", "Juego L\nVersión 1.0\nInventor: Edward de Bono (1967)");
        es.put("pelaajienNimet", "Nombres de Jugadores");
        es.put("pelaaja1", "Jugador 1");
        es.put("pelaaja2", "Jugador 2");
        es.put("tallenna", "Guardar");
        es.put("peruuta", "Cancelar");
        es.put("punainen", "Rojo");
        es.put("sininen", "Azul");
        es.put("voittaja", "¡JUEGO TERMINADO! Ganador: ");
        es.put("asetaL", "Colocar pieza L (R=Rotar, F=Reflejar, clic + V=Confirmar)");
        es.put("siirraKolikko", "Mover moneda (clic moneda + celda vacía o S=Omitir)");
        es.put("vahvista", "Confirmar (V)");
        es.put("ohita", "Omitir (S)");
        es.put("pyorita", "Rotar (R)");
        es.put("peilaa", "Reflejar (F)");
        es.put("uusiPeliBtn", "Nuevo juego (N)");
        es.put("uusiPeliPromptTitle", "Nuevo juego");
        es.put("uusiPeliPromptText", "¿Iniciar un nuevo juego L?");
        es.put("kylla", "Sí");
        es.put("ei", "No");
        es.put("lataaPeli", "Cargar juego");
        es.put("listaKeskeneraiset", "Seleccionar otra partida inacabada");
        es.put("listaPaattyneet", "Lista de partidas terminadas");
        es.put("kieliDialogTitle", "Seleccionar idioma");
        es.put("kieliDialogHeader", "Establecer idioma del juego");
        es.put("esikatselu", "Estado del tablero:");
        es.put("noMovesLost", "Sin movimientos. ¡Has perdido el juego!");
        es.put("errOutOfBounds", "Movimiento inválido: la pieza debe estar completamente dentro del tablero.");
        es.put("errNotMoved", "Movimiento inválido: la pieza debe moverse a una nueva posición (al menos una celda nueva).");
        es.put("errOverlapOpponent", "Movimiento inválido: la pieza no puede superponerse a la del oponente.");
        es.put("errOverlapCoin", "Movimiento inválido: la pieza no puede superponerse a las monedas.");
        translations.put("es", es);
    }

    public static String getTranslation(String key, String lang) {
        Map<String, String> map = translations.get(lang);
        if (map == null) {
            map = translations.get("fi"); // Fallback to Finnish
        }
        String val = map.get(key);
        if (val == null) {
            return key; // Return key if not translated
        }
        return val;
    }
}

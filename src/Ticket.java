public class Ticket implements Comparable<Ticket> {
    private String codice;
    private String testoDettaglio;
    private String priorita;
    private Long orarioIngresso;

    public Ticket(String codice, String testoDettaglio, String priorita, Long orarioIngresso) {
        this.codice = codice;
        this.testoDettaglio = testoDettaglio;
        this.priorita = priorita;
        this.orarioIngresso = orarioIngresso;
    }

    public String getCodice() {
        return codice;
    }
    public String getTestoDettaglio() {
        return testoDettaglio;
    }
    public String getPriorita() {
        return priorita;
    }
    public Long getOrarioIngresso() {
        return orarioIngresso;
    }

    //ritorna il grado di urgenza (1 max urgenza)
    public int calcolaGradoUrgenza() {
        return switch (priorita.toUpperCase()) {
            case "CRITICO" -> 1;
            case "ALTO"    -> 2;
            case "MEDIO"   -> 3;
            case "BASSO"   -> 4;
            default        -> 999;
        };
    }

    public int stimaDurataIntervento() {
        return switch (priorita.toUpperCase()) {
            case "CRITICO" -> 15;
            case "ALTO"    -> 30;
            case "MEDIO"   -> 60;
            case "BASSO"   -> 120;
            default        -> 0;
        };
    }

    @Override
    public int compareTo(Ticket altro) {
        int confrontoPrio = Integer.compare(this.calcolaGradoUrgenza(), altro.calcolaGradoUrgenza());
        if (confrontoPrio != 0) {
            return confrontoPrio;
        }
        //ordine temporale (fifo) per parita di urgenza
        return Long.compare(this.orarioIngresso, altro.orarioIngresso);
    }

    @Override
    public String toString() {
        return "#" + codice + " [" + priorita + "] - " + testoDettaglio;
    }
}
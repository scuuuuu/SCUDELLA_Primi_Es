public class RichiestaHttp {
    private String indirizzoIp;
    private String risorsa;
    private int codiceStato;
    private long latenzaMs;
    private long momentoEsecuzione;

    public RichiestaHttp(String indirizzoIp, String risorsa, int codiceStato, long latenzaMs, long momentoEsecuzione) {
        this.indirizzoIp = indirizzoIp;
        this.risorsa = risorsa;
        this.codiceStato = codiceStato;
        this.latenzaMs = latenzaMs;
        this.momentoEsecuzione = momentoEsecuzione;
    }

    public String getIndirizzoIp() {
        return indirizzoIp;
    }
    public String getRisorsa() {
        return risorsa;
    }
    public int getCodiceStato() {
        return codiceStato;
    }
    public long getLatenzaMs() {
        return latenzaMs;
    }
    public long getMomentoEsecuzione() {
        return momentoEsecuzione;
    }

    @Override
    public String toString() {
        return indirizzoIp + " -> " + risorsa + " (" + codiceStato + ") " + latenzaMs + "ms";
    }
}
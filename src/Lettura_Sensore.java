import java.util.Optional;

//eccezione personalizzata
class AnomaliaParametroException extends RuntimeException {
    public AnomaliaParametroException(String motivo) {
        super(motivo);
    }
}

public class Lettura_Sensore {
    private Double tempC;
    private Integer umiditaPerc;
    private Long timestamp;
    private Boolean avvisoBatteria;

    public Lettura_Sensore(Double tempC, Integer umiditaPerc, Long timestamp, Boolean avvisoBatteria) {
        this.tempC = tempC;
        this.umiditaPerc = umiditaPerc;
        this.timestamp = timestamp;
        this.avvisoBatteria = avvisoBatteria;
    }

    public Double getTempC() { return tempC; }
    public Integer getUmiditaPerc() { return umiditaPerc; }
    public Long getTimestamp() { return timestamp; }
    public Boolean getAvvisoBatteria() { return avvisoBatteria; }

    public static Optional<Lettura_Sensore> estraiDatiDaStringa(String inputGrezzo) {
        if (inputGrezzo == null || inputGrezzo.isBlank()) {
            return Optional.empty();
        }

        Double temperatura = null;
        Integer umidita = null;
        Long ts = null;
        Boolean batLow = null;

        String[] coppie = inputGrezzo.split(";");
        for (String c : coppie) {
            String[] kv = c.split("=", 2);
            if (kv.length < 2) continue;

            String tag = kv[0].trim().toLowerCase();
            String val = kv[1].trim();

            if (val.isEmpty()) continue;

            switch (tag) {
                case "temp":
                    try {
                        temperatura = Double.parseDouble(val);
                    } catch (NumberFormatException e) {
                        System.out.println("[WARNING] Parsing temperatura fallito: " + val);
                    }
                    break;

                case "umid":
                    try {
                        int u = Integer.parseInt(val);
                        if (u < 0 || u > 100) {
                            throw new AnomaliaParametroException("Valore umidità errato: " + u);
                        }
                        umidita = u;
                    } catch (NumberFormatException e) {
                        System.out.println("[WARNING] Parsing umidità fallito: " + val);
                    } catch (AnomaliaParametroException e) {
                        System.out.println("[ALERT] " + e.getMessage());
                    }
                    break;

                case "ts":
                    try {
                        ts = Long.parseLong(val);
                    } catch (NumberFormatException e) {
                        System.out.println("[WARNING] Parsing timestamp fallito: " + val);
                    }
                    break;

                case "batt_low":
                    batLow = Boolean.parseBoolean(val);
                    break;

                default:
                    //campo sconosciuto, ignorato
                    break;
            }
        }

        return Optional.of(new Lettura_Sensore(temperatura, umidita, ts, batLow));
    }

    @Override
    public String toString() {
        return "SensoreData[T=" + tempC + "°C, U=" + umiditaPerc + "%, TS=" + timestamp + ", LowBat=" + avvisoBatteria + "]";
    }
}
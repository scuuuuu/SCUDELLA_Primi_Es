import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {

    //==========================================
    //esercizio 1
    //==========================================
    public static void eseguiAnalisiSensori() {
        System.out.println("--- TEST ESERCIZIO 1 ---");

        String[] flussiMock = {
                "temp=23.5;umid=61;ts=1732000000;batt_low=false",
                "temp=xx.xx;umid=50;ts=1732000001;batt_low=false",
                "temp=21.0;umid=150;ts=1732000002;batt_low=true",
                "temp=;umid=40;ts=1732000003;batt_low=false",
                "temp=19.8;umid=80;ts=abc;batt_low=true",
                "temp=25.2;umid=55;ts=1732000005;batt_low=false",
                "temp=18.0;umid=30;ts=1732000006;batt_low=",
                "temp=22.1;umid=65;ts=1732000007;batt_low=true"
        };

        List<Lettura_Sensore> rilevazioniValide = new ArrayList<>();
        int contatoreScarti = 0;
        double sommaTemperature = 0.0;
        int conteggioTempCorrette = 0;

        for (String raw : flussiMock) {
            Optional<Lettura_Sensore> optSensore = Lettura_Sensore.estraiDatiDaStringa(raw);
            if (optSensore.isPresent()) {
                Lettura_Sensore ls = optSensore.get();
                rilevazioniValide.add(ls);

                if (ls.getTempC() != null) {
                    sommaTemperature += ls.getTempC();
                    conteggioTempCorrette++;
                } else {
                    contatoreScarti++;
                }
            }
        }

        System.out.println("\n--- RIEPILOGO SENSORI ---");
        System.out.println("Pacchetti letti correttamente: " + rilevazioniValide.size());
        System.out.println("Letture con temperatura non valida: " + contatoreScarti);
        double mediaTemp = (conteggioTempCorrette > 0) ? (sommaTemperature / conteggioTempCorrette) : 0.0;
        System.out.printf("Temperatura media rilevata: %.2f °C\n", mediaTemp);

        //dimostrazione comportamento cache integer vs nuovi oggetti
        System.out.println("\n--- DIMOSTRAZIONE CACHE AUTOBOXING ---");
        Integer numA1 = 80, numA2 = 80;
        System.out.println("Valori in range (-128..127) con == : " + (numA1 == numA2)); //true

        Integer numB1 = 300, numB2 = 300;
        System.out.println("Valori fuori range con == : " + (numB1 == numB2)); //false
        System.out.println("Valori fuori range con .equals() : " + Objects.equals(numB1, numB2)); //true
    }

    //==========================================
    //esercizio 2
    //==========================================
    public static void eseguiAnalisiLogHttp() {
        System.out.println("\n--- TEST ESERCIZIO 2 ---");

        ArrayList<RichiestaHttp> registroTotale = new ArrayList<>();
        Deque<RichiestaHttp> ultimePagineAperte = new ArrayDeque<>(); //uso deque al posto di linkedlist
        Set<String> blacklistIp = new HashSet<>();
        NavigableSet<Long> tempiRispostaOrdinati = new TreeSet<>();

        String[] poolIp = {"10.0.0.1", "192.168.1.5", "172.16.0.10", "10.0.0.2", "192.168.1.9"};
        int[] codiciStatus = {200, 200, 404, 500, 503, 201};
        Random rng = new Random();

        for (int i = 0; i < 30; i++) {
            String ip = poolIp[rng.nextInt(poolIp.length)];
            String endpoint = "/endpoint/v" + rng.nextInt(5);
            int status = codiciStatus[rng.nextInt(codiciStatus.length)];
            long latenza = 30 + rng.nextInt(970);

            RichiestaHttp req = new RichiestaHttp(ip, endpoint, status, latenza, System.currentTimeMillis());

            registroTotale.add(req);

            //mantiene una finestra scorrevole di 10 elementi
            ultimePagineAperte.addLast(req);
            if (ultimePagineAperte.size() > 10) {
                ultimePagineAperte.removeFirst();
            }

            if (status >= 400) {
                blacklistIp.add(ip);
            }

            tempiRispostaOrdinati.add(latenza);
        }

        //calcolo 90 percentile
        Long valore90Percentile = null;
        if (!tempiRispostaOrdinati.isEmpty()) {
            int posTarget = (int) Math.ceil(0.90 * tempiRispostaOrdinati.size()) - 1;
            Iterator<Long> iter = tempiRispostaOrdinati.iterator();
            for (int k = 0; k <= posTarget && iter.hasNext(); k++) {
                valore90Percentile = iter.next();
            }
        }

        System.out.println("Richieste elaborate: " + registroTotale.size());
        System.out.println("Indirizzi IP unici con errori: " + blacklistIp.size());
        System.out.println("Tempo risposta 90° percentile: " + valore90Percentile + " ms");
    }

    //==========================================
    //esercizio 3
    //==========================================
    public static void eseguiGestioneTicket() {
        System.out.println("\n--- TEST ESERCIZIO 3 ---");

        Path sorgenteCsv = Paths.get("ticket.csv");
        PriorityQueue<Ticket> codaLavorazione = new PriorityQueue<>();
        List<Ticket> registroCritici = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(sorgenteCsv)) {
            String riga;
            while ((riga = br.readLine()) != null) {
                if (riga.isBlank() || riga.toLowerCase().startsWith("id")) {
                    continue;
                }

                String[] campi = riga.split(",");
                if (campi.length < 4) continue;

                try {
                    String id = campi[0].trim();
                    String desc = campi[1].trim();
                    String prio = campi[2].trim().toUpperCase();
                    long timestamp = Long.parseLong(campi[3].trim());

                    Ticket t = new Ticket(id, desc, prio, timestamp);
                    codaLavorazione.add(t);

                    if (prio.equals("CRITICO")) {
                        registroCritici.add(t);
                        if (registroCritici.size() > 2) {
                            System.out.println("[WARNING SYSTEM] Attenzione: più di 2 ticket CRITICI simultanei!");
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Errore parsing riga CSV: " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Impossibile trovare il file ticket.csv");
            return;
        }

        int tempoAccumulato = 0;
        int contatoreUrgentiDiFila = 0;
        int totCritici = 0, totAlti = 0, totMedi = 0, totBassi = 0;

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("report_lavorazione.txt"))) {
            bw.write("========= PIANO DI ELABORAZIONE TICKET =========\n\n");

            while (!codaLavorazione.isEmpty()) {
                Ticket t = codaLavorazione.poll();
                registroCritici.remove(t);

                switch (t.getPriorita()) {
                    case "CRITICO" -> totCritici++;
                    case "ALTO"    -> totAlti++;
                    case "MEDIO"   -> totMedi++;
                    case "BASSO"   -> totBassi++;
                }

                if (t.getPriorita().equals("CRITICO") || t.getPriorita().equals("ALTO")) {
                    contatoreUrgentiDiFila++;
                    if (contatoreUrgentiDiFila > 5) {
                        tempoAccumulato += 10; //pause obbligatorie
                        bw.write("--- [PAUSA TECNICA RIGENERATIVA: 10 MIN] ---\n");
                        contatoreUrgentiDiFila = 1;
                    }
                } else {
                    contatoreUrgentiDiFila = 0;
                }

                tempoAccumulato += t.stimaDurataIntervento();

                bw.write(String.format("ID Ticket: %s | Priorità: %s | Tempo cumulato: %d min\n",
                        t.getCodice(), t.getPriorita(), tempoAccumulato));
            }

            bw.write("\n================ STATISTICHE ================\n");
            bw.write("Totale lavorati: " + (totCritici + totAlti + totMedi + totBassi) + "\n");
            bw.write("Critici: " + totCritici + " | Alti: " + totAlti + " | Medi: " + totMedi + " | Bassi: " + totBassi + "\n");
            bw.write("Tempo totale di lavorazione stimato: " + tempoAccumulato + " min\n");

            System.out.println("File 'report_lavorazione.txt' creato correttamente.");

        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del report: " + e.getMessage());
        }
    }

    //==========================================
    //main
    //==========================================
    public static void main(String[] args) {
        eseguiAnalisiSensori();
        eseguiAnalisiLogHttp();
        eseguiGestioneTicket();
    }
}
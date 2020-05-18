package ro.pub.cs.systems.eim.practicaltest02;

import java.time.LocalDateTime;

public class BitcoinRateInfo {
    public String currency;
    public String lastUpdated;
    public String rate;

    public BitcoinRateInfo(String currency, String lastUpdated, String rate) {
        this.currency = currency;
        this.lastUpdated = lastUpdated;
        this.rate = rate;
    }
}

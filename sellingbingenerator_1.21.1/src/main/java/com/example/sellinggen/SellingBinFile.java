package com.example.sellinggen.json;

import java.util.List;
import java.util.Map;

public class SellingBinFile {

    public Map<String, SettingsTier> settings;
    public List<TradeEntry> trades;

    public static class SettingsTier {
        public int updateDelayTicks;
        public int inventoryRows;

        public SettingsTier(int updateDelayTicks, int inventoryRows) {
            this.updateDelayTicks = updateDelayTicks;
            this.inventoryRows = inventoryRows;
        }
    }
}

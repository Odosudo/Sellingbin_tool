package com.example.sellinggen.json;

public class TradeEntry {

    public InputDef input;
    public OutputDef output;

    public static class InputDef {
        public String filter;
        public int count;

        public InputDef(String filter, int count) {
            this.filter = filter;
            this.count = count;
        }
    }

    public static class OutputDef {
        public String item;
        public int count;

        public OutputDef(String item, int count) {
            this.item = item;
            this.count = count;
        }
    }

    public TradeEntry(InputDef input, OutputDef output) {
        this.input = input;
        this.output = output;
    }
}

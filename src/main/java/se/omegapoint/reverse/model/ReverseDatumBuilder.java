package se.omegapoint.reverse.model;

public class ReverseDatumBuilder {


    private String data;
    private String reversedData;

    public ReverseDatumBuilder withData(String data) {
        this.data = data;
        return this;
    }

    public ReverseDatumBuilder withReversedData(String s) {
        this.reversedData = s;
        return this;
    }

    public ReverseDatum build() {
         ReverseDatum datum = new ReverseDatum();
         datum.data = this.data;
         datum.reversedData = this.reversedData;
         return datum;
    }
}

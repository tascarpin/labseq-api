package org.altice.labseq.dto;

public class LabseqResponse {
    private long n;
    private String result;

    public LabseqResponse(long n, String result) {
        this.n = n;
        this.result = result;
    }

    public long getN() {
        return n;
    }

    public String getResult() {
        return result;
    }
}

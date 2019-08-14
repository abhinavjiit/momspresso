package com.mycity4kids.models.response;

/**
 * Created by hemant on 4/5/17.
 */
public class LanguageRanksModel implements Comparable<LanguageRanksModel> {

    private String langValue;
    private String langKey;
    private int rank;

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getLangValue() {
        return langValue;
    }

    public void setLangValue(String langValue) {
        this.langValue = langValue;
    }

    @Override
    public int compareTo(LanguageRanksModel another) {
        return this.getRank() - another.getRank();
    }
}

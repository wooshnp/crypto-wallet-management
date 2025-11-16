package com.spicep.cryptowallet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


//Supports intervals for CoinCap historical price data
@Getter
@AllArgsConstructor
public enum CoinCapInterval {
    MINUTE_1("m1"),
    MINUTE_5("m5"),
    MINUTE_15("m15"),
    MINUTE_30("m30"),
    HOUR_1("h1"),
    HOUR_2("h2"),
    HOUR_6("h6"),
    HOUR_12("h12"),
    DAY_1("d1");

    private final String value;
}

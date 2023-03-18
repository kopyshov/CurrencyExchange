package model;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;

public record ExchangeRate(
        int id,
        Currency baseCurrency,
        Currency targetCurrency,
        BigDecimal rate,
        @Expose
        Double amount,
        @Expose
        BigDecimal convertedAmount
        )
{
}

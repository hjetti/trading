package com.example.trading.service;

import jakarta.annotation.PostConstruct;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.account.Account;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarAdjustment;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarFeed;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.snapshot.Snapshot;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TradingService {
    private AlpacaAPI alpacaAPI;

    private final String BUY = "BUY";
    private final String DO_NOT_BUY = "DO NOT BUY";

    @Value("${key_id}")
    private String keyId;

    @Value("${secret_key}")
    private String secretKey;
    @Autowired
    public TradingService(@Value("${key_id}") String keyId, @Value("${secret_key}") String secretKey) {
        alpacaAPI = new AlpacaAPI(keyId, secretKey);
    }

    public TradingService(AlpacaAPI alpacaAPI) {
        this.alpacaAPI = alpacaAPI;
    }

    public Snapshot getStockPrice(String stock) throws AlpacaClientException {
        Snapshot snapshot = alpacaAPI.stockMarketData().getSnapshot(stock);
        return snapshot;
    }

    public String getClosingCostList() throws AlpacaClientException {
        boolean isApplePriceGoingUp = false;
        boolean isAvgPriceGoingUp = false;
        ZonedDateTime currentTime = ZonedDateTime.now();
        ZonedDateTime pastTime = currentTime.minusDays(50);
        System.out.println("cur: "+ currentTime + " past: "+ pastTime);
        StockBarsResponse appleHistoricalPrice = alpacaAPI.stockMarketData().getBars("AAPL", pastTime, currentTime, 1000, null, 1, BarTimePeriod.DAY, BarAdjustment.RAW, BarFeed.IEX);
        StockBarsResponse msftHistoricalPrice = alpacaAPI.stockMarketData().getBars("MSFT", pastTime, currentTime, 1000, null, 1, BarTimePeriod.DAY, BarAdjustment.RAW, BarFeed.IEX);
        StockBarsResponse googleHistoricalPrice = alpacaAPI.stockMarketData().getBars("GOOG", pastTime, currentTime, 1000, null, 1, BarTimePeriod.DAY, BarAdjustment.RAW, BarFeed.IEX);
        StockBarsResponse ibmHistoricalPrice = alpacaAPI.stockMarketData().getBars("IBM", pastTime, currentTime, 1000, null, 1, BarTimePeriod.DAY, BarAdjustment.RAW, BarFeed.IEX);

        if(appleHistoricalPrice.getBars() != null && msftHistoricalPrice.getBars() != null && googleHistoricalPrice.getBars() != null && ibmHistoricalPrice.getBars() != null){
            List<Double> appleClosingPrices = getClosingPricesList(appleHistoricalPrice);
            List<Double> msftClosingPrices = getClosingPricesList(msftHistoricalPrice);
            List<Double> googClosingPrices = getClosingPricesList(googleHistoricalPrice);
            List<Double> ibmClosingPrices = getClosingPricesList(ibmHistoricalPrice);


            if(getSimpleMovingAverage(appleClosingPrices) > getSimpleMovingAverage(appleClosingPrices.subList(0,appleClosingPrices.size()-2))){
                isApplePriceGoingUp = true;
            }

            double currentAvg = (getSimpleMovingAverage(msftClosingPrices) + getSimpleMovingAverage(googClosingPrices) + getSimpleMovingAverage(ibmClosingPrices)) / 3;
            double pastAvg = (getSimpleMovingAverage(msftClosingPrices.subList(0,msftClosingPrices.size()-2)) + getSimpleMovingAverage(googClosingPrices.subList(0, googClosingPrices.size()-2)) + getSimpleMovingAverage(ibmClosingPrices.subList(0, ibmClosingPrices.size()-2))) / 3;


            if(currentAvg > pastAvg){
                isAvgPriceGoingUp = true;
            }

            if(isApplePriceGoingUp && isAvgPriceGoingUp){
                return BUY;
            }
        }
        return DO_NOT_BUY;
    }

    private List<Double> getClosingPricesList(StockBarsResponse barsResponse){
        List<Double> closingPrices = new ArrayList<>();
        for (StockBar bar : barsResponse.getBars()) {
            double close = bar.getClose();
            closingPrices.add(close);
        }
        return closingPrices;
    }

    private double getSimpleMovingAverage(List<Double> closingPrices) {
        double sma = 0.0;
        for (int i = 0; i< closingPrices.size(); i++){
            sma = sma + closingPrices.get(i);
        }
        return sma/closingPrices.size();
    }
}


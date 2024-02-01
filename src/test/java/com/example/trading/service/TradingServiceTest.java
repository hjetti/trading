package com.example.trading.service;


import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarAdjustment;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarFeed;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.snapshot.Snapshot;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.trade.StockTrade;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.endpoint.marketdata.stock.StockMarketDataEndpoint;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBarsResponse;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class TradingServiceTest {
    AlpacaAPI alpacaAPI;
    StockMarketDataEndpoint stockMarketDataEndpoint;
    //AlpacaClient alpacaClient;


    @BeforeEach
    void init() {
        alpacaAPI = Mockito.mock(AlpacaAPI.class);
        stockMarketDataEndpoint = Mockito.mock(StockMarketDataEndpoint.class);
        //alpacaClient = Mockito.mock(AlpacaClient.class);
    }
    @Test
    public void decisionDoNotBuyTest() throws AlpacaClientException {

        TradingService tradingService = new TradingService(alpacaAPI);

        StockBarsResponse appleHistoricalPrice = mockStockBarsResponse(Arrays.asList(194.50, 194.17, 192.42, 191.73, 188.04, 184.40));
        StockBarsResponse msftHistoricalPrice = mockStockBarsResponse(Arrays.asList(404.87, 403.93, 409.72, 408.59, 397.58, 403.63));
        StockBarsResponse googleHistoricalPrice = mockStockBarsResponse(Arrays.asList(153.64, 153.79, 154.84, 153.05, 141.80, 142.84));
        StockBarsResponse ibmHistoricalPrice = mockStockBarsResponse(Arrays.asList(190.43, 187.42, 187.14, 187.87, 183.66, 185.58));

        when(alpacaAPI.stockMarketData()).thenReturn(stockMarketDataEndpoint);

        when(alpacaAPI.stockMarketData().getBars(eq("AAPL"), any(ZonedDateTime.class), any(ZonedDateTime.class), anyInt(), any(), anyInt(), any(), any(), any()))
                .thenReturn(appleHistoricalPrice);
        when(alpacaAPI.stockMarketData().getBars(eq("MSFT"), any(ZonedDateTime.class), any(ZonedDateTime.class), anyInt(), any(), anyInt(), any(), any(), any()))
                .thenReturn(msftHistoricalPrice);
        when(alpacaAPI.stockMarketData().getBars(eq("GOOG"), any(ZonedDateTime.class), any(ZonedDateTime.class), anyInt(), any(), anyInt(), any(), any(), any()))
                .thenReturn(googleHistoricalPrice);
        when(alpacaAPI.stockMarketData().getBars(eq("IBM"), any(ZonedDateTime.class), any(ZonedDateTime.class), anyInt(), any(), anyInt(), any(), any(), any()))
                .thenReturn(ibmHistoricalPrice);

        String result = tradingService.getClosingCostList();
        assertEquals("DO NOT BUY", result);
    }

    @Test
    public void decisionBuyTest() throws AlpacaClientException {

        TradingService tradingService = new TradingService(alpacaAPI);

        StockBarsResponse appleHistoricalPrice = mockStockBarsResponse(Arrays.asList(194.50, 195.17, 196.42));
        StockBarsResponse msftHistoricalPrice = mockStockBarsResponse(Arrays.asList(404.87, 405.93, 406.72));
        StockBarsResponse googleHistoricalPrice = mockStockBarsResponse(Arrays.asList(153.64, 154.79, 155.84));
        StockBarsResponse ibmHistoricalPrice = mockStockBarsResponse(Arrays.asList(190.43, 191.42, 192.14));

        when(alpacaAPI.stockMarketData()).thenReturn(stockMarketDataEndpoint);

        when(alpacaAPI.stockMarketData().getBars(eq("AAPL"), any(ZonedDateTime.class), any(ZonedDateTime.class), anyInt(), any(), anyInt(), any(), any(), any()))
                .thenReturn(appleHistoricalPrice);
        when(alpacaAPI.stockMarketData().getBars(eq("MSFT"), any(ZonedDateTime.class), any(ZonedDateTime.class), anyInt(), any(), anyInt(), any(), any(), any()))
                .thenReturn(msftHistoricalPrice);
        when(alpacaAPI.stockMarketData().getBars(eq("GOOG"), any(ZonedDateTime.class), any(ZonedDateTime.class), anyInt(), any(), anyInt(), any(), any(), any()))
                .thenReturn(googleHistoricalPrice);
        when(alpacaAPI.stockMarketData().getBars(eq("IBM"), any(ZonedDateTime.class), any(ZonedDateTime.class), anyInt(), any(), anyInt(), any(), any(), any()))
                .thenReturn(ibmHistoricalPrice);


        String result = tradingService.getClosingCostList();
        assertEquals("BUY", result);
    }

    private StockBarsResponse mockStockBarsResponse(List<Double> closingPrices) {
        StockBarsResponse response = new StockBarsResponse();
        List<StockBar> bars = new ArrayList<>();
        for (Double price : closingPrices) {
            StockBar bar = new StockBar();
            bar.setClose(price);
            bars.add(bar);
        }
        response.setBars((ArrayList<StockBar>) bars);
        return response;
    }
}

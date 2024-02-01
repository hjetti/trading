package com.example.trading.controller;

import com.example.trading.service.TradingService;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.quote.LatestStockQuoteResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.snapshot.Snapshot;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TradingController {
    private final TradingService tradingService;

    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @RequestMapping(value = "/stockSnapshot/{stock}", method = RequestMethod.GET)
    public Snapshot getStockPrice(
            @PathVariable("stock") String stock) throws Exception {
            Snapshot snapshot = tradingService.getStockPrice(stock);
            return snapshot;
    }

    @RequestMapping(value = "/tradeDecision", method = RequestMethod.GET)
    public String getHistoricalStockPrice() throws Exception {
        return tradingService.getClosingCostList();
    }


}

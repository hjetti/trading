package com.example.trading.listener;

import com.example.trading.service.TradingService;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.MarketDataMessage;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TradingListener implements MarketDataListener {
    private final TradingService tradingService;
    @Autowired
    public TradingListener(TradingService tradingService) {
        this.tradingService = tradingService;
    }


    @Override
    public void onMessage(MarketDataMessageType messageType, MarketDataMessage message) {
        System.out.printf("Received Trade Data: %s\n", message);
    }

}


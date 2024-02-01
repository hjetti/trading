package com.example.trading.configuration;

import com.example.trading.service.TradingService;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.realtime.enums.MarketDataMessageType;
import net.jacobpeterson.alpaca.websocket.marketdata.MarketDataListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class TradingConfiguration {

    @Autowired
    private TradingService tradingService;

    @Value("${key_id}")
    String keyId;
    @Value("${secret_key}")
    String secretKey;

    @Bean
    public AlpacaAPI alpacaAPI() {
        AlpacaAPI alpacaAPI = new AlpacaAPI(keyId, secretKey);
        alpacaAPI.stockMarketDataStreaming().disconnect();
        MarketDataListener marketDataListener = (messageType, message) ->
                System.out.println(messageType.name() + " : " +  message);
        alpacaAPI.stockMarketDataStreaming().setListener(marketDataListener);

        alpacaAPI.stockMarketDataStreaming().subscribeToControl(
                MarketDataMessageType.SUCCESS,
                MarketDataMessageType.SUBSCRIPTION,
                MarketDataMessageType.ERROR);

        alpacaAPI.stockMarketDataStreaming().connect();
        alpacaAPI.stockMarketDataStreaming().waitForAuthorization(5, TimeUnit.SECONDS);
        if(!alpacaAPI.stockMarketDataStreaming().isValid()){
            throw new RuntimeException();
        }

        alpacaAPI.stockMarketDataStreaming().subscribe(
                Arrays.asList("AAPL", "MSFT", "IBM", "GOOG"),
                null,
                Arrays.asList("*"));
        return alpacaAPI;
    }
}

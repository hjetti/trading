## Trading Application
This application has 3 components.
- Get Current Snapshot of a stock
- Streaming Data about stocks
- Get Trading Decision on whether to buy an APPLE stock or not.

#### Getting Started
- The first step to setup the trading application is to create a `key_Id` and a `secret_Key`. These can be obtained from Alpaca Paper Account.
- Ideally the keys must be stores in Vault but since this is not being deployed anywhere, we can use the props file to reference these keys.
- Once the application succeeds to build, you can run the application to see the steaming data being printed.
- Use POSTMAN to test the endpoints. The curl commands for each endpoints are listed below.
- Using the Alpaca Streaming API we can get the live stream data about each stock which we subscribed to.
- Also, the application listens and prints out if any trade happens in your paper account. 

### GET Current Snapshot
This is a `GET` endpoint which takes the name of stock as the path parameter and prints out the latest snapshot of the stock. 
It includes latestTrade, latestQuote, minuteBar, dailyBar and prevDailyBar information. 

Curl Command:

```agsl
curl --location 'http://localhost:8081/stockPrice/{stockSymbol}'
```

### Streaming Data about stocks
Upon launching the application, the app makes a call to Alpaca streaming endpoint to establish a connection. 
It continuously prints out the information on the stock price for each stock symbol in the console.

### GET Trading Decision
This is a `GET` endpoint which prints out either `BUY` or `DO NOT BUY` the Apple stock.
The decision is calculated by using Simple Moving Averages strategy. 
The Simple Moving Averages is calculated for `AAPL`, `MSFT`, `GOOG` and `IBM` for current and previous day to determine the going up strategy.
If `APPL` stock is going up and the average of other three are going up then we recommend to `BUY` else `DO NOT BUY`.
The period is taken as 50 for our analysis. However, we can take any number based on what type of analysis we need to perform.

Curl Command:

```agsl
curl --location 'http://localhost:8081/tradeDecision'
```
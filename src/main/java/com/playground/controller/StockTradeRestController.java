package com.playground.controller;

import com.playground.model.StockTrade;
import com.playground.service.StockTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins="http://localhost:4200")
public class StockTradeRestController {

    private final StockTradeService stockTradeService;

    public static final String TRADE_END_POINT = "/trades";
    public static final String TRADE_END_POINT_SPECIFIC_ID = "/trades/{id}";


    /*
    Creates a new trade
     */
    @PostMapping(path = TRADE_END_POINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StockTrade> createNewStockTrade(@RequestBody @Valid StockTrade stockTradeToInsert) {
        StockTrade stockTradeCreated = stockTradeService.create(stockTradeToInsert);
        return new ResponseEntity<>(stockTradeCreated, HttpStatus.CREATED);
    }

    /*
    Returns a collection of all trades
     */
    @GetMapping(path = TRADE_END_POINT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StockTrade>> getAllStockTrades(@RequestParam(required = false) final String type,
                                                              @RequestParam(required = false) final Integer userId) {
        List<StockTrade> listOfStockTrade = stockTradeService.getStockTrades(type, userId);
        return new ResponseEntity<>(listOfStockTrade, HttpStatus.OK);
    }

    /*
    Returns a specific stock trade
    */
    @GetMapping(path = TRADE_END_POINT_SPECIFIC_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StockTrade> getStockTradeById(@PathVariable final Integer id) {
        Optional<StockTrade> stockTrade = stockTradeService.getStockTrade(id);
        return stockTrade.map(trade -> new ResponseEntity<>(trade, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}

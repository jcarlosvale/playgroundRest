package com.playground.service;

import com.playground.model.StockTrade;
import com.playground.repository.StockTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class StockTradeService {

    private final StockTradeRepository stockTradeRepository;

    public List<StockTrade> getStockTrades(final String type, final Integer userId) {
        if (Objects.isNull(type) && Objects.isNull(userId)) {
            return stockTradeRepository.findAllByOrderByIdAsc();
        } else {
            if (Objects.nonNull(type) && Objects.nonNull(userId)) {
                return stockTradeRepository.findAllByTypeEqualsAndUserIdEqualsOrderByIdAsc(type, userId);
            } else {
                if (Objects.nonNull(type)) {
                    return stockTradeRepository.findAllByTypeEqualsOrderByIdAsc(type);
                } else {
                    return stockTradeRepository.findAllByUserIdEqualsOrderByIdAsc(userId);
                }
            }
        }
    }

    public Optional<StockTrade> getStockTrade(Integer id) {
        return stockTradeRepository.findById(id);
    }

    public StockTrade create(@Valid StockTrade stockTradeToInsert) {
        return stockTradeRepository.save(stockTradeToInsert);
    }
}

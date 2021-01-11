package com.playground.repository;

import com.playground.model.StockTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTradeRepository extends JpaRepository<StockTrade, Integer> {

    List<StockTrade> findAllByOrderByIdAsc();

    List<StockTrade> findAllByTypeEqualsOrderByIdAsc(String type);

    List<StockTrade> findAllByUserIdEqualsOrderByIdAsc(Integer userId);

    List<StockTrade> findAllByTypeEqualsAndUserIdEqualsOrderByIdAsc(String type, Integer userId);
}

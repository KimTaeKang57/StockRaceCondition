package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PessimisticLockStockService {

    private StockRepository stockRepository;

    public PessimisticLockStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decrease(Long id, Long quantity) {
        // lock 을 걸고 데이터를 가져오고
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);
        // 재고를 감소시킨 다음
        stock.decrease(quantity);
        // 다시 저장해준다
        stockRepository.saveAndFlush(stock);
    }
}

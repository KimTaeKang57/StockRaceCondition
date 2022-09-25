package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class StockService {

    private StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Spring 의 Transactional 은 실행하면 우리가 만든 클래스를 새로만들어서 실행한다.
     * StockService 의 클래스를 새로 만들어서 실행한다.
     * transaction 을 시작하고, decrease 메소드를 실행 한 후 *  transcation 을 적용하는 * 타이밍 때 새로운 트랜잭션이 시작
     * 될 수 있음
     **/

    /**
     * synchronized 문제점 : 하나의 프로세스 안에서만 동작함, 서버 한대에선 잘 동작하지만 서버가 여러대면 동작하지 않는다
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void decrease(Long id, Long quantity) {
        // get stock -> 재고감소 -> 저장
        Stock stock = stockRepository.findById(id).orElseThrow(NullPointerException::new);
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}

package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
//    private StockService service;
    private PessimisticLockStockService service;

    @Autowired
    private StockRepository repository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 100L);
        repository.saveAndFlush(stock);
    }

    @AfterEach
    public void after() {
        repository.deleteAll();
    }

    @Test
    void stock_decrease() throws Exception {
        //given
        service.decrease(1L, 1L);
        //when
        Stock stock = repository.findById(1L).orElseThrow(NullPointerException::new);
        //then
        assertEquals(99, stock.getQuantity());
    }

    @Test
    void 동시에_100개_요청() throws Exception {
        //given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    service.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        //then
        Stock stock = repository.findById(1L).orElseThrow(NullPointerException::new);
        // 100 - 1 * 100 = 0
        assertEquals(0L, stock.getQuantity());
    }
}
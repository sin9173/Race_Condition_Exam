package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.facade.OptimisticLockStockFacade;
import com.example.stock.repository.StockRepository;
import org.assertj.core.api.Assertions;
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
    //private StockService stockService;
    private PessimisticLockStockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 100L);

        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void stock_decrease() {
        stockService.decrease(1L, 1L);

        // 100 - 1 = 99

        Stock stock = stockRepository.findById(1L).orElseThrow();

        Assertions.assertThat(stock.getQuantity()).isEqualTo(99L);
    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException {
        int threadCount = 100;

        //비동기로 실행하는 작업을 단순화 하도록 해주는 JAVA 의 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        //다른 쓰레드에서의 작업이 완료될 때까지 대기할 수 있도록 함
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i=0 ; i<threadCount ; i++) {
            executorService.submit(() -> {
                try {
                    //Race Condition (임계 영역) 이 발생됨
                    // - 두 개 이상의 프로세스(쓰레드)가 공통자원을 읽거나 쓰는 작업을 할 때
                    //   어떤 순서로 이루어졌는지에 따라 결과가 달라지는 상황

                    // * Mutual Exclusion (상호 배제)
                    //   - 두 개 이상의 프로세스가 공용 데이터에 동시에 접근하는 것을 막아야함
                    // * Progress (진행)
                    //   - 임계영역에 프로세스가 없는 상태에서 자원에 접근하는것을 막아선 안됨
                    // * Bounded Waiting (한정 대기)
                    //   - 기아상태(starvation)를 방지하기 위해 임계영역에 들어가는 횟수에 한계(제한)가 있어야함
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        //100 - 1 * 100
        Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
    }


}
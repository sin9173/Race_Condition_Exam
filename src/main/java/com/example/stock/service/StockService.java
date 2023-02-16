package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    //재고 감소
    //synchronized 를 넣을 경우 해당메소드는 하나의 쓰레드만 접근이 가능
    //synchronized 는 하나의 프로세스에서만 유효하므로 서버가 여러대일 경우 Race Condition 이 발생

    //Transaction 이 있을 경우 
    // - StockService 를 필드로 가지는 클래스가 새로 만들어져서 실행됨
    // - decrease 는 하나의 쓰레드만 접근이 가능하지만 트랜젝션을 종료하는 메소드는 다른 쓰레드에서도 접근이 가능
    // - 트렌젝션 종료부분에서 Race Condition 이 발생
    @Transactional(
            propagation = Propagation.REQUIRES_NEW // 부모의 트랜젝션과 별도로 실행
    )
    public synchronized void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}

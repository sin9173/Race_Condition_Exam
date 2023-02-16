package com.example.stock.facade;

import com.example.stock.service.OptimisticLockStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

    private final OptimisticLockStockService optimisticLockStockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while(true) {
            try {
                optimisticLockStockService.decrease(id, quantity);
                //정상적으로 업데이트가 될 경우 빠져나옴
                break;
            } catch (Exception e) {
                //업데이트가 실패할 경우 재시도
                Thread.sleep(50);
            }
        }
    }
}

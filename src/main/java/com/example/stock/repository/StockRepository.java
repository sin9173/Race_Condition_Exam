package com.example.stock.repository;

import com.example.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;

// MYSQL 을 이용한 정합성을 맞추는 방법

// * Pessimistic Lock
//   - 실제 데이터에 Lock 을 걸어서 Lock 이 해제되기 전에는 다른 트랜젝션에서 데이터를 가져갈 수 없게 됨
//   - 충돌이 빈번하게 일어날 경우 Optimistic 보다 성능이 좋을 수 있음
//   - 정합성이 어느정도 보장됨
//   - 데드락이 발생될 수 있기 때문에 주의하여 사용해야함
//   - 별도의 Lock 을 잡기 때문에 성능저하가 발생됨

// * Optimistic Lock
//   - 데이터에 버전 정보를 둬서 update 시 버전을 검사하도록 함
//   - 업데이트가 실패할 경우 다시 데이터를 가져옴
//   - 개발자가 재시도 로직을 작성해주어야 함

// * Named Lock
//   - 이름을 가진 metadata Lock 을 획득한 후 다른 세션은 Lock 을 획들할 수 없도록 함
//   - 트랜젝션이 종료될 때 Lock 이 자동으로 해제되지 않기 때문에 별도의 명령어로 해제를 수행하거나 선점시간이 끝나야 해제됨
//   - 분산 Lock 을 구현할 때에 주로 사용
//   - 타임아웃을 구현하기 용이
public interface StockRepository extends JpaRepository<Stock, Long> {

    //Spring Data JPA 에서 Lock 기능을 편리하게 사용하도록 제공
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.id = :id")
    Stock findByIdWithPessimisticLock(Long id);

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select s from Stock s where s.id = :id")
    Stock findByIdWithOptimisticLock(Long id);
}

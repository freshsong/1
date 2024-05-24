package net.musecom.boot_board.repository;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import net.musecom.boot_board.entity.BoardEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
   
    
    @Modifying
    @Query(value = "update BoardEntity b set b.hits=b.hits+1 where b.id=:id")
    void updateHits(@Param("id") Long id); //구현만되어있는상태 -> service에서 실행
    //sort - 역순정렬
    
    @NonNull List<BoardEntity> findAll(@NonNull Sort sort);

    @Transactional
    @Modifying
    @Query(value = "delete BoardEntity b where b.id=:id and b.pass=:bpass")
    void delete(@Param("id") Long id, @Param("bpass") String bpass);

}

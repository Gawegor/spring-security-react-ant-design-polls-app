package com.example.polls.repository;

import com.example.polls.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 选举实体类的DAO实现
 * Created by rajeevkumarsingh on 20/11/17.
 */
@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    /**
     * 通过ID查找Poll
     *
     * @param pollId 选举Id
     * @return 返回可空集合
     */
    Optional<Poll> findById(Long pollId);

    /**
     * 通过用户查找其创建的所有Poll
     *
     * @param userId
     * @param pageable
     * @return
     */
    Page<Poll> findByCreatedBy(Long userId, Pageable pageable);

    /**
     * 返回用户创建的Poll数
     *
     * @param userId
     * @return
     */
    long countByCreatedBy(Long userId);

    /**
     * 查找给定的Poll ID列表中的所有Poll
     *
     * @param pollIds
     * @return
     */
    List<Poll> findByIdIn(List<Long> pollIds);

    /**
     * 重载 findByIdIn(), 使可通过给定的Sort进行排序
     *
     * @param pollIds
     * @param sort
     * @return
     */
    List<Poll> findByIdIn(List<Long> pollIds, Sort sort);
}

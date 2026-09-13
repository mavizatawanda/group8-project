package com.devops.qvs.repository;

import com.devops.qvs.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findTopByOrderByBlockIndexDesc();

    List<Block> findAllByOrderByBlockIndexAsc();

    List<Block> findByCertificateNumberOrderByBlockIndexAsc(String certificateNumber);

    Optional<Block> findByBlockHash(String blockHash);
}

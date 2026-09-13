package com.devops.qvs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "blockchain_ledger")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "block_index")
    private Long blockIndex;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 100)
    private String certificateNumber;

    @Column(nullable = false, length = 128)
    private String dataHash; // Digital fingerprint of the qualification payload

    @Column(nullable = false, length = 128)
    private String previousHash;

    @Column(nullable = false, length = 128, unique = true)
    private String blockHash; // SHA-256 (blockIndex + timestamp + certificateNumber + dataHash + previousHash + nonce)

    @Column(nullable = false)
    private Long nonce;

    @Column(nullable = false, length = 50)
    private String actionType; // e.g. "GENESIS", "ISSUE", "REVOKE"

    @Column(length = 100)
    private String minedBy; // Node / Institution issuer
}

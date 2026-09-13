package com.devops.qvs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockDto {
    private Long id;
    private Long blockIndex;
    private LocalDateTime timestamp;
    private String certificateNumber;
    private String dataHash;
    private String previousHash;
    private String blockHash;
    private Long nonce;
    private String actionType;
    private String minedBy;
    private boolean valid;
}

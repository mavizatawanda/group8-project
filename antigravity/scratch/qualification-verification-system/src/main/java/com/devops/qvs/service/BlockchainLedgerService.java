package com.devops.qvs.service;

import com.devops.qvs.dto.BlockDto;
import com.devops.qvs.model.Block;
import com.devops.qvs.repository.BlockRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainLedgerService {

    public static final String GENESIS_PREV_HASH = "0000000000000000000000000000000000000000000000000000000000000000";

    private final BlockRepository blockRepository;

    @PostConstruct
    @Transactional
    public void initGenesisBlock() {
        if (blockRepository.count() == 0) {
            log.info("Initializing Blockchain Genesis Block for Qualification Verification System...");
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            String genesisData = "QVS-GENESIS-INIT-ROOT-LEDGER";
            String genesisDataHash = sha256(genesisData);
            long nonce = 42L;
            String genesisBlockHash = calculateBlockHash(0L, now, "GENESIS-ROOT", genesisDataHash, GENESIS_PREV_HASH, nonce);

            Block genesisBlock = Block.builder()
                    .blockIndex(0L)
                    .timestamp(now)
                    .certificateNumber("GENESIS-ROOT")
                    .dataHash(genesisDataHash)
                    .previousHash(GENESIS_PREV_HASH)
                    .blockHash(genesisBlockHash)
                    .nonce(nonce)
                    .actionType("GENESIS")
                    .minedBy("QVS-GENESIS-NODE-01")
                    .build();

            blockRepository.save(genesisBlock);
            log.info("Genesis block successfully forged. Block Hash: {}", genesisBlockHash);
        }
    }

    /**
     * Mines a new block on the blockchain ledger for a qualification event (ISSUE, REVOKE, UPDATE).
     */
    @Transactional
    public synchronized Block mineBlock(String certificateNumber, String dataHash, String actionType, String minedBy) {
        Block latestBlock = blockRepository.findTopByOrderByBlockIndexDesc()
                .orElseThrow(() -> new IllegalStateException("Blockchain has no genesis block initialized."));

        long newIndex = latestBlock.getBlockIndex() + 1;
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        String prevHash = latestBlock.getBlockHash();
        long nonce = generateProofOfIntegrity(newIndex, prevHash, dataHash);

        String newBlockHash = calculateBlockHash(newIndex, now, certificateNumber, dataHash, prevHash, nonce);

        Block block = Block.builder()
                .blockIndex(newIndex)
                .timestamp(now)
                .certificateNumber(certificateNumber)
                .dataHash(dataHash)
                .previousHash(prevHash)
                .blockHash(newBlockHash)
                .nonce(nonce)
                .actionType(actionType != null ? actionType : "ISSUE")
                .minedBy(minedBy != null ? minedBy : "QVS-VALIDATOR-NODE")
                .build();

        Block saved = blockRepository.save(block);
        log.info("New Block #{} mined on QVS Ledger for Cert [{}]. Hash: {}", newIndex, certificateNumber, newBlockHash);
        return saved;
    }

    /**
     * Verifies cryptographic integrity of the entire blockchain.
     */
    @Transactional(readOnly = true)
    public boolean validateChain() {
        List<Block> chain = blockRepository.findAllByOrderByBlockIndexAsc();
        if (chain.isEmpty()) {
            return false;
        }

        for (int i = 0; i < chain.size(); i++) {
            Block current = chain.get(i);

            // 1. Validate block internal hash integrity
            String recalculatedHash = calculateBlockHash(
                    current.getBlockIndex(),
                    current.getTimestamp(),
                    current.getCertificateNumber(),
                    current.getDataHash(),
                    current.getPreviousHash(),
                    current.getNonce()
            );

            if (!recalculatedHash.equalsIgnoreCase(current.getBlockHash())) {
                log.error("Blockchain corruption detected at Block #{}: expected hash {} but found {}",
                        current.getBlockIndex(), recalculatedHash, current.getBlockHash());
                return false;
            }

            // 2. Validate previous hash linkage
            if (i > 0) {
                Block previous = chain.get(i - 1);
                if (!current.getPreviousHash().equalsIgnoreCase(previous.getBlockHash())) {
                    log.error("Blockchain chain link broken between Block #{} and Block #{}",
                            previous.getBlockIndex(), current.getBlockIndex());
                    return false;
                }
            } else {
                if (!GENESIS_PREV_HASH.equalsIgnoreCase(current.getPreviousHash())) {
                    log.error("Genesis block previous hash is invalid");
                    return false;
                }
            }
        }

        return true;
    }

    @Transactional(readOnly = true)
    public List<BlockDto> getAllBlocks() {
        return blockRepository.findAllByOrderByBlockIndexAsc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BlockDto> getBlocksForCertificate(String certificateNumber) {
        return blockRepository.findByCertificateNumberOrderByBlockIndexAsc(certificateNumber).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getChainHeight() {
        return blockRepository.count();
    }

    public String calculateBlockHash(Long index, LocalDateTime time, String certNum, String dataHash, String prevHash, Long nonce) {
        LocalDateTime normalizedTime = time != null ? time.truncatedTo(ChronoUnit.SECONDS) : LocalDateTime.MIN;
        String raw = String.format("%d|%s|%s|%s|%s|%d", index, normalizedTime.toString(), certNum, dataHash, prevHash, nonce);
        return sha256(raw);
    }

    private long generateProofOfIntegrity(long index, String prevHash, String dataHash) {
        long nonce = 0;
        while (true) {
            String candidate = sha256(index + "|" + prevHash + "|" + dataHash + "|" + nonce);
            if (candidate.startsWith("00")) { // Light Proof-of-Authority/Integrity difficulty target
                return nonce;
            }
            nonce++;
            if (nonce > 100000) {
                return nonce; // safety break
            }
        }
    }

    private BlockDto mapToDto(Block block) {
        return BlockDto.builder()
                .id(block.getId())
                .blockIndex(block.getBlockIndex())
                .timestamp(block.getTimestamp())
                .certificateNumber(block.getCertificateNumber())
                .dataHash(block.getDataHash())
                .previousHash(block.getPreviousHash())
                .blockHash(block.getBlockHash())
                .nonce(block.getNonce())
                .actionType(block.getActionType())
                .minedBy(block.getMinedBy())
                .valid(true)
                .build();
    }

    private String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}

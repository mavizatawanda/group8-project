package com.devops.qvs.service;

import com.devops.qvs.dto.BlockDto;
import com.devops.qvs.model.Block;
import com.devops.qvs.repository.BlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: Blockchain Credential Ledger Service")
class BlockchainLedgerServiceTest {

    @Mock
    private BlockRepository blockRepository;

    @InjectMocks
    private BlockchainLedgerService blockchainLedgerService;

    private Block genesisBlock;
    private Block blockOne;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        String genesisHash = blockchainLedgerService.calculateBlockHash(
                0L, now, "GENESIS-ROOT", "genesis_payload_hash", BlockchainLedgerService.GENESIS_PREV_HASH, 42L
        );

        genesisBlock = Block.builder()
                .id(1L)
                .blockIndex(0L)
                .timestamp(now)
                .certificateNumber("GENESIS-ROOT")
                .dataHash("genesis_payload_hash")
                .previousHash(BlockchainLedgerService.GENESIS_PREV_HASH)
                .blockHash(genesisHash)
                .nonce(42L)
                .actionType("GENESIS")
                .minedBy("QVS-GENESIS-NODE")
                .build();

        String blockOneHash = blockchainLedgerService.calculateBlockHash(
                1L, now.plusMinutes(1), "QVS-2024-BSC-8891", "data_hash_sarah", genesisHash, 100L
        );

        blockOne = Block.builder()
                .id(2L)
                .blockIndex(1L)
                .timestamp(now.plusMinutes(1))
                .certificateNumber("QVS-2024-BSC-8891")
                .dataHash("data_hash_sarah")
                .previousHash(genesisHash)
                .blockHash(blockOneHash)
                .nonce(100L)
                .actionType("ISSUE")
                .minedBy("TECH-UNI-NODE")
                .build();
    }

    @Test
    @DisplayName("Should initialize genesis block when repository is empty")
    void testInitGenesisBlockWhenEmpty() {
        when(blockRepository.count()).thenReturn(0L);

        blockchainLedgerService.initGenesisBlock();

        verify(blockRepository).save(any(Block.class));
    }

    @Test
    @DisplayName("Should mine a new block linked to the latest block hash")
    void testMineBlock() {
        when(blockRepository.findTopByOrderByBlockIndexDesc()).thenReturn(Optional.of(genesisBlock));
        when(blockRepository.save(any(Block.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Block newBlock = blockchainLedgerService.mineBlock("QVS-2024-TEST-001", "some_payload_hash", "ISSUE", "TEST_NODE");

        assertNotNull(newBlock);
        assertEquals(1L, newBlock.getBlockIndex());
        assertEquals(genesisBlock.getBlockHash(), newBlock.getPreviousHash());
        assertEquals("QVS-2024-TEST-001", newBlock.getCertificateNumber());
        verify(blockRepository).save(any(Block.class));
    }

    @Test
    @DisplayName("Should return true when validating a healthy, un-tampered chain")
    void testValidateChainSuccess() {
        when(blockRepository.findAllByOrderByBlockIndexAsc()).thenReturn(List.of(genesisBlock, blockOne));

        boolean valid = blockchainLedgerService.validateChain();

        assertTrue(valid);
    }

    @Test
    @DisplayName("Should detect tampering when a block payload hash has been modified")
    void testValidateChainFailsOnTampering() {
        Block tamperedBlockOne = Block.builder()
                .id(2L)
                .blockIndex(1L)
                .timestamp(blockOne.getTimestamp())
                .certificateNumber("QVS-2024-BSC-8891")
                .dataHash("TAMPERED_PAYLOAD_HASH")
                .previousHash(genesisBlock.getBlockHash())
                .blockHash(blockOne.getBlockHash()) // keeping original hash despite payload change
                .nonce(100L)
                .actionType("ISSUE")
                .minedBy("FORGED-NODE")
                .build();

        when(blockRepository.findAllByOrderByBlockIndexAsc()).thenReturn(List.of(genesisBlock, tamperedBlockOne));

        boolean valid = blockchainLedgerService.validateChain();

        assertFalse(valid);
    }

    @Test
    @DisplayName("Should retrieve all blocks as DTOs")
    void testGetAllBlocks() {
        when(blockRepository.findAllByOrderByBlockIndexAsc()).thenReturn(List.of(genesisBlock, blockOne));

        List<BlockDto> dtos = blockchainLedgerService.getAllBlocks();

        assertEquals(2, dtos.size());
        assertEquals("GENESIS-ROOT", dtos.get(0).getCertificateNumber());
        assertEquals("QVS-2024-BSC-8891", dtos.get(1).getCertificateNumber());
    }
}

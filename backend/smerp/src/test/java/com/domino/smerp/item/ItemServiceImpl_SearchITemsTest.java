package com.domino.smerp.item;

import com.domino.smerp.common.dto.PageResponse;
import com.domino.smerp.item.dto.request.SearchItemRequest;
import com.domino.smerp.item.dto.response.ItemListResponse;
import com.domino.smerp.item.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImpl_SearchITemsTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    // ✅ 정상 조회 케이스 ---------------------------------------------
    @Test
    @DisplayName("검색 조건에 맞는 품목 목록을 정상적으로 반환한다")
    void searchItems_success() {
        // 🧩 Arrange
        SearchItemRequest request = SearchItemRequest.builder()
                                                     .status("정상")
                                                     .name("볼트")
                                                     .specification("M8x20")
                                                     .groupName1("철물")
                                                     .groupName2("나사")
                                                     .groupName3("공구")
                                                     .build();

        Pageable pageable = PageRequest.of(0, 10);

        // 가짜 Item 데이터 (조회 결과)
        Item item = Item.builder()
                        .itemId(1L)
                        .name("볼트")
                        .rfid("RFID-001")
                        .build();

        Page<Item> mockPage = new PageImpl<>(List.of(item));

        when(itemRepository.searchItems(request, pageable)).thenReturn(mockPage);

        // Act
        PageResponse<ItemListResponse> response = itemService.searchItems(request, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("볼트", response.getContent().getFirst().getName());
        assertEquals("RFID-001", response.getContent().getFirst().getRfid());
        verify(itemRepository, times(1)).searchItems(request, pageable);
    }

    // 조회 결과 없음 케이스
    @Test
    @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
    void searchItems_empty() {
        // 🧩 Arrange
        SearchItemRequest request = SearchItemRequest.builder()
                                                     .status("정상")
                                                     .name("없는품목")
                                                     .build();

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> emptyPage = new PageImpl<>(Collections.emptyList());
        when(itemRepository.searchItems(request, pageable)).thenReturn(emptyPage);

        // ⚙️ Act
        PageResponse<ItemListResponse> response = itemService.searchItems(request, pageable);

        // ✅ Assert
        assertNotNull(response);
        assertEquals(0, response.getTotalElements());
        assertTrue(response.getContent().isEmpty());
        verify(itemRepository, times(1)).searchItems(request, pageable);
    }
}

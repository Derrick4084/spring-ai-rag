package com.derocode.rag.records;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Double qty,
        BigDecimal price,
        String categoryName
) {
}

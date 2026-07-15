package com.derocode.rag.tools;

import com.derocode.rag.entities.Product;
import com.derocode.rag.repository.ProductRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ProductTool {


    private final ProductRepository productRepository;

    public ProductTool(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Tool(description = """
Returns a product for a NUMERIC product ID.
Only call this tool if the user explicitly provides a numeric product ID.
Do not call this tool for product names, document titles, report names,
or general questions.
""")
    public Product getProductInfo(@ToolParam(description = "The id of the product") Long id) {
        return productRepository.findById(id).orElseThrow();
    }
}

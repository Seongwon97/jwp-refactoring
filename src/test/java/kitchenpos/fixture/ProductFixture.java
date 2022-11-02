package kitchenpos.fixture;

import java.math.BigDecimal;

import kitchenpos.menu.domain.Product;
import kitchenpos.menu.application.dto.request.ProductRequest;

@SuppressWarnings("NonAsciiCharacters")
public enum ProductFixture {

    후라이드("후라이드", new BigDecimal(16_000)),
    양념치킨("양념치킨", new BigDecimal(16_000)),
    ;

    private final String name;
    private final BigDecimal price;

    ProductFixture(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public ProductRequest toRequest() {
        return new ProductRequest(name, price);
    }

    public Product toProduct() {
        return new Product(name, price);
    }
}

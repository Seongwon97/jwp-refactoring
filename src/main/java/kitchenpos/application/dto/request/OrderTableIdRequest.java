package kitchenpos.application.dto.request;

public class OrderTableIdRequest {

    private Long id;

    public OrderTableIdRequest() {
    }

    public OrderTableIdRequest(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
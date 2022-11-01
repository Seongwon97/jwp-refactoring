package kitchenpos.application;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.application.dto.request.ChangeNumOfTableGuestsRequest;
import kitchenpos.application.dto.request.ChangeOrderTableEmptyRequest;
import kitchenpos.application.dto.request.OrderTableRequest;
import kitchenpos.application.dto.response.OrderTableResponse;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

@Service
@Transactional
public class TableService {

    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;

    public TableService(OrderRepository orderRepository, OrderTableRepository orderTableRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
    }

    public OrderTableResponse create(OrderTableRequest request) {
        OrderTable orderTable = request.toOrderTable();

        OrderTable savedOrderTable = orderTableRepository.save(orderTable);
        return new OrderTableResponse(savedOrderTable);
    }

    @Transactional(readOnly = true)
    public List<OrderTableResponse> list() {
        List<OrderTable> orderTables = orderTableRepository.findAll();
        return orderTables.stream()
                .map(OrderTableResponse::new)
                .collect(Collectors.toList());
    }

    public OrderTableResponse changeEmpty(Long orderTableId, ChangeOrderTableEmptyRequest request) {
        OrderTable orderTable = findOrderTable(orderTableId);

        validateNotBelongToTableGroup(orderTable);
        validateTableCanChangeEmpty(orderTableId);

        orderTable.changeEmptyStatus(request.isEmpty());

        OrderTable savedOrderTable = orderTableRepository.save(orderTable);
        return new OrderTableResponse(savedOrderTable);
    }

    private void validateNotBelongToTableGroup(OrderTable orderTable) {
        if (Objects.nonNull(orderTable.getTableGroup())) {
            throw new IllegalArgumentException("이미 테이블 그룹이 형성된 테이블입니다.");
        }
    }

    private void validateTableCanChangeEmpty(Long orderTableId) {
        if (orderRepository.existsByOrderTableIdAndOrderStatusIn(
                orderTableId, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
            throw new IllegalArgumentException("비울 수 없는 테이블이 존재합니다.");
        }
    }

    public OrderTableResponse changeNumberOfGuests(Long orderTableId, ChangeNumOfTableGuestsRequest request) {
        OrderTable orderTable = findOrderTable(orderTableId);
        validateOrderTableIsNotEmpty(orderTable);

        orderTable.changeNumberOfGuests(request.getNumberOfGuests());

        OrderTable savedOrderTable = orderTableRepository.save(orderTable);
        return new OrderTableResponse(savedOrderTable);
    }

    private OrderTable findOrderTable(Long orderTableId) {
        return orderTableRepository.findById(orderTableId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테이블입니다."));
    }

    private void validateOrderTableIsNotEmpty(OrderTable orderTable) {
        if (orderTable.isEmpty()) {
            throw new IllegalArgumentException("주문 테이블이 비어있습니다.");
        }
    }
}

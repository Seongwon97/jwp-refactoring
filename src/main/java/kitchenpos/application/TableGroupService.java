package kitchenpos.application;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import kitchenpos.application.dto.request.TableGroupRequest;
import kitchenpos.application.dto.response.TableGroupResponse;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.TableGroup;
import kitchenpos.domain.TableGroupRepository;

@Service
@Transactional
public class TableGroupService {

    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;
    private final TableGroupRepository tableGroupRepository;

    public TableGroupService(OrderRepository orderRepository, OrderTableRepository orderTableRepository,
                             TableGroupRepository tableGroupRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
        this.tableGroupRepository = tableGroupRepository;
    }

    public TableGroupResponse create(TableGroupRequest request) {
        List<Long> orderTableIds = request.getOrderTableIds();
        validateOrderTableSize(orderTableIds);

        List<OrderTable> savedOrderTables = orderTableRepository.findAllByIdIn(orderTableIds);
        validateOrderTableSize(orderTableIds, savedOrderTables);
        validateOrderTableIsNotEmpty(savedOrderTables);

        TableGroup tableGroup = request.toTableGroup();
        TableGroup savedTableGroup = tableGroupRepository.save(tableGroup);
        groupOrderTables(savedOrderTables, savedTableGroup);

        return new TableGroupResponse(savedTableGroup);
    }

    private void validateOrderTableSize(List<Long> orderTableIds) {
        if (CollectionUtils.isEmpty(orderTableIds) || orderTableIds.size() < 2) {
            throw new IllegalArgumentException("테이블을 그룹화하려면 2개 이상의 테이블이 필요합니다.");
        }
    }

    private void groupOrderTables(List<OrderTable> savedOrderTables, TableGroup savedTableGroup) {
        Long tableGroupId = savedTableGroup.getId();
        for (OrderTable savedOrderTable : savedOrderTables) {
            savedOrderTable.joinTableGroup(tableGroupId);
            savedOrderTable.changeEmptyStatus(false);
            orderTableRepository.save(savedOrderTable);
        }
        savedTableGroup.addOrderTables(savedOrderTables);
    }

    private void validateOrderTableSize(List<Long> orderTableIds, List<OrderTable> savedOrderTables) {
        if (orderTableIds.size() != savedOrderTables.size()) {
            throw new IllegalArgumentException("존재하지 않는 주문 테이블이 있습니다.");
        }
    }

    private void validateOrderTableIsNotEmpty(List<OrderTable> savedOrderTables) {
        for (OrderTable savedOrderTable : savedOrderTables) {
            if (!savedOrderTable.isEmpty()) {
                throw new IllegalArgumentException("테이블이 비어있지 않습니다.");
            }
            if (Objects.nonNull(savedOrderTable.getTableGroup())) {
                throw new IllegalArgumentException("이미 테이블 그룹이 형성된 테이블입니다.");
            }
        }
    }

    public void ungroup(Long tableGroupId) {
        List<OrderTable> orderTables = orderTableRepository.findAllByTableGroupId(tableGroupId);

        List<Long> orderTableIds = getOrderTableIds(orderTables);
        validateIsPossibleToUngroup(orderTableIds);

        ungroupTables(orderTables);
    }

    private List<Long> getOrderTableIds(List<OrderTable> orderTables) {
        return orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());
    }

    private void validateIsPossibleToUngroup(List<Long> orderTableIds) {
        if (orderRepository.existsByOrderTableIdInAndOrderStatusIn(
                orderTableIds, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
            throw new IllegalArgumentException("그룹 해제를 할 수 없는 테이블이 존재합니다.");
        }
    }

    private void ungroupTables(List<OrderTable> orderTables) {
        for (OrderTable orderTable : orderTables) {
            orderTable.leaveTableGroup();
            orderTable.changeEmptyStatus(false);
            orderTableRepository.save(orderTable);
        }
    }
}

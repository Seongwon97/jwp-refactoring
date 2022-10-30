package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.application.dto.request.OrderTableIdRequest;
import kitchenpos.application.dto.request.TableGroupRequest;
import kitchenpos.application.dto.response.TableGroupResponse;
import kitchenpos.support.SpringBootNestedTest;

@Transactional
@SpringBootTest
class TableGroupServiceTest {

    @Autowired
    private TableGroupService tableGroupService;

    @Autowired
    private OrderTableDao orderTableDao;

    @Autowired
    private OrderDao orderDao;

    OrderTableIdRequest tableRequest1;
    OrderTableIdRequest tableRequest2;

    @BeforeEach
    void setUp() {
        OrderTable newTable1 = new OrderTable(3, true);
        OrderTable table1 = orderTableDao.save(newTable1);
        tableRequest1 = new OrderTableIdRequest(table1.getId());

        OrderTable newTable2 = new OrderTable(3, true);
        OrderTable table2 = orderTableDao.save(newTable2);
        tableRequest2 = new OrderTableIdRequest(table2.getId());
    }

    @DisplayName("단체 테이블을 만든다")
    @SpringBootNestedTest
    class CreateTest {

        @DisplayName("단체 테이블을 생성하면 ID를 할당된 TableGroup객체가 반환된다")
        @Test
        void create() {
            TableGroupRequest request = new TableGroupRequest(List.of(tableRequest1, tableRequest2));
            TableGroupResponse actual = tableGroupService.create(request);

            assertThat(actual).isNotNull();
        }

        @DisplayName("테이블이 없을 경우 예외가 발생한다")
        @Test
        void throwExceptionBecauseOfNoTable() {
            TableGroupRequest request = new TableGroupRequest(Collections.emptyList());
            assertThatThrownBy(() -> tableGroupService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("테이블을 그룹화하려면 2개 이상의 테이블이 필요합니다.");
        }

        @DisplayName("테이블이 2개 보다 작을 경우 예외가 발생한다")
        @Test
        void throwExceptionBecauseOfInvalidNumOfTables() {
            TableGroupRequest request = new TableGroupRequest(List.of(tableRequest1));
            assertThatThrownBy(() -> tableGroupService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("테이블을 그룹화하려면 2개 이상의 테이블이 필요합니다.");
        }

        @DisplayName("존재하지 않는 테이블이 존재할 경우 예외가 발생한다")
        @Test
        void throwExceptionBecauseOfNotExistTable() {
            OrderTableIdRequest notExistTableId = new OrderTableIdRequest(0L);

            TableGroupRequest tableGroup = new TableGroupRequest(List.of(tableRequest1, notExistTableId));
            assertThatThrownBy(() -> tableGroupService.create(tableGroup))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("존재하지 않는 주문 테이블이 있습니다.");
        }

        @DisplayName("비어있지 않은 테이블이 존재하는 경우 예외가 발생한다")
        @Test
        void throwExceptionBecauseOfEmptyTable() {
            OrderTable newEmptyTable = new OrderTable(1, false);
            orderTableDao.save(newEmptyTable);
            OrderTableIdRequest notEmptyTableId = new OrderTableIdRequest(newEmptyTable.getId());

            TableGroupRequest tableGroup = new TableGroupRequest(List.of(tableRequest1, notEmptyTableId));
            assertThatThrownBy(() -> tableGroupService.create(tableGroup))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("테이블이 비어있지 않습니다.");
        }

        @DisplayName("이미 단체로 묶인 테이블이 있을 경우 예외가 발생한다")
        @Test
        void throwExceptionBecauseOfAlreadyGroupedTable() {
            TableGroupRequest request = new TableGroupRequest(List.of(tableRequest1, tableRequest2));
            tableGroupService.create(request);

            assertThatThrownBy(() -> tableGroupService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("이미 테이블 그룹이 형성된 테이블입니다.");
        }
    }

    @DisplayName("단체 테이블을 분리한다")
    @SpringBootNestedTest
    class Ungroup {

        TableGroupResponse tableGroup;

        @BeforeEach
        void setUp() {
            TableGroupRequest request = new TableGroupRequest(List.of(tableRequest1, tableRequest2));
            tableGroup = tableGroupService.create(request);
        }

        @DisplayName("단체 테이블을 정상적으로 분리한다")
        @Test
        void ungroup() {
            assertDoesNotThrow(() -> tableGroupService.ungroup(tableGroup.getId()));
        }

        @DisplayName("테이블 중 주문 상태가 Cooking, Meal인 주문이 있을 경우 예외가 발생한다")
        @Test
        void throwExceptionBecauseOrderStatusIsCookingOrMeal() {
            Order order = Order.create(tableRequest1.getId(), List.of(new OrderLineItem(1L, 3)));
            order.changeStatus(OrderStatus.COOKING);
            orderDao.save(order);

            assertThatThrownBy(() -> tableGroupService.ungroup(tableGroup.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("그룹 해제를 할 수 없는 테이블이 존재합니다.");
        }
    }
}

# 키친포스

## 요구 사항

### 테이블

- 테이블을 생성할 수 있다.
- 테이블 목록을 조회할 수 있다.
- 빈 테이블 설정 및 해지를 할 수 있다.
  - 테이블 그룹에 속한 테이블은 변경할 수 없다.
  - 주문 상태가 'COOKING' 혹은 'MEAL'인 경우 변경할 수 없다
- 손님의 수를 입력 할 수 있다.
  - 손님의 수는 0 이상이다.
  - 빈 테이블은 손님의 수를 입력 할 수 없다.

### 테이블 그룹

- 2개 이상의 테이블을 그룹으로 지정할 수 있다.
  - 하나의 테이블은 하나의 그룹만을 가진다.
  - 빈 테이블만 그룹 지정이 가능하다.
- 그룹 지정을 해제 할 수 있다.
  - 주문 상태가 'COOKING' 혹은 'MEAL'인 경우 해제할 수 없다.

### 상품

- 0원 이상의 가격을 지정해 상품을 등록할 수 있다.
- 상품 목록을 조회 할 수 있다.

### 메뉴

- 0원 이상의 가격으로 메뉴를 생성할 수 있다.
  - 메뉴의 가격은 메뉴에 포함된 상품 금액들의 합보다 작거나 같아야한다.
- 메뉴 목록을 조회할 수 있다.

### 메뉴 그룹

- 메뉴 그룹을 생성할 수 있다.
- 메뉴 그룹 목록을 조회할 수 있다.

### 주문

- 주문을 생성할 수 있다.
  - 빈 테이블에는 주문할 수 없다.

- 주문 목록을 조회 할 수 있다.

- 주문 상태를 변경할 수 있다.
  - 완료된 주문은 상태를 변경할 수 없다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 상품 | product | 메뉴를 관리하는 기준이 되는 데이터 |
| 메뉴 그룹 | menu group | 메뉴 묶음, 분류 |
| 메뉴 | menu | 메뉴 그룹에 속하는 실제 주문 가능 단위 |
| 메뉴 상품 | menu product | 메뉴에 속하는 수량이 있는 상품 |
| 금액 | amount | 가격 * 수량 |
| 주문 테이블 | order table | 매장에서 주문이 발생하는 영역 |
| 빈 테이블 | empty table | 주문을 등록할 수 없는 주문 테이블 |
| 주문 | order | 매장에서 발생하는 주문 |
| 주문 상태 | order status | 주문은 조리 ➜ 식사 ➜ 계산 완료 순서로 진행된다. |
| 방문한 손님 수 | number of guests | 필수 사항은 아니며 주문은 0명으로 등록할 수 있다. |
| 단체 지정 | table group | 통합 계산을 위해 개별 주문 테이블을 그룹화하는 기능 |
| 주문 항목 | order line item | 주문에 속하는 수량이 있는 메뉴 |
| 매장 식사 | eat in | 포장하지 않고 매장에서 식사하는 것 |

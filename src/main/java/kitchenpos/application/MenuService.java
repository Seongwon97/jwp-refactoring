package kitchenpos.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.application.dto.request.MenuProductRequest;
import kitchenpos.application.dto.request.MenuRequest;
import kitchenpos.application.dto.response.MenuResponse;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.repository.MenuRepository;

@Service
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final ProductRepository productRepository;

    public MenuService(MenuRepository menuRepository, MenuGroupRepository menuGroupRepository, ProductRepository productRepository) {
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.productRepository = productRepository;
    }

    public MenuResponse create(MenuRequest request) {
        validateIsExistMenuGroup(request.getMenuGroupId());

        List<Long> menuProductIds = request.getMenuProducts()
                .stream()
                .map(MenuProductRequest::getProductId)
                .collect(Collectors.toList());

        List<Product> products = findProducts(menuProductIds);

        Menu menu = request.toMenu();

        return new MenuResponse(menuRepository.save(menu));
    }

    private List<Product> findProducts(List<Long> menuProductIds) {
        return menuProductIds.stream()
                .map(this::findProduct)
                .collect(Collectors.toList());
    }

    private Product findProduct(Long menuProductId) {
        return productRepository.findById(menuProductId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
    }
    /*
  - 메뉴 가격(price)는 null이 아닌 0이상의 값이어야 한다.
  - 기존에 존재하는 메뉴 상품여야 한다. ✅
  - 상품들은 기존에 존재하는 데이터여야 한다. ✅
  - 메뉴 가격은 메뉴를 형성하고 있는 상품들 가격의 합보다 적어야 한다.
     */

    private void validateIsExistMenuGroup(Long menuGroupId) {
        if (!menuGroupRepository.existsById(menuGroupId)) {
            throw new IllegalArgumentException("메뉴 그룹이 존재하지 않습니다.");
        }
    }

    private void validateProductAndPrice(Menu menu) {
        BigDecimal sum = BigDecimal.ZERO;
        for (MenuProduct menuProduct : menu.getMenuProducts()) {
            sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));
        }

        menu.validatePriceIsCheaperThanSum(sum);
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> list() {
        List<Menu> menus = menuRepository.findAll();
        return menus.stream()
                .map(MenuResponse::new)
                .collect(Collectors.toList());
    }
}

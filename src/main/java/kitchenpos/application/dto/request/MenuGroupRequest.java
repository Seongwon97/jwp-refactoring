package kitchenpos.application.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import kitchenpos.domain.MenuGroup;

public class MenuGroupRequest {

    private final String name;

    @JsonCreator
    public MenuGroupRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

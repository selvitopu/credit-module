

package com.ing.credit_module.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ing.credit_module.authentication.Tokens;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RefreshTokenDTO {

    @NotNull
    @JsonProperty("token")
    private Tokens tokens;

    public RefreshTokenDTO() {
    }

    public RefreshTokenDTO(@NotEmpty @NotNull Tokens tokens) {
        this.tokens = tokens;
    }

    public void setTokens(Tokens tokens) {
        this.tokens = tokens;
    }
}

package ro.deloittedigital.samekh.usermanagement.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class LoginResponse {
    @ApiModelProperty(example = "header.payload.signature")
    private String jwt;
}

package ro.deloittedigital.samekh.usermanagement.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GetShortProfileResponse {
    @ApiModelProperty(example = "Jane")
    protected String firstName;

    @ApiModelProperty(example = "Doe")
    protected String lastName;
}

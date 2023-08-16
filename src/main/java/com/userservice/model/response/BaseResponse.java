package com.userservice.model.response;

import com.userservice.model.response.mapper.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Component
public class BaseResponse {

    private ResponseStatus status;
    private String errorMessage;

}

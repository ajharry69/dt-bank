package com.github.ajharry69.customer.models.mappers;

import com.github.ajharry69.customer.service.account.dtos.CreateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {
    CreateAccountRequest toCreateAccountRequest(com.github.ajharry69.customer.models.CreateAccountRequest request);
}

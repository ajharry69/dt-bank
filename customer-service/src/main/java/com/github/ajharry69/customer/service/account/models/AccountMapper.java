package com.github.ajharry69.customer.service.account.models;

import com.github.ajharry69.customer.service.account.models.dtos.CreateAccountRequest;
import com.github.ajharry69.customer.service.account.models.dtos.CreateCustomerAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {
    CreateCustomerAccountRequest toCreateAccountRequest(CreateAccountRequest request);
}

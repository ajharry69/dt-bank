package com.github.ajharry69.account.service.account.models;

import com.github.ajharry69.account.service.account.models.dtos.AccountRequest;
import com.github.ajharry69.account.service.account.models.dtos.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {
    @Mapping(target = "id", ignore = true)
    Account toEntity(AccountRequest request);

    AccountResponse toResponse(Account account);
}

package com.github.ajharry69.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class OAuth2JwtAuthenticationTokenConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final Logger log = LoggerFactory.getLogger(OAuth2JwtAuthenticationTokenConverter.class);
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    @NonNull
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        log.debug("Extracting JWT authorities...");
        Collection<GrantedAuthority> scopeAuthorities = jwtGrantedAuthoritiesConverter.convert(jwt);

        Collection<GrantedAuthority> roleAuthorities = extractRoles(jwt);

        Collection<GrantedAuthority> authorities = Stream.concat(
                scopeAuthorities.stream(),
                roleAuthorities.stream()
        ).collect(Collectors.toSet());

        String principalName = jwt.getSubject();
        log.debug("Extracted JWT authorities for principal '{}': {}", principalName, authorities);

        return new JwtAuthenticationToken(jwt, authorities, principalName);
    }

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        var realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
            return roles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }
}

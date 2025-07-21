# Ref: https://www.keycloak.org/server/containers#_writing_your_optimized_keycloak_containerfile
ARG TAG=26.2
FROM quay.io/keycloak/keycloak:$TAG AS builder

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
ENV KC_FEATURES=preview,docker
ENV KC_DB=postgres

WORKDIR /opt/keycloak

COPY server.crt.pem server.key.pem /opt/keycloak/conf/

RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:$TAG
LABEL org.opencontainers.image.source="https://github.com/ajharry69/dt-bank"
LABEL org.opencontainers.image.licenses="Apache-2.0"

COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENV KC_DB=postgres
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
#!/usr/bin/env bash
set -Eeuo pipefail

psql \
  --username "${POSTGRES_USER}" \
  --dbname "${POSTGRES_DB}" \
  --set=migration_user="${DB_MIGRATION_USERNAME}" \
  --set=migration_password="${DB_MIGRATION_PASSWORD}" \
  --set=app_user="${DB_USERNAME}" \
  --set=app_password="${DB_PASSWORD}" <<'EOSQL'
SELECT format(
    'CREATE ROLE %I WITH LOGIN PASSWORD %L',
    :'migration_user',
    :'migration_password'
) \gexec

SELECT format(
    'CREATE ROLE %I WITH LOGIN PASSWORD %L',
    :'app_user',
    :'app_password'
) \gexec

SELECT format(
    'ALTER DATABASE %I OWNER TO %I',
    current_database(),
    :'migration_user'
) \gexec

SELECT format(
    'ALTER SCHEMA public OWNER TO %I',
    :'migration_user'
) \gexec

REVOKE CREATE ON SCHEMA public FROM PUBLIC;

SELECT format(
    'GRANT CONNECT ON DATABASE %I TO %I, %I',
    current_database(),
    :'migration_user',
    :'app_user'
) \gexec

SELECT format(
    'GRANT USAGE ON SCHEMA public TO %I',
    :'app_user'
) \gexec
EOSQL

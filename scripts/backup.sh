#!/usr/bin/env sh
set -eu

cd "$(dirname "$0")/.."
mkdir -p backups

timestamp="$(date +%Y%m%d-%H%M%S)"
output="backups/devflow-${timestamp}.sql.gz"

docker compose exec -T mysql sh -c \
    'exec mysqldump --single-transaction -uroot -p"$MYSQL_ROOT_PASSWORD" "$MYSQL_DATABASE"' \
    | gzip > "$output"

echo "Backup written to $output"

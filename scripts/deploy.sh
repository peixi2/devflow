#!/usr/bin/env sh
set -eu

cd "$(dirname "$0")/.."

if [ ! -f .env ]; then
    echo "Missing .env. Copy .env.example to .env and fill in production values." >&2
    exit 1
fi

docker compose config --quiet
docker compose up -d --build --remove-orphans
docker compose ps

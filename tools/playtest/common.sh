# Shared settings, sourced by the playtest scripts.
set -euo pipefail
ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)
RUN=$ROOT/run-test
STATE=$RUN/.playtest
NAME=ClaudeTester
INSTANCE=ardan-test
PORT=25566
RCON_PORT=25576
export DISPLAY=:99
mkdir -p "$STATE"

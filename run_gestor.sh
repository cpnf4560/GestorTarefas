#!/usr/bin/env bash
# Lightweight launcher for GestorTarefas
# Usage: ./run_gestor.sh [--full|--backend-only|--gui-only] [--no-build]
# Creates logs: backend.log and gui.log in the repo root.

set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

MODE="full"
DO_BUILD=true

print_help() {
  cat <<EOF
Usage: $0 [--full|--backend-only|--gui-only] [--no-build] [--help]

Options:
  --full         Start backend then GUI (default)
  --backend-only Start backend only
  --gui-only     Start GUI only (assumes backend already running)
  --no-build     Skip 'mvn package' before starting backend
  --help         Show this help and exit

Logs:
  backend.log    Backend stdout/stderr
  gui.log        GUI stdout/stderr

Examples:
  $0 --full             # build, start backend, then GUI
  $0 --gui-only --no-build  # start GUI only
EOF
}

# Parse args
while [[ $# -gt 0 ]]; do
  case "$1" in
    --full) MODE="full"; shift;;
    --backend-only) MODE="backend-only"; shift;;
    --gui-only) MODE="gui-only"; shift;;
    --no-build) DO_BUILD=false; shift;;
    --help) print_help; exit 0;;
    *) echo "Unknown arg: $1"; print_help; exit 2;;
  esac
done

# Helper: wait for http endpoint
wait_for() {
  local url="$1"; local retries=${2:-30}; local delay=${3:-1}
  echo "Waiting for $url ..."
  for i in $(seq 1 "$retries"); do
    if curl -sSf --connect-timeout 2 "$url" >/dev/null 2>&1; then
      echo "OK: $url"
      return 0
    fi
    sleep "$delay"
  done
  echo "Timeout waiting for $url"
  return 1
}

# Build (optional)
if [ "$DO_BUILD" = true ]; then
  echo "Running mvn -DskipTests=true package..."
  mvn -DskipTests=true package
fi

start_backend() {
  echo "Starting backend... logs -> backend.log"
  nohup ./start_backend.sh > backend.log 2>&1 &
  sleep 1
  # Wait for actuator health
  if wait_for "http://localhost:8080/actuator/health" 30 1; then
    echo "Backend ready"
  else
    echo "Backend failed to start (check backend.log)"
    return 1
  fi
}

start_gui() {
  echo "Starting GUI... logs -> gui.log"
  nohup ./iniciar_gui.sh > gui.log 2>&1 &
  # GUI script itself checks backend availability; give a little time
  sleep 1
  echo "GUI started (check gui.log for details)."
}

case "$MODE" in
  full)
    start_backend || exit 1
    start_gui
    ;;
  backend-only)
    start_backend || exit 1
    ;;
  gui-only)
    start_gui
    ;;
esac

echo "Launcher finished. Tail logs with: tail -f backend.log gui.log"

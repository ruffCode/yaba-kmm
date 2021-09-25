#!/bin/bash
set -e
#V_PROP="../version.properties"
#CURRENT_VERSION=$(grep -w version $V_PROP | cut -d '=' -f 2 | tr -d '\n')
RELEASE_URL="https://github.com/ruffCode/yaba-kmm/releases/tag/%s"
since=$(source scripts/tags.sh; previous_tag)
prev=$(git describe --tags 2>/dev/null || git rev-parse --short HEAD)
function release() {
    github_changelog_generator -u ruffcode -p yaba-kmm -t "$GITHUB_CHANGELOG_API_KEY" \
  --no-pr-wo-labels \
  --no-author \
  --no-issues \
  --enhancement-label "**Enhancements:**" \
  --bugs-label "**Bug fixes:**" \
  --release-url $RELEASE_URL \
  --exclude-labels documentation \
  --o RELEASE_CHANGELOG.md \
  --since-tag "$since"
}


function changelog() {
    github_changelog_generator -u ruffcode -p yaba-kmm -t "$GITHUB_CHANGELOG_API_KEY" \
  --no-pr-wo-labels \
  --no-author \
  --enhancement-label "**Enhancements:**" \
  --bugs-label "**Bug fixes:**" \
  --release-url $RELEASE_URL \
  -o CHANGELOG.md
}


case $1 in
    "-r")
        release
        ;;
    *)
        changelog
        ;;
esac


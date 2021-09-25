SHELL := /bin/bash -o pipefail

.PHONY: changelog
changelog:
	@$(shell pwd)/scripts/changelog.sh

.PHONY: changelog-release
changelog-release:
	@$(shell pwd)/scripts/changelog.sh -r

B="$(find $TRAVIS_BUILD_DIR/target -maxdepth 1 -type f -name 'Connect-Four-Battle-of-AI-Core-v*-release.zip' | cut -d'_' -f7)"
export GIT_TAG=$TRAVIS_BRANCH-$B
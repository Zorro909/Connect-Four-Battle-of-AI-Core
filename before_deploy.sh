B="$(ls Connect-Four-Battle-of-AI-Core-v*-release.zip | cut -d'_' -f7)"
export GIT_TAG=$TRAVIS_BRANCH-$B
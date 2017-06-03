IFS=';' read -ra ADDR <<< $(ls $TRAVIS_BUILD_DIR/target/Connect-Four-Battle-of-AI-Core*)
for i in "${ADDR[@]}"; do
    if [[$i == v*]] ;
    then
        export GIT_TAG=$TRAVIS_BRANCH-$i
    fi 
done
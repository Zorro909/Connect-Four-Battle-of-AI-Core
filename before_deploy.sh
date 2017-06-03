IFS=';' read -ra ADDR <<< (ls target\Connect-Four-Battle-of-AI-Core*)
for i in "${ADDR[@]}"; do
    if [[$i == v*]] ;
    then
        export GIT_TAG=$TRAVIS_BRANCH-$i
    fi 
done
if [[ "$(docker images -q tonlabs/local-node 2> /dev/null)" == "" ]]; then
    docker run -d --name local-node -p80:80 tonlabs/local-node
elif ! docker ps | grep -q tonlabs/local-node; then
    docker start local-node
fi

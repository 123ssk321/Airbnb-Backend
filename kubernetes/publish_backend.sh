#!/usr/bin/env sh

cp -f ../target/scc2324-project-1.0.war ./backend/
# Docker from kubernetes folder
docker build -t sskumar777/scc2324-app backend
docker push sskumar777/scc2324-app

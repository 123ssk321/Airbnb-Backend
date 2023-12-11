#!/usr/bin/env sh


az group create --name scc2324-cluster-57449 --location westeurope
out=$(az ad sp create-for-rbac --name http://scc2324-kuber-57449 --role Contributor --scope /subscriptions/91d97e86-bce1-4db8-8a75-2233dfb8a83a)
appId=$(echo $out | python3 -c "import sys, json; print(json.load(sys.stdin)['appId'])")
password=$(echo $out | python3 -c "import sys, json; print(json.load(sys.stdin)['password'])")
az aks create --resource-group scc2324-cluster-57449 --name my-scc2324-cluster-57449 --node-vm-size Standard_B2s --generate-ssh-keys --node-count 2 --service-principal $appId --client-secret $password
az aks get-credentials --resource-group scc2324-cluster-57449 --name my-scc2324-cluster-57449

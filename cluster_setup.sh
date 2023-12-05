az group create --name scc2324-cluster-57449 --location westeurope
az ad sp create-for-rbac --name http://scc2324-kuber-57449 --role Contributor --scope /subscriptions/91d97e86-bce1-4db8-8a75-2233dfb8a83a
az aks create --resource-group scc2324-cluster-57449 --name my-scc2324-cluster-57449 --node-vm-size Standard_B2s --generate-ssh-keys --node-count 2 --service-principal 0b28d2bd-ce21-4a69-ac23-c2149ea5fb49 --client-secret Gpr8Q~VQNQKTu5a8ixRo6-I3b81A_XSk6iWZSc9l
az aks get-credentials --resource-group scc2324-cluster-57449 --name my-scc2324-cluster-57449



#!/usr/bin/env sh

# Change these four parameters as needed
#ACI_RESOURCE_GROUP=scc24-rg-japanwest-57449
#ACI_STORAGE_ACCOUNT_NAME=mystorageaccount57449
#ACI_LOCATION=japanwest
#ACI_SHARE_NAME=acishare
#
## Get storage key
## STORAGE_KEY=$(az storage account keys list --resource-group $ACI_RESOURCE_GROUP --account-name $ACI_STORAGE_ACCOUNT_NAME --query "[0].value" --output tsv)
#
## Upload updated workload-light.yml
#az storage file upload --account-name $ACI_STORAGE_ACCOUNT_NAME --share-name $ACI_SHARE_NAME --source "workload-light.yml" --path "artillery/workload-light.yml"

#docker login azure
#docker context create aci myacicontext
#docker context use myacicontext
#docker run --rm -it -v %cd%/artillery:/artillery artilleryio/artillery:latest run /artillery/workload-light.yml
#docker logs $(container_name)

docker build -t sskumar777/scc2324-app-artillery .


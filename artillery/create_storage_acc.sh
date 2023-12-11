#!/usr/bin/env sh

# Change these four parameters as needed
ACI_RESOURCE_GROUP=scc24-rg-japanwest-57449
ACI_STORAGE_ACCOUNT_NAME=mystorageaccount57449
ACI_LOCATION=japanwest
ACI_SHARE_NAME=acishare

# Create the storage account with the parameters
az storage account create --resource-group $ACI_RESOURCE_GROUP --name $ACI_STORAGE_ACCOUNT_NAME --location $ACI_LOCATION --sku Standard_LRS

# Create the file share
az storage share create --name $ACI_SHARE_NAME --account-name $ACI_STORAGE_ACCOUNT_NAME

# Get storage key
STORAGE_KEY=$(az storage account keys list --resource-group $ACI_RESOURCE_GROUP --account-name $ACI_STORAGE_ACCOUNT_NAME --query "[0].value" --output tsv)

# Create a directory
az storage directory create --account-name $ACI_STORAGE_ACCOUNT_NAME --share-name $ACI_SHARE_NAME --name "artillery"

# Upload artillery folder
az storage file upload-batch --destination $ACI_SHARE_NAME --source . --account-name $ACI_STORAGE_ACCOUNT_NAME --account-key $STORAGE_KEY --destination-path artillery

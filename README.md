# Airbnb-Backend
This project consists in the design and implementation of the backend for a house rental company like Airbnb using Microsoft Azure. The project is done on behalf of the Cloud Computing Systems course.  
## Group members  
| Name        | Number | email                      |
|-------------|--------|----------------------------|
| Sahil Kumar | 57449  | ss.kumar@campus.fct.unl.pt |
| Bruno Carmo | 57418  | bm.carmo@campus.fct.unl.pt |

## Deploy Web App
```console
$ mvn clean compile package azure-webapp:deploy
```
#### URL: https://scc24appwesteurope57418.azurewebsites.net/rest

## Deploy Azure Functions

Unzip Azure-Functions project in another folder and run:
```console
$ cd Azure-Functions
$ mvn clean compile package azure-functions:deploy
```

#### URL: https://scc24funwesteurope57418.azurewebsites.net

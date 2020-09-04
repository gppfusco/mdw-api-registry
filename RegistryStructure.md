# Registry structure

### File system structure

A registry of APIs has the following structure on file system.
```shell script
base_dir_path
    |--> env_dir_name
        |--> repository_dir_name
            |--> <api_local_path_1>
                |--> <api_name_1>.json
                |--> <api_name_1>_dependencies.json
                |--> wadl
                    |--> <api_1_wadl>.wadl
                |--> wsdl
                    |--> <api_1_wsdl>.wsdl
                |--> xsd
                    |--> <api_1_xsd_1>.json
                    |--> <api_1_xsd_2>.json
                        .........
                    |--> <api_1_xsd_n>.json
            |--> <api_local_path_2>
                |--> <api_name_2>.json
                |--> <api_name_2>_dependencies.json
                |--> wadl
                    |--> <api_2_wadl>.wadl
                |--> wsdl
                    |--> <api_2_wsdl>.wsdl
                |--> xsd
                    |--> <api_2_xsd_1>.json
                    |--> <api_2_xsd_2>.json
                        .........
                    |--> <api_2_xsd_n>.json
                .........
            |--> <api_local_path_n>
                |--> <api_name_n>.json
                |--> <api_name_n>_dependencies.json
                |--> wadl
                    |--> <api_n_wadl>.wadl
                |--> wsdl
                    |--> <api_n_wsdl>.wsdl
                |--> xsd
                    |--> <api_n_xsd_1>.json
                    |--> <api_n_xsd_2>.json
                        .........
                    |--> <api_n_xsd_n>.json
        |--> <env_name>.json
```

### Registry documentation

A registry itself is stored inside a file named `<env_name>.json`. The json file has the following structure:
```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "type": "object",
  "properties": {
    "environment": {
      "type": "object",
      "properties": {
        "referenceName": {
          "type": "string"
        },
        "baseUrl": {
          "type": "string"
        },
        "envDir": {
          "type": "string"
        },
        "registry": {
          "type": "object",
          "properties": {
            "apis": {
              "type": "array",
              "items": [
                {
                  "type": "object",
                  "properties": {
                    "type": {
                      "type": "string",
                      "enum": ["REST", "SOAP", "Messaging"]
                    },
                    "endpoint": {
                      "type": "string"
                    },
                    "path": {
                      "type": "string"
                    },
                    "name": {
                      "type": "string"
                    },
                    "localPath": {
                      "type": "string"
                    },
                    "authentication": {
                      "type": "string",
                      "enum": ["Undefined", "http:HttpBasicAuthenticationType"]
                    },
                    "apiSpecification": {
                      "type": "object",
                      "properties": {
                        "apiSpecEndpoint": {
                          "type": "string"
                        },
                        "xsdSchemas": {
                          "type": "array",
                          "items": [
                            {
                              "type": "object",
                              "properties": {
                                "xsdPath": {
                                  "type": "string"
                                },
                                "xsdNameSpace": {
                                  "type": "string"
                                }
                              },
                              "required": [
                                "xsdPath",
                                "xsdNameSpace"
                              ]
                            },
                            {
                              "type": "object",
                              "properties": {
                                "xsdPath": {
                                  "type": "string"
                                },
                                "xsdNameSpace": {
                                  "type": "string"
                                }
                              },
                              "required": [
                                "xsdPath",
                                "xsdNameSpace"
                              ]
                            }
                          ]
                        },
                        "soapApiSpec": {
                          "type": "object",
                          "properties": {
                            "apiSpecEndpoint": {
                              "type": "string"
                            },
                            "xsdSchemas": {
                              "type": "array",
                              "items": [
                                {
                                  "type": "object",
                                  "properties": {
                                    "xsdPath": {
                                      "type": "string"
                                    },
                                    "xsdNameSpace": {
                                      "type": "string"
                                    }
                                  },
                                  "required": [
                                    "xsdPath",
                                    "xsdNameSpace"
                                  ]
                                },
                                {
                                  "type": "object",
                                  "properties": {
                                    "xsdPath": {
                                      "type": "string"
                                    },
                                    "xsdNameSpace": {
                                      "type": "string"
                                    }
                                  },
                                  "required": [
                                    "xsdPath",
                                    "xsdNameSpace"
                                  ]
                                }
                              ]
                            }
                          },
                          "required": [
                            "apiSpecEndpoint",
                            "xsdSchemas"
                          ]
                        }
                      },
                      "required": [
                        "apiSpecEndpoint",
                        "xsdSchemas",
                        "soapApiSpec"
                      ]
                    },
                    "integrationScenario": {
                      "type": "string",
                      "enum": ["NOT_DEFINED", "PROXY", "DATA_MANIPULATION", "ORCHESTRATION"]
                    },
                    "compressionSupported": {
                      "type": "boolean"
                    },
                    "bufferingSupported": {
                      "type": "boolean"
                    }
                  },
                  "required": [
                    "type",
                    "endpoint",
                    "path",
                    "name",
                    "localPath",
                    "authentication",
                    "apiSpecification",
                    "integrationScenario",
                    "compressionSupported",
                    "bufferingSupported"
                  ]
                }
              ]
            }
          },
          "required": [
            "apis"
          ]
        }
      },
      "required": [
        "referenceName",
        "baseUrl",
        "envDir",
        "registry"
      ]
    }
  },
  "required": [
    "environment"
  ]
}
```

An example of registry is shown below:
```json
{
  "environment" : {
    "referenceName" : "OSB_IT",
    "baseUrl" : "https://servizicollaudo.sky.it/osbprj",
    "envDir" : "osb_it",
    "registry" : {
      "apis" : [ {
        "type" : "REST",
        "endpoint" : "https://servizicollaudo.sky.it/osbprj",
        "path" : "/services/salesforce/rest/orders",
        "name" : "SalesforceProject_REST_CRM_ORDER_HANDLER_proxy_PS_REST_CRM_ORDER_HANDLER",
        "localPath" : "osb_it\\repo\\SalesforceProject\\REST\\CRM\\ORDER\\HANDLER\\PS_REST_CRM_ORDER_HANDLER",
        "authentication" : "http:HttpBasicAuthenticationType",
        "apiSpecification" : {
          "apiSpecEndpoint" : "https://servizicollaudo.sky.it/osbprj/services/salesforce/rest/orders?WADL",
          "xsdSchemas" : [ {
            "xsdPath" : "MDW_CDM/EnterpriseServices/SalesforceProject/Rest_OrderHandler/xsd/XSD_CRM_ORDER_HANDLER",
            "xsdNameSpace" : "http://www.skytv.it/mdw/data"
          }, {
            "xsdPath" : "MDW_CDM/EnterpriseObjects/CommonEntities/restErrors",
            "xsdNameSpace" : "http://www.skytv.it/mdw/data"
          } ],
          "soapApiSpec" : {
            "apiSpecEndpoint" : "https://servizicollaudo.sky.it/osbprj",
            "xsdSchemas" : [ {
              "xsdPath" : "/MDW_CDM/EnterpriseServices/SalesforceProject/Rest_OrderHandler/xsd/XSD_CRM_ORDER_HANDLER",
              "xsdNameSpace" : "http://www.skytv.it/mdw/data"
            }, {
              "xsdPath" : "/MDW_CDM/EnterpriseObjects/CommonEntities/restErrors",
              "xsdNameSpace" : "http://www.skytv.it/mdw/data"
            } ]
          }
        },
        "integrationScenario" : "DATA_MANIPULATION",
        "compressionSupported" : false,
        "bufferingSupported" : false
      }, {
        "type" : "SOAP",
        "endpoint" : "https://servizicollaudo.sky.it/osbprj",
        "path" : "/services/mdw/cpq/v2/WS_CPQ_SELLING_CONFIRMATION",
        "name" : "CPQProject_sellingConfirmation_v2_proxy_PS_WS_CPQ_SELLING_CONFIRMATION",
        "localPath" : "osb_it\\repo\\CPQProject\\sellingConfirmation\\v2\\PS_WS_CPQ_SELLING_CONFIRMATION",
        "authentication" : "http:HttpBasicAuthenticationType",
        "apiSpecification" : {
          "apiSpecEndpoint" : "https://servizicollaudo.sky.it/osbprj/services/mdw/cpq/v2/WS_CPQ_SELLING_CONFIRMATION?WSDL",
          "xsdSchemas" : [ {
            "xsdPath" : "/MDW_CDM/EnterpriseServices/CPQProject/SellingConfirmation_v2/xsd/XSD_CPQ_SELLING_CONFIRMATION",
            "xsdNameSpace" : "http://www.skytv.it/mdw/data"
          }, {
            "xsdPath" : "/MDW_CDM/EnterpriseObjects/CommonEntities/SoapHeaderSKY",
            "xsdNameSpace" : "http://www.skytv.it/mdw/data"
          } ]
        },
        "integrationScenario" : "DATA_MANIPULATION",
        "compressionSupported" : false,
        "bufferingSupported" : false
      }, {
        "type" : "SOAP",
        "endpoint" : "https://servizicollaudo.sky.it/osbprj",
        "path" : "/services/igfs/WS_IGFS_PAYMENT_GATEWAY_MUTUAL",
        "name" : "IGFSProject_PaymentGatewayMutual_proxy_PS_IGFS_PAYMENT_GATEWAY_MUTUAL",
        "localPath" : "osb_it\\repo\\IGFSProject\\PaymentGatewayMutual\\PS_IGFS_PAYMENT_GATEWAY_MUTUAL",
        "authentication" : "Undefined",
        "apiSpecification" : {
          "apiSpecEndpoint" : "https://servizicollaudo.sky.it/osbprj/services/igfs/WS_IGFS_PAYMENT_GATEWAY_MUTUAL?WSDL",
          "xsdSchemas" : [ {
            "xsdPath" : "/MDW_CDM/EnterpriseServices/IGFSProject/PaymentGateway/xsd/XSD_IGFS_PAYMENT_GATEWAY",
            "xsdNameSpace" : "http://www.skytv.it/mdw/data"
          }, {
            "xsdPath" : "/MDW_CDM/EnterpriseObjects/CommonEntities/SoapHeaderSKY",
            "xsdNameSpace" : "http://www.skytv.it/mdw/data"
          } ]
        },
        "integrationScenario" : "ORCHESTRATION",
        "compressionSupported" : false,
        "bufferingSupported" : false
      }
	  ]
    }
  }
}
```
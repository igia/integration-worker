#
# This Source Code Form is subject to the terms of the Mozilla Public License, v.
# 2.0 with a Healthcare Disclaimer.
# A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
# be found under the top level directory, named LICENSE.
# If a copy of the MPL was not distributed with this file, You can obtain one at
# http://mozilla.org/MPL/2.0/.
# If a copy of the Healthcare Disclaimer was not distributed with this file, You
# can obtain one at the project website https://github.com/igia.
#
# Copyright (C) 2018-2019 Persistent Systems, Inc.
#

import requests, json

token_url = "http://localhost:9080/auth/realms/igia/protocol/openid-connect/token"

API_url = "http://localhost:8050/api/data-pipelines"

client_id = 'internal'
client_secret = 'internal'

data = {'grant_type': 'client_credentials'}

access_token_response = requests.post(token_url, data=data, verify=False, allow_redirects=False, auth=(client_id, client_secret))

#print access_token_response.headers
#print access_token_response.text

tokens = json.loads(access_token_response.text)

print "access token: " + tokens['access_token']

#step B - with the returned access_token we can make as many calls as we want

data = {
    'name':'003_Deployed_Pipeline_with_FT',
    'description':'MLLP to FILE and HL7 to HL7',
    'deploy':'true',
    'workerService':'INTEGRATIONWORKER',
    'source':{
      'type':'MLLP',
      'name':'Source Hospital',
      'inDataType':'HL7_V2',
      'outDataType':'HL7_V2',
      'configurations':[
        {
          'key':'hostname',
          'value':'10.1.5.6'
        },
        {
          'key':'port',
          'value':'1282'
        }
      ],
      'filters':[
        {
          'order':0,
          'type':'JAVASCRIPT',
          'data':'var logger = org.slf4j.LoggerFactory.getLogger(exchange.getFromRouteId()); var msg = exchange.getIn().getBody(Java.type(\'ca.uhn.hl7v2.model.v26.message.ADT_A01\').class); var Mtype = msg.getMSH().getMessageType().encode(); logger.info (\'Source Filter Logging: {}\', Mtype); result=msg.getMSH().getMessageType().encode().equals(\'ADT^A04^ADT_A01\')',
          'description':'sample source filter'
        }
      ],
      'transformers':[

      ]
    },
    'destinations':[
      {
        'type':'FILE',
        'name':'Shared file',
        'inDataType':'HL7_V2',
        'outDataType':'HL7_V2',
        'configurations':[
          {
            'key':'fileName',
            'value':'dest_info_3.hl7'
          },
          {
            'key':'directoryName',
            'value':'received_files'
          }
        ],
        'filters':[

        ],
        'transformers':[
          {
            'order':0,
            'type':'JAVASCRIPT',
            'data':'var logger = org.slf4j.LoggerFactory.getLogger(exchange.getFromRouteId());var msg = exchange.getIn().getBody(Java.type(\'ca.uhn.hl7v2.model.v26.message.ADT_A01\').class); var pid = msg.getPID().getPatientIdentifierList(0).getIDNumber().getValueOrEmpty(); var pname = msg.getPID().getPatientName(0).getGivenName().getValueOrEmpty(); logger.info (\'Destination transformer logging: {}, {}\', pid, pname); var content = []; content.push(pid); content.push(pname); logger.info(\'Final content: {}\', content); result = content;',
            'description':'This script extracts Patient id and Patient name from HL7 V2 message.'
          }
        ]
      }
    ]
  }


api_call_headers = {'Authorization' : 'Bearer ' + tokens['access_token'], 
                    'Content-type': 'application/json',
                    'Accept': 'application/json'}
api_call_response = requests.post(API_url, data=json.dumps(data), headers=api_call_headers)

print "POST RESPONSE:-"+api_call_response.text

api_call_response = requests.get(API_url, headers=api_call_headers)
                    
print "GET RESPONSE:-"+api_call_response.text

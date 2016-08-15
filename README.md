# skype4jenkins 

####how to run
- mvn package  
- add "credentials" file near jar file (with credentials "<login>:<password>") TODO: will be rewritten
- add config.json near jar file (optionally config2.json for different jenkins host etc)
- run application:   java -Dconfig.file=config.json,config2.json -jar skype4jenkins-0.0.1-SNAPSHOT.jar

for debug: run as simple java RunNotification.java with -Dconfig.file=config.json,config2.json properties


Several jenkins hosts can be monitored - all jobs should be grouped by jenkins url
host1 supports jobs A  B
host2 supports jobs C and D
Consequently,  config file #1 has property  "jenkinsUrl": "host1" and description of jobs A and B
config file #2 has property  "jenkinsUrl": "host2" and description of jobs C and D

6 modes of notifiers are implemented:
- start
- success
- failure
- aborted
- stillRed
- backNormal


Example of config is attached below:
```
{
	"jenkinsUrl": "<jenkins host (with protocol and port)>", 
	"jobs": [
		{
			"info": {
				"name": "<custom thread name>",
				"jobName": "<job name>",
				"chatId": "<skype : /get name>"
			},
			"defaultParameters": [  //optional
				{
					"name": "CONFIG_FILE",
					"message": "Config file is %s"  //optional
				}
			],
			"notify": [ //each notifier type is optional
				{
					"type": "start",
					"message": "some buildStart text",
					"loneFromLog": "Started by", //optional
					Parameters": [  //optional
						{
							"name": "CONFIG_FILE",
							"message": "Config file is %s" //optional
						}
				},
				{
					"type": "failure",
					"once": true,
					"message": "some buildFailure text"
				},
				{
					"type": "success",
					"once": false,
					"message": "some buildSuccess text"
				},
				{
					"type": "aborted",
					"once": false,
					"message": "some buildAborted text",
					"lineFromLog": "Aborted by"
				},
				{
					"type": "stillRed",
					"message": "some buildStillRed text"
				},
				{
					"type": "backNormal",
					"message": "some buildBackNormal text"
				}
			]
		}
	]
}
```
# skype4jenkins 

mvn package  
add credentials file near jar file  
add config.json near jar file (optionally config2.json for different jenkins host etc)
run application:  
java -Dconfig.file=config.json,config2.json -jar skype4jenkins-0.0.1-SNAPSHOT.jar


- Several jenkins hosts can be monitored - all jobs should be grouped by jenkins url


Example of config is attached below:

    {
	"jenkinsUrl": "<jenkins host (with protocol and port)>",
	"jobs":[
		{
			"info":{
				"name": "<custom thread name>",
				"jobName": "<job name>",
				"chatId": "<skype : /get name>"

			},
			"defaultParameters": [
				{
					"name": "CONFIG_FILE",
					"message": "Config file is %s"
				}
			],
			"notify":[
			
			{
						"type": "start",
					"message" : "some buildStart text",
					"loneFromLog": "Started by"
				},
				{
					"type": "failure",
					"once": false,
					"message" : "some buildFailure text"
				},
				{
					"type": "success",
					"once": true,
					"message" : "some buildSuccess text"
				},
				{
					"type": "aborted",
					"once": false,
					"message" : "some buildAborted text",
					"lineFromLog": "Aborted by"
				},
				{
					"type": "stillRed",
					"message" : "some buildStillRed text"
				},
				{
					"type": "backNormal",
					"message" : "some buildBackNormal text"
				}
			
			]
		}
	]
}

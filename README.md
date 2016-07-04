# skype4jenkins 

mvn package  
add credentials file near jar file  
add config.json near jar file (optionally config2.json for different jenkins host etc)
run application:  
java -Dconfig.file=config.json, config2.json -Dbot.active=true -jar skype4jenkins-0.0.1-SNAPSHOT.jar


- Several jenkins hosts can be monitored - all jobs should be grouped by jenkins url
host1 supports jobs A  B
host2 supports jobs C and D
Consequently,  config file #1 has property  "jenkinsUrl": "host1" and description of jobs A and B
config file #2 has property  "jenkinsUrl": "host2" and description of jobs C and D

- 3 modes of notifiers are implemented:
statusOfEachBuild
buildStatusChanged
buildStillRed

Example of config is attached below:

    {
      "info": {
        "name": "Deploy-env",
        "jobName": "JOB NAME",
        "timeout": 20,
        "chatId": "/get name"
		},
      "defaultParameters": [
        {
          "name": "ENV",
          "message": "at %s Env"
        }
      ]
    ,
      "notify": [
        {
          "type": "statusOfEachBuild",
          "status": [
            {
              "type": "SUCCESS",
              "message": "(rock) - Deploy completed",
              "lineFromLog": "Started by user"
            }
          ]
        },
        {
          "type": "buildStatusChanged",
          "status": [
            {
              "type": "SUCCESS",
              "message": "Hoorray, Deploy finally completed",
              "lineFromLog": "Started by user"
            },
            {
              "type": "FAILURE",
              "message": "Oops, Deploy suddenly failed",
              "lineFromLog": "Started by user"
            }
          ]
        },
        {
          "type": "buildStillRed",
          "status": [
            {
              "type": "FAILURE",
              "message": "Oops, Deploy still failed",
              "lineFromLog": "Started by user"
            }
          ]
        }
      ]
    }
# Configuration as Code AWS SSM
Jenkins plugin for getting secrets from AWS parameter store when using Jenkins Configuration as Code plugin.

More information about AWS SSM:
https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-paramstore.html

More information about Jenkins Configuration as Code plugin:
https://github.com/jenkinsci/configuration-as-code-plugin


## Usage
Install plugin via Jenkins Update Center.

Make sure that Jenkins at least the following IAM permissions:

    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": "ssm:GetParameter",
                "Resource": "arn:aws:ssm:<region>:<account>:parameter/*"
            }
        ]
    }

Plugin will try to resolve secrets

    - credentials:
      - string:
        id: "cred-id"
        secret: ${filename}
    
from SSM with name _filename_. 

If a prefix is needed then configure environment variable _CASC_SSM_PREFIX_.
Example:
CASC_SSM_PREFIX=jenkins.master.

It will then resolve the example above with name _jenkins.master.filename_ from SSM.

Code has been contributed by Bambora


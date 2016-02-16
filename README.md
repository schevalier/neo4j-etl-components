# Neo Integration

Integration with MySQL.

## Integration Tests

The integration tests create a MySQL server either locally (using Vagrant) or in AWS. To run the tests locally, you'll need to install (Vagrant) [https://www.vagrantup.com/]. To run the tests in AWS, you'll need an AWS IAM user.

To run the tests using AWS:

`mvn -DPLATFORM=aws -DEC2_SSH_KEY=<name of your EC2 SSH key> clean compile verify`

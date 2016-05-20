# Neo Integration

Integration with MySQL.

## Integration Tests

You can run the tests with a local MySQL instance, [Vagrant] (https://www.vagrantup.com/), or in AWS.

You will need a mysql user neo with password neo with admin privileges to run the tests.

To run the tests in AWS, you'll need an AWS IAM user.

To run the tests using a local MySQL instance:

`mvn -DPLATFORM=local clean integration-test`

To run the tests using Vagrant:

`mvn -DPLATFORM=vagrant clean integration-test`

To run the tests using AWS:

Note: You need to create AWS Keypair and have the credentials file created to do this

`mvn -DPLATFORM=aws -DEC2_SSH_KEY=<name of your EC2 SSH key> clean integration-test`

If you don't supply a `PLATFORM` parameter, the tests will attempt to use Vagrant.

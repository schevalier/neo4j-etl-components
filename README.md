# Neo Integration

Integration with MySQL.

## Integration Tests

You can run the tests with a local MySQL instance, or using [Vagrant] (https://www.vagrantup.com/), or in AWS. To run the tests in AWS, you'll need an AWS IAM user.

To run the tests using AWS:

`mvn -DPLATFORM=aws -DEC2_SSH_KEY=<name of your EC2 SSH key> clean integration-test`

To run the tests using Vagrant:

`mvn -DPLATFORM=vagrant clean integration-test`

To run the tests using a local MySQL instance:

`mvn -DPLATFORM=local clean integration-test`

If you don't supply a `PLATFORM` parameter, the tests will attempt to use Vagrant.

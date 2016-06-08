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

## Performance Tests

Set of tests that are part of the `neo-integration-it` module.

However, they are skipped usually when you run the integration-test target by default. You can run them separtely as part of a test suite.

To run performance tests in AWS:

`mvn clean dependency:copy-dependencies integration-test -Dtest=PerformanceTestSuite -DfailIfNoTests=false -DPLATFORM=aws -DEC2_SSH_KEY=<name of your EC2 SSH key>`

To run performance tests in locally,

Ensure that you have the datasets imported in your local mysql instance. You could do this by uncommenting the bits in the code to download the sql file from S3.

`mvn clean dependency:copy-dependencies integration-test -Dtest=PerformanceTestSuite -DfailIfNoTests=false -DPLATFORM=local`

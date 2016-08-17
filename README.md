# Neo4j ETL Components

Integration with MySQL.

## Prerequisites

* [Maven] (https://maven.apache.org/)
* [MySql] (https://www.mysql.com/)

## Integration Tests

You can run the tests with a local MySQL instance, [Vagrant] (https://www.vagrantup.com/), or in AWS.

You will need a mysql user neo4j with password neo4j with admin privileges to run the tests.

To run the tests in AWS, you'll need an AWS IAM user.

To run the tests using a local MySQL instance:

Grant all privileges to the user _neo4j_ identified by password _neo4j_

`mvn -DPLATFORM=local clean integration-test`

To run the tests using Vagrant:

`mvn -DPLATFORM=vagrant clean integration-test`

To run the tests using AWS:

Note: You need to create AWS Keypair and have the credentials file created to do this

`mvn -DPLATFORM=aws -DEC2_SSH_KEY=<name of your EC2 SSH key> clean integration-test`

If you don't supply a `PLATFORM` parameter, the tests will attempt to use Vagrant.

## Performance Tests

Set of tests that are part of the `neo4j-etl-it` module.

However, they are skipped usually when you run the integration-test target by default. You can run them separately as part of a test suite.

To run performance tests in AWS:

`mvn clean dependency:copy-dependencies integration-test -Dtest=PerformanceTestSuite -DfailIfNoTests=false -DPLATFORM=aws -DEC2_SSH_KEY=<name of your EC2 SSH key>`

To run performance tests in locally,

Ensure that you have the datasets imported in your local mysql instance. You could do this by uncommenting the bits in the code to download the sql file from S3.

`mvn clean dependency:copy-dependencies integration-test -Dtest=PerformanceTestSuite -DfailIfNoTests=false -DPLATFORM=local`

## CLI Usage

To run the cli locally once you clone the project you'd have have install the neo4j-etl project locally,

From the _neo-etl-components_ run maven install

`mvn install -DPLATFORM=local`

_PLATFORM=local_ is supplied only to speed the install process that runs tests, you can choose to _skipTests_.

Once this is done, you can run maven package from _neo-etl-cli_ sub-project

`mvn package`

Once that has been successfully executed, the scripts are located in the _bin_ directory under _neo-etl-cli_

Examples of command usage,

```
./neo4j-etl mysql export  --host 127.0.0.1 --user neo4j --password neo4j \
--database javabase --destination /tmp/neo4j-enterprise-3.0.1/data/databases/graph.db/ \
--import-tool /tmp/neo4j-enterprise-3.0.1/bin  --csv-directory ./  \
--options-file ./import-tool-options.json --force --debug
```

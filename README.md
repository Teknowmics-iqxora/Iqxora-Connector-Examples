# Iqxora-Connector-Examples

In this connector example 

A platform end point to build API for connecting external resources

**List Of Connectors**
1. External Connectors : used to handle HTTP services and other things
2. Document Connectors : Office document management

**Getting Started**

**_Method 1: Using Template Model_**

To build an external connector in faster way is to use template model and add custom change on the template model.

Step 1 : Checkout the project to local directory and open project in your IDE.

Step 2 : To Execute connector task you can use default service Implementation class(ConnectorServiceImpl) or you can create your own custom Implementation class.

Step 3 : Extend abstract class, you have to extend abstract class (com.teknowmics.externalconnector.fw.service.Impl.AbstractConnectorPluginServiceImpl). if you are creating your own implementation class

Step 4 : Then need to implement abstract methods. 

The abstract methods are:

1. **getEndConnectorKey** - Used to identify your connector, have to provide connector key. That is used while in connector creation(sysAdmin)
2. **isAsync** - used to check the process is synchronised or not
3. **executeTask** - It will triggered when connector task is excuted and connector task key is matched with the connector key that provide in the getEndConnectorKey method. In this executeTask method you can provide your custom implementation for the external resources.

**Input and OutPut Managment**

To work Connector succesfully, have to manage the input and output response properly.
In method executorTask, _EndPointConnectorDetailsForm_ contain the input and outparams. you can use getter method to get input parameters and output response parameters.
I/O fiedls are:

```
private List<ExternalConnectorInputs> externalConnectorInputs;
private List<ExternalConnectorOutputDetails> externalConnectorOutputDetails;
```

For submitting response, Should follow the system accepting standards.
In Responce data the ExternalConnectorOutputDetails must be attached!

**_Method 2: Without Using Template Model_**

1. To use external connector service, have to build a java application and need to add the required dependency library.

step 1 : Create a java maven application in your IDE

step 2: Add Client library in your pom.xml file
```
<dependency>
<groupId>com.teknowmics.iqxora</groupId>
<artifactId>ExternalConnectorFramework</artifactId>
<version>1.0.0</version>
</dependency>
```

follow above mentioned steps, from step 2.


**Generate Responce Data**

To complete a task successfully you have to fill mandatory fields. The mandatory fields are
    1. UniqueJobId - It can get it from _EndPointConnectorDetailsForm_
    2. Status (SUCCESS/FAILED)

if there is any output details that should responded back to Iqxora then include that in ResponseData
```
    ResponseData responceData = new ResponseData();
    responceData.setUniqueJobId(endPointConnectorDetailsForm.getUniqueJobId());
    responceData.setOutputDetails(externalConnectorOutputDetails);
    responceData.setStatus("SUCCESS");
```

**Samples**

In this example, a connector created to add a record into an external database, using JDBC.

```
        try (Connection connection = DriverManager
                .getConnection(mysqlUrl, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SUPPLIER)) {
            connection.setAutoCommit(false);
            preparedStatement.setInt(1, 0);
            preparedStatement.setString(2, supplierName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, pin);
            preparedStatement.setString(5, supplierCode);
            preparedStatement.addBatch();
            int[] updateCounts = preparedStatement.executeBatch();
            //System.out.println(Arrays.toString(updateCounts));
            connection.commit();
            connection.setAutoCommit(true);
            return generateResponce(updateCounts, endPointConnectorDetailsForm);
        } catch (BatchUpdateException batchUpdateException) {
           LOGGER.error("Error while connecting external database from end connector ", batchUpdateException);
        } catch (SQLException e) {
           LOGGER.error("Error while connecting external database from end connector ", e);
        }
        
```


    






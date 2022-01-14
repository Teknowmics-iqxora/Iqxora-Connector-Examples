# Iqxora-Connector-Examples

In this Iqxora  example project demonstration how can make a connection to an external database using  connector template. Here we used JDBC to connect an sql server.


**Process Flow**
This External Database connector works using scheduler.

It contains a SupplierDetailsServiceImpl which is extends a  _AbstractConnectorPluginServiceImpl_ class.

In which we have executerTask method it gets triggerd by scheduler when connector task key is matched with the connector key that provide in the getEndConnectorKey method. 

In this executeTask method we added custom implementation for inserting values into database. 
On successful completion the responce will be updated back Iqxora.




    






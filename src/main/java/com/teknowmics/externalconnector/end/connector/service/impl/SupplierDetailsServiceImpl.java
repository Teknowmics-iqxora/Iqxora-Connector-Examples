/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teknowmics.externalconnector.end.connector.service.impl;

import com.teknowmics.externalconnector.fw.dto.ExternalConnectorInputs;
import com.teknowmics.externalconnector.fw.dto.ExternalConnectorOutputDetails;
import com.teknowmics.externalconnector.fw.forms.EndPointConnectorDetailsForm;
import com.teknowmics.externalconnector.fw.forms.ResponseData;
import com.teknowmics.externalconnector.fw.service.ConnectorPluginService;
import com.teknowmics.externalconnector.fw.service.Impl.AbstractConnectorPluginServiceImpl;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author saikrishnan
 */
@Service
public class SupplierDetailsServiceImpl extends AbstractConnectorPluginServiceImpl implements ConnectorPluginService {

    private static final Logger LOGGER = Logger.getLogger(SupplierDetailsServiceImpl.class);

    String INSERT_SUPPLIER = "INSERT INTO test.supplier(idsupplier,supplier_name,address,pin,suppliercode)VALUES"
            + "(?,?,?,?,?);";

    String DB_URL = "jdbc:mysql://<IP Address>/<Database name>";
    String USERNAME = "<user name>";
    String PASSWORD = "<password>";

    @Override
    protected ResponseData executeTask(EndPointConnectorDetailsForm endPointConnectorDetailsForm) {
        if (endPointConnectorDetailsForm.getActionType().equals("ADD_USER")) {
            return uploadData(endPointConnectorDetailsForm);
        } else {
            return uploadData(endPointConnectorDetailsForm);
        }

    }

    @Override
    protected Boolean isAsync() {
        return false;
    }

    @Override
    public String getEndConnectorKey() {
        return "ADD_USER_CONNECTOR";
    }

    private ResponseData uploadData(EndPointConnectorDetailsForm endPointConnectorDetailsForm) {
        String mysqlUrl = getDatabaseConnectionPath();
        List<ExternalConnectorInputs> inputs = endPointConnectorDetailsForm.getExternalConnectorInputs();
        String supplierName = "", address = "", pin = "", supplierCode = "";
        for (ExternalConnectorInputs input : inputs) {
            if (input.getName().equals("supplier_name")) {
                supplierName = input.getFieldValue();
            } else if (input.getName().equals("address")) {
                address = input.getFieldValue();
            } else if (input.getName().equals("pin")) {
                pin = input.getFieldValue();
            } else if (input.getName().equals("suppliercode")) {
                supplierCode = input.getFieldValue();
            }

        }

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
        return null;
    }

    private ResponseData generateResponce(int[] responce, EndPointConnectorDetailsForm endPointConnectorDetailsForm) {
        ResponseData responceData = new ResponseData();
        List<ExternalConnectorOutputDetails> externalConnectorOutputDetails = endPointConnectorDetailsForm.getExternalConnectorOutputDetails();
        for (ExternalConnectorOutputDetails externalConnectorOutputDetail : externalConnectorOutputDetails) {
            if (externalConnectorOutputDetail.getName().equals("status")) {
                externalConnectorOutputDetail.setVariableValue("SUCCESS");
            }
        }
        if (responce[0] >= 1) {
            responceData.setUniqueJobId(endPointConnectorDetailsForm.getUniqueJobId());
            responceData.setOutputDetails(externalConnectorOutputDetails);
            responceData.setStatus("SUCCESS");
            return responceData;
        } else {
            responceData.setUniqueJobId(endPointConnectorDetailsForm.getUniqueJobId());
            responceData.setStatus("FAIL");
            return responceData;
        }
    }

    private String getDatabaseConnectionPath() {
        return DB_URL;
    }

}

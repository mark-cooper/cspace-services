/**
 * This document is a part of the source code and related artifacts
 * for CollectionSpace, an open source collections management system
 * for museums and related institutions:
 *
 * http://www.collectionspace.org
 * http://wiki.collectionspace.org
 *
 * Copyright © 2009 Regents of the University of California
 *
 * Licensed under the Educational Community License (ECL), Version 2.0.
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the ECL 2.0 License at
 * https://source.collectionspace.org/collection-space/LICENSE.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 package org.collectionspace.services.client.test;

import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.collectionspace.services.client.IntakeClient;
import org.collectionspace.services.client.test.ServiceRequestType;
import org.collectionspace.services.intake.Intake;
import org.collectionspace.services.intake.IntakeList;

import org.jboss.resteasy.client.ClientResponse;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * IntakeServiceTest, carries out tests against a
 * deployed and running Intake Service.
 * 
 * $LastChangedRevision: 511 $
 * $LastChangedDate: 2009-08-06 20:16:16 +0000 (Thu, 06 Aug 2009) $
 */
public class IntakeServiceTest extends AbstractServiceTest {

    // Instance variables specific to this test.
    private IntakeClient client = new IntakeClient();
    final String SERVICE_PATH_COMPONENT = "intakes";
    private String knownObjectId = null;


    // ---------------------------------------------------------------
    // CRUD tests : CREATE tests
    // ---------------------------------------------------------------

    // Success outcomes
    
    @Override
    @Test
    public void create() {

        // Perform setup, such as initializing the type of service request
        // (e.g. CREATE, DELETE), its valid and expected status codes, and
        // its associated HTTP method name (e.g. POST, DELETE).
        setupCreate();

        // Submit the request to the service and store the response.
        String identifier = createIdentifier();
        Intake intake = createIntake(identifier);
        ClientResponse<Response> res = client.create(intake);
        int statusCode = res.getStatus();

        // Check the status code of the response: does it match the expected response(s)?
        //
        // Does it fall within the set of valid status codes?
        // Does it exactly match the expected status code?
        verbose("create: status = " + statusCode);
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);

        // Store the ID returned from this create operation for additional tests below.
        knownObjectId = extractId(res);
    }

    @Override
    @Test(dependsOnMethods = {"create"})
    public void createList() {
        for(int i = 0; i < 3; i++){
            create();
        }
    }

    // Failure outcomes

    @Override
    @Test(dependsOnMethods = {"create"}, expectedExceptions = IllegalArgumentException.class)
    public void createNull() {
        ClientResponse<Response> res = client.create(null);
    }
    
    // Placeholders until the two tests below can be uncommented.  See Issue CSPACE-401.
    public void createWithMalformedXml() {}
    public void createWithWrongXmlSchema() {}

/*
    @Override
    @Test(dependsOnMethods = {"create", "testSubmitRequest"})
    public void createWithMalformedXml() {
    
        // Perform setup.
        setupCreateWithMalformedXml();

        // Submit the request to the service and store the response.
        String method = REQUEST_TYPE.httpMethodName();
        String url = getServiceRootURL();
        final String entity = MALFORMED_XML_DATA; // Constant from abstract base class.
        int statusCode = submitRequest(method, url, entity);
        
        // Check the status code of the response: does it match the expected response(s)?
        verbose("createWithMalformedXml url=" + url + " status=" + statusCode);
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    @Override
    @Test(dependsOnMethods = {"create", "testSubmitRequest"})
    public void createWithWrongXmlSchema() {
    
        // Perform setup.
        setupCreateWithWrongXmlSchema();
     
        // Submit the request to the service and store the response.
        String method = REQUEST_TYPE.httpMethodName();
        String url = getServiceRootURL();
        final String entity = WRONG_XML_SCHEMA_DATA;
        int statusCode = submitRequest(method, url, entity);
        
        // Check the status code of the response: does it match the expected response(s)?
        verbose("createWithWrongSchema url=" + url + " status=" + statusCode);
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }
*/

    // ---------------------------------------------------------------
    // CRUD tests : READ tests
    // ---------------------------------------------------------------

    // Success outcomes

    @Override
    @Test(dependsOnMethods = {"create"})
    public void read() {
    
        // Perform setup.
        setupRead();

        // Submit the request to the service and store the response.
        ClientResponse<Intake> res = client.read(knownObjectId);
        int statusCode = res.getStatus();
            
        // Check the status code of the response: does it match the expected response(s)?
        verbose("read: status = " + statusCode);
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    @Override
    @Test(dependsOnMethods = {"read"})
    public void readNonExistent() {

        // Perform setup.
        setupReadNonExistent();
        
        // Submit the request to the service and store the response.
        ClientResponse<Intake> res = client.read(NON_EXISTENT_ID);
        int statusCode = res.getStatus();

        // Check the status code of the response: does it match the expected response(s)?
        verbose("readNonExistent: status = " + res.getStatus());
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }


    // ---------------------------------------------------------------
    // CRUD tests : READ_LIST tests
    // ---------------------------------------------------------------

    // Success outcomes

    @Override
    @Test(dependsOnMethods = {"createList"})
    public void readList() {
    
        // Perform setup.
        setupReadList();

        // Submit the request to the service and store the response.
        ClientResponse<IntakeList> res = client.readList();
        IntakeList list = res.getEntity();
        int statusCode = res.getStatus();

        // Check the status code of the response: does it match the expected response(s)?
        verbose("readList: status = " + res.getStatus());
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);

        // Optionally output additional data about list members for debugging.
        boolean iterateThroughList = false;
        if (iterateThroughList && logger.isDebugEnabled()) {
            List<IntakeList.IntakeListItem> items =
                list.getIntakeListItem();
            int i = 0;
            for(IntakeList.IntakeListItem item : items){
                verbose("readList: list-item[" + i + "] csid=" + item.getCsid());
                verbose("readList: list-item[" + i + "] objectNumber=" + item.getEntryNumber());
                verbose("readList: list-item[" + i + "] URI=" + item.getUri());
                i++;
            }
        }
        
    }

    // Failure outcomes
    
    // None at present.


    // ---------------------------------------------------------------
    // CRUD tests : UPDATE tests
    // ---------------------------------------------------------------

    // Success outcomes

    @Override
    @Test(dependsOnMethods = {"create"})
    public void update() {
    
        // Perform setup.
        setupUpdate();

        // Retrieve an existing resource that we can update.
        ClientResponse<Intake> res = client.read(knownObjectId);
        verbose("read: status = " + res.getStatus());
        Assert.assertEquals(res.getStatus(), EXPECTED_STATUS_CODE);
        Intake intake = res.getEntity();
        verbose("Got object to update with ID: " + knownObjectId,
                intake, Intake.class);

        // Update the content of this resource.
        intake.setEntryNumber("updated-" + intake.getEntryNumber());
        intake.setEntryDate("updated-" + intake.getEntryDate());
    
        // Submit the request to the service and store the response.
        res = client.update(knownObjectId, intake);
        int statusCode = res.getStatus();
        Intake updatedObject = res.getEntity();

        // Check the status code of the response: does it match the expected response(s)?
        verbose("update: status = " + res.getStatus());
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
        
        // Check the contents of the response: does it match what was submitted?
        verbose("update: ", updatedObject, Intake.class);
        Assert.assertEquals(updatedObject.getEntryDate(), 
            intake.getEntryDate(), 
            "Data in updated object did not match submitted data.");
    }

    // Placeholders until the two tests below can be uncommented.  See Issue CSPACE-401.
    public void updateWithMalformedXml() {}
    public void updateWithWrongXmlSchema() {}

/*
    @Override
    @Test(dependsOnMethods = {"create", "update", "testSubmitRequest"})
    public void updateWithMalformedXml() {

        // Perform setup.
        setupUpdateWithMalformedXml();

        // Submit the request to the service and store the response.
        String method = REQUEST_TYPE.httpMethodName();
        String url = getResourceURL(knownObjectId);
        final String entity = MALFORMED_XML_DATA; // Constant from abstract base class.
        int statusCode = submitRequest(method, url, entity);
        
        // Check the status code of the response: does it match the expected response(s)?
        verbose("updateWithMalformedXml: url=" + url + " status=" + statusCode);
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    @Override
    @Test(dependsOnMethods = {"create", "update", "testSubmitRequest"})
    public void updateWithWrongXmlSchema() {
    
        // Perform setup.
        setupUpdateWithWrongXmlSchema();
        
        // Submit the request to the service and store the response.
        String method = REQUEST_TYPE.httpMethodName();
        String url = getResourceURL(knownObjectId);
        final String entity = WRONG_XML_SCHEMA_DATA; // Constant from abstract base class.
        int statusCode = submitRequest(method, url, entity);
        
        // Check the status code of the response: does it match the expected response(s)?
        verbose("updateWithWrongSchema: url=" + url + " status=" + statusCode);
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }
*/

    @Override
    @Test(dependsOnMethods = {"update", "testSubmitRequest"})
    public void updateNonExistent() {

        // Perform setup.
        setupUpdateNonExistent();

        // Submit the request to the service and store the response.
        // Note: The ID used in this 'create' call may be arbitrary.
        // The only relevant ID may be the one used in update(), below.
        Intake intake = createIntake(NON_EXISTENT_ID);
        ClientResponse<Intake> res =
          client.update(NON_EXISTENT_ID, intake);
        int statusCode = res.getStatus();

        // Check the status code of the response: does it match the expected response(s)?
        verbose("updateNonExistent: status = " + res.getStatus());
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    // ---------------------------------------------------------------
    // CRUD tests : DELETE tests
    // ---------------------------------------------------------------

    // Success outcomes

    @Override
    @Test(dependsOnMethods = 
        {"create", "read", "update"})
    public void delete() {

        // Perform setup.
        setupDelete();

        // Submit the request to the service and store the response.
        ClientResponse<Response> res = client.delete(knownObjectId);
        int statusCode = res.getStatus();

        // Check the status code of the response: does it match the expected response(s)?
        verbose("delete: status = " + res.getStatus());
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    // Failure outcomes

    @Override
    @Test(dependsOnMethods = {"delete"})
    public void deleteNonExistent() {

        // Perform setup.
        setupDeleteNonExistent();

        // Submit the request to the service and store the response.
        ClientResponse<Response> res = client.delete(NON_EXISTENT_ID);
        int statusCode = res.getStatus();

        // Check the status code of the response: does it match the expected response(s)?
        verbose("deleteNonExistent: status = " + res.getStatus());
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }


    // ---------------------------------------------------------------
    // Utility tests : tests of code used in tests above
    // ---------------------------------------------------------------

    /**
     * Tests the code for manually submitting data that is used by several
     * of the methods above.
     */
    @Test(dependsOnMethods = {"create", "read"})
    public void testSubmitRequest() {

        // Expected status code: 200 OK
        final int EXPECTED_STATUS_CODE = Response.Status.OK.getStatusCode();

        // Submit the request to the service and store the response.
        String method = ServiceRequestType.READ.httpMethodName();
        String url = getResourceURL(knownObjectId);
        int statusCode = submitRequest(method, url);
        
        // Check the status code of the response: does it match the expected response(s)?
        verbose("testSubmitRequest: url=" + url + " status=" + statusCode);
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);

    }		

    // ---------------------------------------------------------------
    // Utility methods used by tests above
    // ---------------------------------------------------------------

    @Override
    public String getServicePathComponent() {
        // @TODO Determine if it is possible to obtain this
        // value programmatically.
        //
        // We set this in an annotation in the CollectionObjectProxy
        // interface, for instance.  We also set service-specific
        // constants in each service module, which might also
        // return this value.
        return SERVICE_PATH_COMPONENT;
    }
    
    private Intake createIntake(String identifier) {
        Intake intake =
            createIntake(
                "entryNumber-" + identifier,
                "entryDate-" + identifier);
        return intake;
    }
    
    private Intake createIntake(String entryNumber, String entryDate) {
        Intake intake = new Intake();
        intake.setEntryNumber(entryNumber);
        intake.setEntryDate(entryDate);
        return intake;
    }

}

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.collectionspace.services.client.CollectionSpaceClient;
import org.collectionspace.services.client.BlobClient;
import org.collectionspace.services.jaxb.AbstractCommonList;
import org.collectionspace.services.blob.BlobCommon;
import org.collectionspace.services.blob.BlobCommonList;

import org.jboss.resteasy.client.ClientResponse;

import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BlobServiceTest, carries out tests against a deployed and running Blob Service. <p/>
 * $LastChangedRevision:  $
 * $LastChangedDate:  $
 */
public class BlobServiceTest extends AbstractServiceTestImpl {

    private final String CLASS_NAME = BlobServiceTest.class.getName();
    private final Logger logger = LoggerFactory.getLogger(CLASS_NAME);
    final String SERVICE_PATH_COMPONENT = "blob";
    private String knownResourceId = null;

    @Override
    protected CollectionSpaceClient getClientInstance() {
        return new BlobClient();
    }

    @Override
    protected AbstractCommonList getAbstractCommonList(ClientResponse<AbstractCommonList> response) {
        return response.getEntity(BlobCommonList.class);
    }

    @Override
    @Test(dataProvider = "testName", dataProviderClass = AbstractServiceTestImpl.class)
    public void create(String testName) throws Exception {
        logger.debug(testBanner(testName, CLASS_NAME));
        setupCreate();
        BlobClient client = new BlobClient();
        MultipartOutput multipart = createBlobInstance(createIdentifier());
        ClientResponse<Response> res = client.create(multipart);
        assertStatusCode(res, testName);
        if (knownResourceId == null) {
            knownResourceId = extractId(res);  // Store the ID returned from the first resource created for additional tests below.
            logger.debug(testName + ": knownResourceId=" + knownResourceId);
        }
        allResourceIdsCreated.add(extractId(res)); // Store the IDs from every resource created by tests so they can be deleted after tests have been run.
    }

    @Override
    @Test(dataProvider = "testName", dataProviderClass = AbstractServiceTestImpl.class, dependsOnMethods = {"create"})
    public void createList(String testName) throws Exception {
        logger.debug(testBanner(testName, CLASS_NAME));
        for (int i = 0; i < 3; i++) {
            create(testName);
        }
    }

    @Override
    @Test(dataProvider = "testName", dataProviderClass = AbstractServiceTestImpl.class, dependsOnMethods = {"create"})
    public void read(String testName) throws Exception {
        logger.debug(testBanner(testName, CLASS_NAME));
        setupRead();
        BlobClient client = new BlobClient();
        ClientResponse<MultipartInput> res = client.read(knownResourceId);
        assertStatusCode(res, testName);
        MultipartInput input = (MultipartInput) res.getEntity();
        BlobCommon blob = (BlobCommon) extractPart(input, client.getCommonPartName(), BlobCommon.class);
        Assert.assertNotNull(blob);
    }

    @Override
    @Test(dataProvider = "testName", dataProviderClass = AbstractServiceTestImpl.class, dependsOnMethods = {"createList", "read"})
    public void readList(String testName) throws Exception {
        logger.debug(testBanner(testName, CLASS_NAME));
        setupReadList();
        BlobClient client = new BlobClient();
        ClientResponse<BlobCommonList> res = client.readList();
        BlobCommonList list = res.getEntity();
        assertStatusCode(res, testName);
        if (logger.isDebugEnabled()) {
            List<BlobCommonList.BlobListItem> items = list.getBlobListItem();
            int i = 0;
            for (BlobCommonList.BlobListItem item : items) {
                logger.debug(testName + ": list-item[" + i + "] csid=" + item.getCsid());
                logger.debug(testName + ": list-item[" + i + "] blobNumber=" + item.getExitNumber());
                logger.debug(testName + ": list-item[" + i + "] URI=" + item.getUri());
                i++;
            }
        }
    }

    @Override
    @Test(dataProvider = "testName", dataProviderClass = AbstractServiceTestImpl.class, dependsOnMethods = {"read"})
    public void update(String testName) throws Exception {
        logger.debug(testBanner(testName, CLASS_NAME));
        setupUpdate();
        BlobClient client = new BlobClient();
        ClientResponse<MultipartInput> res = client.read(knownResourceId);
        assertStatusCode(res, testName);
        logger.debug("got object to update with ID: " + knownResourceId);
        MultipartInput input = (MultipartInput) res.getEntity();
        BlobCommon blob = (BlobCommon) extractPart(input, client.getCommonPartName(), BlobCommon.class);
        Assert.assertNotNull(blob);

        blob.setExitNumber("updated-" + blob.getExitNumber());
        logger.debug("Object to be updated:"+objectAsXmlString(blob, BlobCommon.class));
        MultipartOutput output = new MultipartOutput();
        OutputPart commonPart = output.addPart(blob, MediaType.APPLICATION_XML_TYPE);
        commonPart.getHeaders().add("label", client.getCommonPartName());
        res = client.update(knownResourceId, output);
        assertStatusCode(res, testName);
        input = (MultipartInput) res.getEntity();
        BlobCommon updatedBlob = (BlobCommon) extractPart(input, client.getCommonPartName(), BlobCommon.class);
        Assert.assertNotNull(updatedBlob);
    }

    @Override
    @Test(dataProvider = "testName", dataProviderClass = AbstractServiceTestImpl.class, dependsOnMethods = {"update", "testSubmitRequest"})
    public void updateNonExistent(String testName) throws Exception {
        logger.debug(testBanner(testName, CLASS_NAME));
        setupUpdateNonExistent();
        // Submit the request to the service and store the response.
        // Note: The ID used in this 'create' call may be arbitrary.
        // The only relevant ID may be the one used in update(), below.
        BlobClient client = new BlobClient();
        MultipartOutput multipart = createBlobInstance(NON_EXISTENT_ID);
        ClientResponse<MultipartInput> res = client.update(NON_EXISTENT_ID, multipart);
        assertStatusCode(res, testName);
    }

    @Override
    @Test(dataProvider = "testName", dataProviderClass = AbstractServiceTestImpl.class, dependsOnMethods = {"create", "readList", "testSubmitRequest", "update"})
    public void delete(String testName) throws Exception {
        logger.debug(testBanner(testName, CLASS_NAME));
        setupDelete();
        BlobClient client = new BlobClient();
        ClientResponse<Response> res = client.delete(knownResourceId);
        assertStatusCode(res, testName);
    }

    // ---------------------------------------------------------------
    // Failure outcome tests : means we expect response to fail, but test to succeed
    // ---------------------------------------------------------------

    // Failure outcome
    @Override
    @Test(dataProvider = "testName", dataProviderClass = AbstractServiceTestImpl.class, dependsOnMethods = {"read"})
    public void readNonExistent(String testName) throws Exception {
        logger.debug(testBanner(testName, CLASS_NAME));
        setupReadNonExistent();
        BlobClient client = new BlobClient();
        ClientResponse<MultipartInput> res = client.read(NON_EXISTENT_ID);
        assertStatusCode(res, testName);
    }

    // Failure outcome
    @Override
    @Test(dataProvider = "testName", dataProviderClass = AbstractServiceTestImpl.class, dependsOnMethods = {"delete"})
    public void deleteNonExistent(String testName) throws Exception {
        logger.debug(testBanner(testName, CLASS_NAME));
        setupDeleteNonExistent();
        BlobClient client = new BlobClient();
        ClientResponse<Response> res = client.delete(NON_EXISTENT_ID);
        assertStatusCode(res, testName);
    }

    // Failure outcomes
    // Placeholders until the tests below can be implemented. See Issue CSPACE-401.

    @Override
    public void createWithEmptyEntityBody(String testName) throws Exception {
    }

    @Override
    public void createWithMalformedXml(String testName) throws Exception {
    }

    @Override
    public void createWithWrongXmlSchema(String testName) throws Exception {
    }

    @Override
    public void updateWithEmptyEntityBody(String testName) throws Exception {
    }

    @Override
    public void updateWithMalformedXml(String testName) throws Exception {
    }

    @Override
    public void updateWithWrongXmlSchema(String testName) throws Exception {
    }

    // ---------------------------------------------------------------
    // Utility tests : tests of code used in tests above
    // ---------------------------------------------------------------

    @Test(dependsOnMethods = {"create", "read"})
    public void testSubmitRequest() {
        final int EXPECTED_STATUS = Response.Status.OK.getStatusCode(); // Expected status code: 200 OK
        String method = ServiceRequestType.READ.httpMethodName();
        String url = getResourceURL(knownResourceId);
        int statusCode = submitRequest(method, url);
        logger.debug("testSubmitRequest: url=" + url + " status=" + statusCode);
        Assert.assertEquals(statusCode, EXPECTED_STATUS);
    }

    // ---------------------------------------------------------------
    // Utility methods used by tests above
    // ---------------------------------------------------------------

    @Override
    public String getServicePathComponent() {
        return SERVICE_PATH_COMPONENT;
    }

    private MultipartOutput createBlobInstance(String exitNumber) {
        String identifier = "blobNumber-" + exitNumber;
        BlobCommon blob = new BlobCommon();
        blob.setExitNumber(identifier);
        blob.setDepositor("urn:cspace:org.collectionspace.demo:orgauthority:name(TestOrgAuth):organization:name(Northern Climes Museum)'Northern Climes Museum'");
        MultipartOutput multipart = new MultipartOutput();
        OutputPart commonPart = multipart.addPart(blob, MediaType.APPLICATION_XML_TYPE);
        commonPart.getHeaders().add("label", new BlobClient().getCommonPartName());

        if (logger.isDebugEnabled()) {
            logger.debug("to be created, blob common");
            logger.debug(objectAsXmlString(blob, BlobCommon.class));
        }

        return multipart;
    }
}
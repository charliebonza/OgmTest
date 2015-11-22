package ogm.test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import ogm.test.domain.OwnedLicensedEntity;
import ogm.test.domain.User;
import ogm.test.persistence.OwnedLicensedEntityRepository;
import ogm.test.persistence.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.data.neo4j.repository.support.GraphRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.http.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MultipleRelationshipsToSameEntityType {

	// for username password neo4j NEO4J
	public static final String AUTH_HEADER_VALUE = "bmVvNGo6TkVPNEo=";

	private static final Client client = Client.create();

	private static final String USER_NAME = "USER_001";

	private static final String NEW_OWNEDLICENSEDENTITY_IDENTIFIER = "THREE";

	private OwnedLicensedEntityRepository ownedLicensedEntityRepository;
	private UserRepository userRepository;

	@Before
	public void setUp() {
		RepositoryFactorySupport factory = new GraphRepositoryFactory(
				new SessionFactory("ogm.test.domain")
						.openSession(getNeo4jDatabaseUrl())
		);
		// create repositories programmatically
		ownedLicensedEntityRepository = factory.getRepository(OwnedLicensedEntityRepository.class);
		userRepository = factory.getRepository(UserRepository.class);

		// clear the database
		initGraph();

		// set up the nodes and relationships for the test
		executeCypher(getCypherTransactionImmediateCommitUrl(), getSetUpCypher());
	}

	@Test
	public void multipleRelationshipsBetweenTheSameEntityTypesNotUpdatedAsExpected() {

		// VALIDATE THE INITIAL GRAPH
		// there should be no LICENSEE relationships but include them in the check anyway to show the issue
		assertSamegraph("{  \n" +
				"\"cypher\": \"CREATE " +
				"(ol1:OwnedLicensedEntity {identifier:'ONE'}), " +
				"(ol2:OwnedLicensedEntity {identifier:'TWO'}), " +
				"(user:User {name:'" + USER_NAME + "'}), " +
				"(ol1)-[r1:OWNER]->(user), " +
				"(ol2)-[r2:OWNER]->(user)\",\n" +
				"\"node\": \"hasLabel('OwnedLicensedEntity') || hasLabel('User')\",\n" +
				"\"relationship\": \"isType('OWNER') || isType('LICENSEE')\"" +
				"}");

		// Find the User node using its String identifier
		User user = userRepository.findByName(USER_NAME);
		assertNotNull(user);
		assertEquals(USER_NAME, user.getName());

		// Create a new OwnedLicensedEntity object
		OwnedLicensedEntity ownedLicensedEntity = new OwnedLicensedEntity();
		ownedLicensedEntity.setIdentifier(NEW_OWNEDLICENSEDENTITY_IDENTIFIER);
		// and create a relationship between ownedLicensedEntity and the User node - the OWNER relationship
		ownedLicensedEntity.setOwner(user);
		// NOTE: the user (owner) is NOT added to the licensees collection
		// and save ownedLicensedEntity
		OwnedLicensedEntity saved = ownedLicensedEntityRepository.save(ownedLicensedEntity);

		// Check the saved version of the ownedLicensedEntity.
		assertEquals(user.getId(), saved.getOwner().getId());

		assertSamegraph("{  \n" +
				"\"cypher\": \"CREATE " +
				"(ol1:OwnedLicensedEntity {identifier:'ONE'}), " +
				"(ol2:OwnedLicensedEntity {identifier:'TWO'}), " +
				"(ol3:OwnedLicensedEntity {identifier:'THREE'}), " +
				"(user:User {name:'" + USER_NAME + "'}), " +
				"(ol1)-[r1:OWNER]->(user), " +
				"(ol2)-[r2:OWNER]->(user), " +
				"(ol3)-[r3:OWNER]->(user)\",\n" +
				"\"node\": \"hasLabel('OwnedLicensedEntity') || hasLabel('User')\",\n" +
				"\"relationship\": \"isType('OWNER') || isType('LICENSEE')\"" +
				"}");
	}

	/**
	 * Clear the datastore
 	 */
	private void initGraph() {

		WebResource webResource = client.resource(getRestTestUrl() + "clear");

		ClientResponse response = webResource
				.type("text/plain")
				.header("Authorization", AUTH_HEADER_VALUE)
				.post(ClientResponse.class, "");
		if (response.getStatus() != 200) {
			throw new RuntimeException("Could not clear the database " + response.getStatus());
		}

		webResource = client.resource(getRestTestUrl() + "assertEmpty");

		response = webResource
				.type("application/json")
				.header("Authorization", AUTH_HEADER_VALUE)
				.post(ClientResponse.class, "");

		if (response.getStatus() != 200) {
			throw new RuntimeException("Database is not empty " + response.getStatus());
		}
	}

	private String getRestTestUrl() {
		return "http://localhost:7575/graphaware/resttest/";
	}

	private String getCypherTransactionImmediateCommitUrl() {
		return "http://localhost:7575/db/data/transaction/commit";
	}

	private String getNeo4jDatabaseUrl() {
		return "http://neo4j:NEO4J@localhost:7575";
	}

	private void assertSubgraph(String cypher) {
		executeCypher(getRestTestUrl() + "assertSubgraph", cypher);
	}

	public void assertSamegraph(String cypher) {
		executeCypher(getRestTestUrl() + "assertSameGraph", cypher);
	}

	private void executeCypher(String endpoint, String cypher) {

		try {
			WebResource webResource = client.resource(endpoint);

			ClientResponse response = webResource
					.type(MediaType.APPLICATION_JSON_VALUE)
					.header("Authorization", AUTH_HEADER_VALUE)
					.header("Accept", MediaType.APPLICATION_JSON_VALUE)
					.post(ClientResponse.class, cypher);

			if (response.getStatus() != 200) {
				Assert.fail(response.getEntity(String.class));
			}
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	private String getSetUpCypher() {
		return "{\n" +
				"\"statements\" : [ {\n" +
				"\"statement\" : \"CREATE " +
				"(ol1:OwnedLicensedEntity {identifier: 'ONE'}), " +
				"(ol2:OwnedLicensedEntity {identifier: 'TWO'}), " +
				"(u:User {name:'" + USER_NAME + "'}), " +
				"(ol1)-[:OWNER]->(u), " +
				"(ol2)-[:OWNER]->(u) " +
				"RETURN ol1,ol2,u;\"\n" +
				"} ]\n" +
				"}";
	}


}
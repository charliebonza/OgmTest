package ogm.test.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Multiple entities in the system may have similar owner / licensee relationships
 *
 * Owned Entity - has a single owner (a User)
 * Can have 0...n Licensees (also Users). Owner cannot also be a licensee.
 */
@NodeEntity
public class OwnedLicensedEntity {

	@GraphId
	private Long id;

	private String identifier;

	@Relationship(type = "OWNER", direction = Relationship.OUTGOING)
	private User owner;

	@Relationship(type = "LICENSEE", direction = Relationship.OUTGOING)
	private Set<User> licensees;

	public Set<User> getLicensees() {
		return licensees;
	}

	public void setLicensees(Set<User> licensees) {
		this.licensees = licensees;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}

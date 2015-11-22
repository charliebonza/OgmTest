package ogm.test.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Owner and / or licensee
 */
@NodeEntity
public class User {

	@GraphId
	private Long id;

	private String name;

	@Relationship(type = "OWNER", direction = Relationship.INCOMING)
	private Set<OwnedLicensedEntity> owned;

	@Relationship(type = "LICENSEE", direction = Relationship.INCOMING)
	private Set<OwnedLicensedEntity> licensed;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<OwnedLicensedEntity> getLicensed() {
		return licensed;
	}

	public void setLicensed(Set<OwnedLicensedEntity> licensed) {
		this.licensed = licensed;
	}

	public Set<OwnedLicensedEntity> getOwned() {
		return owned;
	}

	public void setOwned(Set<OwnedLicensedEntity> owned) {
		this.owned = owned;
	}
}

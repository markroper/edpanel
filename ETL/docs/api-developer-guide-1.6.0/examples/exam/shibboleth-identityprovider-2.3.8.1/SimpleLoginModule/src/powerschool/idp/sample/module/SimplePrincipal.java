package powerschool.idp.sample.module;

import java.security.Principal;

/**
 * <p> This class implements the <code>Principal</code> interface
 * and represents a Sample user.
 *
 * <p> Principals such as this <code>SimplePrincipal</code>
 * may be associated with a particular <code>Subject</code>
 * to augment that <code>Subject</code> with an additional
 * identity.  Refer to the <code>Subject</code> class for more information
 * on how to achieve this.  Authorization decisions can then be based upon 
 * the Principals associated with a <code>Subject</code>.
 * 
 * @see java.security.Principal
 * @see javax.security.auth.Subject
 */
public class SimplePrincipal implements Principal, java.io.Serializable {

	private String name;

	public SimplePrincipal(String name) {
		if (name == null) throw new NullPointerException("illegal null input");
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return("SimplePrincipal:  " + name);
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (this == o)
			return true;

		if (!(o instanceof SimplePrincipal))
			return false;
		SimplePrincipal that = (SimplePrincipal)o;

		if (this.getName().equals(that.getName()))
			return true;
		return false;
	}

	public int hashCode() {
		return name.hashCode();
	}
}

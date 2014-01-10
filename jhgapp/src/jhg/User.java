package jhg;

/**
 * DOC
 * @author John
 *
 */
public class User {


	public static final int MAX_LENGTH = 50;
	
	private int id;
	private String name;
	private String pass;
	private String email;

	public static final String AUX_USER_ROLE = "aux_user_role";

	public static final String AUX_USER = "aux_user";

	public static final String PASSFLD = "a_paswd";
	public static final String USERFLD = "a_usrnme";
	public static final String EMAILFLD = "a_email";
	
	
	/**
	 * DOC
	 * @param _username
	 * @param _password
	 */
	public User(String _username, String _password) {
		//TODO validate _username length, password etc
		id = 0;
		this.name = _username;
		this.pass = _password;
	}	
	
	/**
	 * DOC
	 */
	public User(){
		super();
	}
	
	/**
	 * DOC
	 * 
	 * @return
	 */
	public boolean isValid(){
		boolean f = false;
		if(name!=null && pass!=null){
			f = true;
		}
		return f;
	}
	
	
	/**
	 * DOC
	 * 
	 * @param id
	 * @param u
	 * @param p
	 */
	public void init(Integer idInt, String u, String p){
		id = idInt;
		name = u;
		pass = p;
	}
	
	/**
	 * DOC
	 * @return
	 */
	public Integer getId(){
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * DOC
	 * @return
	 */
	public String getMaskedPassword(){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<pass.length();i++){
			sb.append("*");
		}
		return sb.toString();
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pass == null) ? 0 : pass.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pass == null) {
			if (other.pass != null)
				return false;
		} else if (!pass.equals(other.pass))
			return false;
		return true;
	}
	
	
	
}


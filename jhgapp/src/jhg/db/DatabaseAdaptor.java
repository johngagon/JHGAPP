package jhg.db;

import java.sql.ResultSet;
import java.util.List;

import jhg.ApplicationException;
import jhg.ModelResult;

/**
 * This interface allows for executing database specific
 * @author John
 *
 */
public interface DatabaseAdaptor {

	public void fill(ResultSet rs);

	public ModelResult fillSingle(ResultSet rs);

	public List<ModelResult> fillSelectAll(ResultSet rs) throws ApplicationException;

}

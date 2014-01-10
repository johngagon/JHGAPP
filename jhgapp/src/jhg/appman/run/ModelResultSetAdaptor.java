package jhg.appman.run;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jhg.ApplicationException;
import jhg.Base;
import jhg.ModelResult;
import jhg.Result;
import jhg.State;
import jhg.appman.SqlTranslator;
import jhg.db.DatabaseAdaptor;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Model;

/*
import jhg.adaptor.core.IField;
import jhg.adaptor.core.IModel;
import jhg.adaptor.core.IValue;
import jhg.adaptor.persistence.ResultSetAdaptor;
import jhg.jmodel.Application;
import jhg.jmodel.Manager;
import jhg.jmodel.Model;
*/

/**
 * One other class with mutual knowledge of jmodel and jdbc.
 * (ResultSetJModelAdaptor)
 * @author John
 *
 */
@SuppressWarnings("rawtypes")
public class ModelResultSetAdaptor extends Base implements DatabaseAdaptor {

	/**
	 * 
	 * @param msg
	 */
	public static void debug(String msg){
		System.out.println("ModelResultSetAdaptor:"+msg);
	}	
	
	private Manager manager;

	/**
	 * 
	 * @param __manager
	 */
	public ModelResultSetAdaptor(Manager __manager) {
		this.manager = __manager;
	}

	/*
	 * (non-Javadoc)
	 * @see jhg.adaptor.persistence.ResultSetAdaptor#fill(java.sql.ResultSet)
	 */
	@Override
	public void fill(ResultSet rs)  {
		
		/*
		int found = 0;
		if(manager!=null){
			List<Field> fields = manager.getFields();
			
			try {
				while(rs.next()){
					Model m = manager.createModel();//it will add it
					//
					//ResultSetMetaData rsmd = rs.getMetaData();
					//int colcount = rsmd.getColumnCount();
					//for(int i =0;i<colcount;i++){
					//	String colname = rsmd.getColumnLabel(i+1);
					//	IField field = manager.getField(colname);
					//	IValue value = field.makeValue(m, rs.getString(field.getName()));
					//	m.updateForLoad(field, value);
					//}
					for(IField field:fields){
						String fieldname = field.getName();
						String strvalue = rs.getString(fieldname);
						IValue value = field.makeValue(m, strvalue);
						//manager.application.log("field:"+fieldname+"["+field.getClass().getName()+"]=strvalue:"+strvalue+",IValue:"+value.toString());
						m.updateForLoad(field, value);
					}
					m.postLoad();		
					manager.addModel(m);
					found++;
				}
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}else{
			throw new IllegalStateException("Manager null!");
		}
		Application.log("ResultSetAdaptor::records found:"+found);//TODO system print
		*/
	}

	/*
	 * (non-Javadoc)
	 * @see jhg.adaptor.persistence.ResultSetAdaptor#fillSingle(java.sql.ResultSet)
	 */
	@Override
	public ModelResult fillSingle(ResultSet rs) {
		ModelResult m = null;
		/*
		int found = 0;
		
		if(manager!=null){
			LinkedHashSet<IField> fields = manager.getFields();
			try {
				if(rs.next()){
					m = manager.createModel();//it will add it
					for(IField field:fields){
						IValue value = field.makeValue(m, rs.getString(field.getName()));
						m.updateForSelect(field, value);
					}
					m.postLoad();		
					if(manager.usesCache()){
						manager.addModel(m);
					}
					found++;
				}
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}else{
			throw new IllegalStateException("Manager null!");
		}
		System.out.println("ResultSetAdaptor::records found:"+found);//TODO system print
		return m;
		*/
		return m;
	}

	//NOTE: this method is only used by load.
	@SuppressWarnings("unchecked")
	@Override
	public List<ModelResult> fillSelectAll(ResultSet rs) throws ApplicationException {
		List<ModelResult> modelResultList = new ArrayList<ModelResult>();
		int found = 0;
		if(manager!=null){
			List<Field> fields = manager.getFields();
			try {
				while(rs.next()){
					ModelResult modelResult = new ModelResult();
					Model _model = manager.makeModel();
					Result _valueResult = new Result();
					_model.setId(rs.getInt("ID"));//RESULTSET
					_model.setVersionId(rs.getInt("VERSION_ID"));
					_model.setOwner(rs.getInt("OWNER_ID"));
					_model.setApprover(rs.getInt("APPROVER_ID"));
					int stateId = rs.getInt("STATE");
					State modelState = State.values()[stateId];
					_model.setState(modelState);
					/* This will be handled in cache 
					Integer openById = rs.getInt("OPEN_BY_ID");
					Date openDate = rs.getDate(columnIndex)
					_model.reserve(rs.getInt("OPEN_BY_ID"),rs.getDate("OPEN_DATE"));
					*/
					
					for(Field field:fields){
						String fieldname = field.getName();
						String strvalue = rs.getString(fieldname);//RESULTSET
						if(strvalue!=null){
							_valueResult = _model.setValue(fieldname, strvalue);
							//if the value result is not successful, log it
							if(!_valueResult.isSuccessful()){
								log("Result for setValue("+fieldname+","+strvalue+") failed:"+_valueResult.getReason().name());
								log("  Messages:"+_valueResult.allMessages());
							}
							modelResult.addResult(_valueResult);	
						}
						log("field:"+fieldname+"["+field.getClass().getName()+"]=strvalue:"+strvalue);
					}
					manager.load(_model);
					modelResult.setModel(_model);
					modelResultList.add(modelResult);
					found++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			throw new IllegalStateException("Manager null!");
		}
		log("Records found:"+found);
		log("Manager model count:"+manager.countModels());
		return modelResultList;
	}
	
	protected void log(String s){
		String msg = "ModelResultSetAdaptor: "+s;
		super.log(msg);
	}

	
}


package jhg.z;

/**
 * The phone number is a very special class and since it can hold special characters
 * or digits as letters and is not a quantity, we can store it best as a text field.
 * 
 * http://www.itu.int/rec/T-REC-E.123-200102-I/e
 * 
 * We then want options for masking various entries, storing in a unified format
 * and then formatting back out as well as concatenating multiple fields.
 *
 * Possible inputs to parse:
 * 
 * Storage format text: 
 * 
 * Formatted output:
 * 
 * Logic: 
 *   getField(AREA_CODE/trunk):
 *       trunk/area code: in parens, doesn't need to be dailed in zone.
 *       
 *   getScope(): Local|National|International (isInternational)
 *   getCountry(): (default US)
 *   getType(): (directory number, operator, line)
 *   getGenre(): (mobile, fax, tel (landline), modem?)
 *   "411" types. 
 *   "0" operator.
 *   
 *   validate(Scope[]allowed,Type[]allows) e.g.: must supply minimum set of fields.
 *       Field[])
 *   Formats  
 *   		N_PARENHYPHEN  (302)123-4568 
 *   		N_DOT          302.123.4567
 *   		N_PARENONLY      (302)1234567
 *          I_STANDARD     +13021234567
 *          I_TEL          Tel. +22 607 123 4567 (spaces only)
 *          (ITU)
 *          ext. 876  
 *          ext. 8*33
 *          
 *   parse(Format expected,String):PhoneNumber		  
 *   
 *   format(Format):  //(302)1234567
 *   
 *   International 
 *   +22 607 1234 5678
 *   +1  302 123 4567
 *   
 *   extensions
 *   
 *   Home,Work,Mobile,Other
 * 
 * @author John
 *
 */
public class PhoneNumber {


	
	
}

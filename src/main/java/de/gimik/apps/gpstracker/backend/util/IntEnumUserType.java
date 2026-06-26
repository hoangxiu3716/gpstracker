package de.gimik.apps.gpstracker.backend.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Generic class to map a JDK 1.5 enums to a INT column in the DB.
 * Beware that the enums.ordinal() relates to the ORDERING of the enums values, so, if
 * your change it later on, all your DB values will return an incorrect value!
 * 
 * @author Benoit Xhenseval
 * @version 1
 */
public class IntEnumUserType<E extends Enum<E>> implements UserType {
    private Class<E> clazz = null;
    private E[] theEnumValues;
    
    /**
     * Contrary to the example mapping to a VARCHAR, this would
     * @param c the class of the enums.
     * @param e The values of enums (by invoking .values()).
     */
    protected IntEnumUserType(Class<E> c, E[] e) { 
        this.clazz = c; 
        this.theEnumValues = e;
    } 
 
    private static final int[] SQL_TYPES = {Types.INTEGER};
    
    /**
     * simple mapping to a SMALLINT.
     */
    public int[] sqlTypes() { 
        return SQL_TYPES; 
    } 
 
    public Class<E> returnedClass() { 
        return clazz; 
    } 

    /**
     * From the INT in the DB, get the enums.  Because there is no
     * Enum.valueOf(class,int) method, we have to iterate through the given enums.values()
     * in order to find the correct "int".
     */
    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) 
        throws HibernateException, SQLException {
        final int val = resultSet.getInt(names[0]);
        E result = null;
        if (!resultSet.wasNull()) {
            try {
                for(int i=0; i < theEnumValues.length && result == null; i++) {
                    if (((Enumerable)theEnumValues[i]).value() == val) {
                        result = theEnumValues[i];
                    }
                }
            } catch (SecurityException e) {
                result = null;
            } catch (IllegalArgumentException e) {
                result = null;
            }
        } 
        return result; 
    } 

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        return nullSafeGet(rs, names, owner);
    }
 
    /**
     * set the SMALLINT in the DB based on enums.ordinal() value, BEWARE this
     * could change.
     */
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException {
        if (null == value) { 
            preparedStatement.setNull(index, Types.INTEGER); 
        } else { 
            preparedStatement.setInt(index, ((Enumerable)value).value());
        } 
    } 

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        nullSafeSet(st, value, index);
    }
 
    public Object deepCopy(Object value) throws HibernateException {
        return value; 
    } 
 
    public boolean isMutable() { 
        return false; 
    } 
 
    public Object assemble(Serializable cached, Object owner)
        throws HibernateException {
         return cached;
    } 

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable)value; 
    } 
 
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original; 
    } 
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode(); 
    } 
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) 
            return true; 
        if (null == x || null == y) 
            return false; 
        return x.equals(y); 
    } 
} 
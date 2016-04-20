package com.jenkov.db.impl.mapping;

import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.IPersistenceConfiguration;
import com.jenkov.db.itf.mapping.*;
import com.jenkov.db.util.ClassUtil;

import java.lang.reflect.Method;
//import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.util.*;

/**
 * @author Jakob Jenkov,  Jenkov Development
 */
public class ObjectMapper implements IObjectMapper{

    protected IDbNameGuesser                    nameGuesser                 = new DbNameGuesser   ();
    protected IDbNameDeterminer                 nameDeterminer              = new DbNameDeterminer();
    protected IDbPrimaryKeyDeterminer           primaryKeyDeterminer        = new DbPrimaryKeyDeterminer();
//    protected CustomObjectMapperAnnotationBased annotationBasedCustomMapper = null;

    protected IObjectMappingFactory objectMappingFactory = null;

    public ObjectMapper(IObjectMappingFactory objectMappingFactory) {
        this.objectMappingFactory = objectMappingFactory;
//        this.annotationBasedCustomMapper = new CustomObjectMapperAnnotationBased(this.objectMappingFactory);
    }

    public IDbPrimaryKeyDeterminer getDbPrimaryKeyDeterminer() {
        return this.primaryKeyDeterminer;
    }

    public void setDbPrimaryKeyDeterminer(IDbPrimaryKeyDeterminer primaryKeyDeterminer) {
        this.primaryKeyDeterminer = primaryKeyDeterminer;
    }

    public IDbNameGuesser getDbNameGuesser() {
        return this.nameGuesser;
    }

    public void setDbNameGuesser(IDbNameGuesser guesser) {
        this.nameGuesser = guesser;
    }

    public IDbNameDeterminer getDbNameDeterminer() {
        return this.nameDeterminer;
    }

    public void setDbNameDeterminer(IDbNameDeterminer nameDeterminer) {
        this.nameDeterminer = nameDeterminer;
    }

    public IObjectMapping getObjectMapping(Object objectMappingKey, IPersistenceConfiguration configuration, Connection connection)
            throws PersistenceException{

        //has object mapping cached for this key already?
        if(configuration.getObjectMappingCache() != null &&
                configuration.getObjectMappingCache().containsObjectMapping(objectMappingKey)){
            return configuration.getObjectMappingCache().getObjectMapping(objectMappingKey);
        }

        IObjectMapping      mapping      = null;
        ICustomObjectMapper customMapper = null;
        if(objectMappingKey instanceof ObjectMappingKey){
            customMapper = ((ObjectMappingKey) objectMappingKey).getCustomObjectMapper();
        }
        if(customMapper == null){
            customMapper = configuration.getCustomObjectMapper();
        }

        try{
            if(customMapper != null){
                //has custom object mapping?
                mapping = customMapper.getObjectMapping(objectMappingKey);
                if(mapping != null){  return mapping; }

                validateObjectMappingKey(objectMappingKey);

                mapping = getObjectMappingFromAnnotations(objectMappingKey, configuration, connection);
                if(mapping != null){ return mapping; }

                //has table name to map object to? tableName = null --> table name will be guessed during auto mapping.
                String tableName = customMapper.getTableName(objectMappingKey);
                if(tableName == null) tableName = getTableNameFromAnnotation(objectMappingKey);
                mapping = generateObjectMapping(objectMappingKey, tableName, connection);

                //need to modify the auto generated object mapping?
                if(mapping != null){
                    createOrModifyMethodMappingsFromAnnotations(mapping, configuration, connection);
                    customMapper.modify(objectMappingKey,  mapping);
                }
            } else {
                validateObjectMappingKey(objectMappingKey);

                mapping = getObjectMappingFromAnnotations(objectMappingKey, configuration, connection);
                if(mapping != null) return mapping;

                //has table name to map object to? tableName = null --> table name will be guessed during auto mapping.
                String tableName = getTableNameFromAnnotation(objectMappingKey);
                mapping = generateObjectMapping(objectMappingKey, tableName, connection);

                //need to modify the auto generated object mapping?
                if(mapping != null){
                    createOrModifyMethodMappingsFromAnnotations(mapping, configuration, connection);
                }
            }

            if(mapping == null){
                throw new PersistenceException("No object mapping stored in the object mapping cache for the" +
                    "object mapping key: " + objectMappingKey + ", and no object mapping could be generated for it either.");
            }

            return mapping;

       } finally{
            if(mapping != null && configuration.getObjectMappingCache() != null) {
                configuration.getObjectMappingCache().storeObjectMapping(objectMappingKey, mapping);
            }
        }

    }

    private void validateObjectMappingKey(Object objectMappingKey) throws PersistenceException {
        if(objectMappingKey instanceof ObjectMappingKey){
            if(((ObjectMappingKey)objectMappingKey).getObjectClass() == null){
                throw new PersistenceException("No object mapping was stored for the given IObjectMappingKey instance, " +
                        "and it contained no object class (was null), so a mapping cannot be generated.");
            }
        }
    }

    private IObjectMapping generateObjectMapping(Object objectMappingKey, String tableName, Connection connection) throws PersistenceException {
        IObjectMapping mapping = null;
        if(objectMappingKey instanceof Class){
            return createObjectMapping((Class) objectMappingKey, tableName, connection);
        }

        if(objectMappingKey instanceof ObjectMappingKey){
            if(((ObjectMappingKey) objectMappingKey).getObjectClass() != null){
                return createObjectMapping(((ObjectMappingKey) objectMappingKey).getObjectClass(), tableName, connection);
            }
        }
        return null;
    }

    protected IObjectMapping createObjectMapping(Class theClass, String tableName, Connection connection)
    throws PersistenceException{
        return mapToTable(theClass, null, connection, null, tableName);
    }


    public IObjectMapping mapToTable(Class objectClass, IObjectMapping objectMapping, Connection connection,
                                     String databaseName, String table) throws PersistenceException {

        return mapGettersToTable(
                objectClass,
                mapSettersToTable(objectClass, objectMapping, connection, databaseName, table),
                connection,
                databaseName,
                table);
    }

    public IObjectMapping mapGettersToTable(Class objectClass, IObjectMapping objectMapping,
                                            Connection connection, String databaseName, String table) throws PersistenceException {

        objectMapping  = assureValidObjectMapping(objectClass, objectMapping, connection, databaseName, table);

        Method[] methods = objectMapping.getObjectClass().getMethods();
        for(int i=0; i < methods.length; i++){
            if(!ClassUtil.isGetter(methods[i])) continue;
            if(methods[i].getName().equals("getClass")) continue;

            Collection possibleNames = this.nameGuesser.getPossibleColumnNames(methods[i]);
            String dbFieldName       = this.nameDeterminer.determineColumnName(possibleNames, objectMapping.getTableName(), connection);

            if(dbFieldName != null) {
                IGetterMapping fieldMapping = this.objectMappingFactory.createGetterMapping(methods[i], dbFieldName, true);
                if(fieldMapping != null){
                    fieldMapping.setColumnType(this.nameDeterminer.getColumnType(dbFieldName, objectMapping.getTableName()));
                    objectMapping.addGetterMapping(fieldMapping);
                }
            }
        }
        return objectMapping;
    }




    public IObjectMapping mapSettersToTable(Class objectClass, IObjectMapping objectMapping,
                                            Connection connection, String databaseName, String table) throws PersistenceException {
        objectMapping  = assureValidObjectMapping(objectClass, objectMapping, connection, databaseName, table);

        Set filteredSetters = getFilteredSetters(objectMapping.getObjectClass());
        Iterator iterator = filteredSetters.iterator();
        while(iterator.hasNext()){
            Method setterMethod = (Method) iterator.next();

            Collection possibleNames = this.nameGuesser.getPossibleColumnNames(setterMethod);
            String dbFieldName       = this.nameDeterminer.determineColumnName(possibleNames,
                                        objectMapping.getTableName(), connection);

            if(dbFieldName != null) {
                ISetterMapping fieldMapping = this.objectMappingFactory.createSetterMapping(setterMethod, dbFieldName, true);
                if(fieldMapping != null){
                    fieldMapping.setColumnType(this.nameDeterminer.getColumnType(dbFieldName, objectMapping.getTableName()));
                    objectMapping.addSetterMapping(fieldMapping);
                }
            }
        }
        return objectMapping;  //To change body of implemented methods use File | Settings | File Templates.
    }



    public IObjectMapping mapSettersToSelf(Class persistentObjectClass, IObjectMapping objectMapping)
    throws PersistenceException{
        if(objectMapping == null){
            objectMapping = this.objectMappingFactory.createObjectMapping();
            objectMapping.setObjectClass(persistentObjectClass);
        }

        Set filteredSetters = getFilteredSetters(objectMapping.getObjectClass());
        Iterator iterator = filteredSetters.iterator();
        while(iterator.hasNext()){
            Method setterMethod = (Method) iterator.next();

            ISetterMapping fieldMapping =
                    this.objectMappingFactory.createSetterMapping(setterMethod, setterMethod.getName(), false);
            if(fieldMapping != null){
                objectMapping.addSetterMapping((ISetterMapping) fieldMapping);
            }
        }

        return objectMapping;
    }

    private IObjectMapping assureValidObjectMapping(Class objectClass, IObjectMapping objectMapping,
                                                    Connection connection, String databaseName, String table)
    throws PersistenceException{
        if(objectMapping == null){
            objectMapping = this.objectMappingFactory.createObjectMapping();
        }
        assureValidObjectClass          (objectMapping, objectClass);
        assureValidTableName            (objectMapping, table, connection);
        assureValidPrimaryKeyColumnName(objectMapping, databaseName, connection);

        return objectMapping;
    }


    private void assureValidTableName(IObjectMapping objectMapping, String table, Connection connection)
    throws PersistenceException {
        if(table == null && objectMapping.getTableName() != null){
            return;
        }
        if(table != null && objectMapping.getTableName() == null){
            objectMapping.setTableName(table);
            return;
        }
        if(table != null && objectMapping.getTableName() != null){
            if(!table.equals(objectMapping.getTableName())){
                throw new PersistenceException("Two different table names provided for object method for class " +
                        objectMapping.getObjectClass().getName() + ". Table name '" + table + "' passed as " +
                        "parameter doesn't match with table name '" + objectMapping.getTableName() +
                        "'  found in the provided object method");
            } else {
                return;
            }
        }

        table = this.nameDeterminer.determineTableName(
                this.nameGuesser.getPossibleTableNames(objectMapping.getObjectClass()),
                null,
                connection );
        if(table != null) {
            objectMapping.setTableName(table);
            return;
        }


        throw new PersistenceException("No table found matching class " + objectMapping.getObjectClass());
    }

    private void assureValidPrimaryKeyColumnName(IObjectMapping objectMapping, String databaseName, Connection connection) throws PersistenceException {
        if(objectMapping.getPrimaryKey().getTable() == null){
            objectMapping.setPrimaryKey(this.primaryKeyDeterminer
                    .getPrimaryKeyMapping(objectMapping.getTableName(), databaseName, connection));
        }
        // todo remove this primary key backward compatiblity call in v. 4.0.0
        //assurePrimaryKeyBackwardCompatibility(objectMapping);
        /**
        if(objectMapping.getPrimaryKeyColumnName() == null){
            objectMapping.setPrimaryKeyColumnName(
                this.primaryKeyDeterminer.getPrimaryKeyColumnName(objectMapping.getTableName(), databaseName, connection));
        }*/
    }

    /**
     * This method assures that if anyone is / was using the old primary key mechanisms directly,
     * and not indirectly throught the AbstractDao, the primary key will still look sensible, and
     * can still be used.
     * todo Remove this primary key backward compatibility in v. 4.0.0
     * @param objectMapping The object mapping to assure still has the primary key denoted the
     * old fashioned way.
     */

    /*
    private void assurePrimaryKeyBackwardCompatibility(IObjectMapping objectMapping) {
        if(objectMapping.getPrimaryKey().getColumns().size() == 1){
            Iterator iterator = objectMapping.getPrimaryKey().getColumns().iterator();
            objectMapping.setPrimaryKeyColumnName((String) iterator.next());
        }
    }
    */

    private void assureValidObjectClass(IObjectMapping mapping, Class persistentObjectClass) throws PersistenceException {
        if(persistentObjectClass == null && mapping.getObjectClass() == null){
            throw new PersistenceException("No class provided in either parameter or inside object method");
        }
        if(persistentObjectClass == null && mapping.getObjectClass() != null){
            return;
        }
        if(persistentObjectClass != null && mapping.getObjectClass() == null){
            mapping.setObjectClass(persistentObjectClass);
            return ;
        }

        if(!persistentObjectClass.equals(mapping.getObjectClass())){
            throw new PersistenceException("The object class passed as parameter ("
                    +  persistentObjectClass.getName()
                    +  ") and the object class found in "
                    +  "the provided object method ("
                    +  mapping.getObjectClass().getName()
                    +  ") did not match. They must be the same, or only one of them should be provided. "
                    +  "You can leave out either of them (set to null) and the one present will be used.");
        }
    }

    /**
     * This method retrieves the setters of a class. It also filters out overloaded setters
     * (setters with same name but different parameter types),
     * so there is only one setter present of a set of overloaded setters. The setter present
     * will be the setter which has the same parameter type as the return type of the matching getter.
     * If there is no matching getter the first setter of a set of overloaded setters will be used.
     *
     * @param objectClass The class to extract the setters from.
     * @return The setters of the given class minus extra overloaded setters not matching the getters.
     */
    private Set<Method> getFilteredSetters(Class objectClass){
        Map      getters           = new HashMap();
        Map      settersOverloaded = new HashMap();
        Set<Method> filteredSetters   = new HashSet<Method>();
        Method[] methods           = objectClass.getMethods();

        for(int i=0; i<methods.length; i++){
            if(ClassUtil.isGetter(methods[i])){
                getters.put(methods[i].getName(), methods[i]);
            } else if (ClassUtil.isSetter(methods[i])){
                if(settersOverloaded.get(methods[i].getName()) == null){
                    settersOverloaded.put(methods[i].getName(), new ArrayList());
                }
                ((List) settersOverloaded.get(methods[i].getName())).add(methods[i]);
            }
        }

        Iterator setterNames = settersOverloaded.keySet().iterator();
        while(setterNames.hasNext()){
            String setterName = (String) setterNames.next();
            List<Method> matchingOverloadedSetters = (List<Method>) settersOverloaded.get(setterName);
            if(matchingOverloadedSetters.size() == 1){
                filteredSetters.add(matchingOverloadedSetters.get(0));
            } else {
                String matchingGetterName = "g" + setterName.substring(1);
                Method matchingGetter     = (Method) getters.get(matchingGetterName);
                if(matchingGetter == null){
                    filteredSetters.add(matchingOverloadedSetters.get(0));
                } else {
                    Method setterMatchingGetter = null;
                    Iterator iterator = matchingOverloadedSetters.iterator();
                    while(iterator.hasNext()){
                        Method setter = (Method) iterator.next();
                        if(matchingGetter.getReturnType().equals(setter.getParameterTypes()[0])){
                            setterMatchingGetter = setter;
                        }
                    }
                    if(setterMatchingGetter != null){
                        filteredSetters.add(setterMatchingGetter);
                    } else {
                        Iterator possibleSetters = matchingOverloadedSetters.iterator();
                        while(possibleSetters.hasNext()){
                            Method method = (Method) possibleSetters.next();
                            if(this.objectMappingFactory.createSetterMapping(method, "", false) != null){
                                filteredSetters.add(method);
                                break;
                            }
                        }
                    }
                }
            }
        }
        getters.clear();
        settersOverloaded.clear();
        return filteredSetters;
    }



    public IObjectMapping getObjectMappingFromAnnotations(Object objectMappingKey, IPersistenceConfiguration configuration,
                                           Connection connection) throws PersistenceException {
        if(!(objectMappingKey instanceof ObjectMappingKey || objectMappingKey instanceof Class)) return null;
        Class targetClass = objectMappingKey instanceof Class? (Class) objectMappingKey : ((ObjectMappingKey) objectMappingKey).getClass() ;

        AClassMapping classMapping = (AClassMapping) targetClass.getAnnotation(AClassMapping.class);
        if(classMapping != null && "manual".equals(classMapping.mappingMode())){
            IObjectMapping mapping = this.objectMappingFactory.createObjectMapping();
            mapping.setObjectClass(targetClass);
            if(isSet(classMapping.tableName())) mapping.setTableName(classMapping.tableName());
            createOrModifyMethodMappingsFromAnnotations(mapping, configuration, connection);
            return mapping;
        }
        return null;
    }

    public String getTableNameFromAnnotation(Object objectMappingKey) throws PersistenceException {
        if(!(objectMappingKey instanceof ObjectMappingKey || objectMappingKey instanceof Class)) return null;
        Class targetClass = objectMappingKey instanceof Class? (Class) objectMappingKey : ((ObjectMappingKey) objectMappingKey).getObjectClass() ;

        AClassMapping classMapping = (AClassMapping) targetClass.getAnnotation(AClassMapping.class);
        if(classMapping != null && isSet(classMapping.tableName())){
            return classMapping.tableName();
        }

        return null;
    }

    public void createOrModifyMethodMappingsFromAnnotations(IObjectMapping mapping, IPersistenceConfiguration configuration,
                                             Connection connection) throws PersistenceException {
        Method[] methods = mapping.getObjectClass().getMethods();
        for(Method method : methods){
            if (ClassUtil.isGetter(method)) {
                AGetterMapping getterAnnotation = (AGetterMapping) method.getAnnotation(AGetterMapping.class);
                if(getterAnnotation != null){
                    IGetterMapping getterMapping = mapping.getGetterMapping(method);
                    if(getterMapping == null){
                        getterMapping = this.objectMappingFactory.createGetterMapping(method, null, false);
                    }
                    if(isSet(getterAnnotation.columnName())) {
                        getterMapping.setColumnName (getterAnnotation.columnName());
                    }
                    getterMapping.setObjectMethod(method);
                    getterMapping.setAutoGenerated(getterAnnotation.databaseGenerated());
                    getterMapping.setTableMapped  (getterAnnotation.includeInWrites());
                    if(isSet(getterAnnotation.columnType())){
                        getterMapping.setColumnType   (translateColumnType(method, getterAnnotation.columnType()));
                    }
                    if(mapping.getGetterMapping(method) == null){
                        mapping.addGetterMapping(getterMapping);
                    }
                }
            }
        }

        Set<Method> filteredSetters = getFilteredSetters(mapping.getObjectClass());
        for(Method method: filteredSetters){
            ASetterMapping setterAnnotation = (ASetterMapping) method.getAnnotation(ASetterMapping.class);
            if(setterAnnotation != null){
                ISetterMapping setterMapping = mapping.getSetterMapping(method);
                if(setterMapping == null){
                    setterMapping = this.objectMappingFactory.createSetterMapping(method, null, false);
                }
                if(isSet(setterAnnotation.columnName())){
                    setterMapping.setColumnName(setterAnnotation.columnName());
                }
                setterMapping.setObjectMethod(method);
                if(isSet(setterAnnotation.columnType())){
                    setterMapping.setColumnType(translateColumnType(method, setterAnnotation.columnType()));
                }
                if(mapping.getSetterMapping(method) == null){
                    mapping.addSetterMapping(setterMapping);
                }
            }

        }
    }


    private int translateColumnType(Method method, String columnType) {
        if("number".equals(columnType)){
            return java.sql.Types.NUMERIC;
        }
        if("string".equals(columnType)){
            return java.sql.Types.VARCHAR;
        }
        if("date".equals(columnType)){
            return java.sql.Types.TIMESTAMP;
        }
        if("binary".equals(columnType)){
            return java.sql.Types.BLOB;
        }
        throw new IllegalArgumentException("Annotation mapping error in method: " + method.getClass().getName()
                + "." + method.getName() + "(). 'columnType' mapping was " + columnType +
                ". The annotation 'columnType' must have one of the values: number, string, date, binary");
    }

    protected boolean isSet(String value){
        return !"".equals(value);
    }


}

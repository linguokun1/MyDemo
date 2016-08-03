package com.sponia.daogenerator;
import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;
public class MyDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(8, "com.sponia.opyfunctioindemo.dao");

//        addUndoEvent(schema);
        addUser(schema);
        addStep(schema);
        new DaoGenerator().generateAll(schema, "./../Opy_Functioin_Demo/app/src/main/java");
    }

    private static void addUser(Schema schema) {

        Entity record = schema.addEntity("User");
        record.addStringProperty("id").primaryKey().notNull();
        record.addStringProperty("name");
        record.addStringProperty("sex").notNull();
        record.addIntProperty("weight");
        record.addIntProperty("today_steps");
    }

    private static void addStep(Schema schema) {
        Entity record = schema.addEntity("Step");
        record.addStringProperty("id").primaryKey().notNull();
        record.addStringProperty("userId");
        record.addStringProperty("date").notNull();
        record.addFloatProperty("stepCount");
        record.addFloatProperty("acceleration");
        record.addStringProperty("status");
    }
}
